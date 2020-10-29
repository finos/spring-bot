package org.finos.symphony.toolkit.stream.cluster;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteRequest;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteResponse;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal implementation of Raft's Cluster Member, where 
 * @author robmoffat
 *
 */
public class RaftClusterMember implements ClusterMember {
	
	public static Logger LOG = LoggerFactory.getLogger(RaftClusterMember.class);

	protected final Participant self;
	protected final long timeoutMs;

	/**
	 * The role this member is playing in the cluster.
	 */
	protected State state;

	/**
	 * Keeps track of who has been voted for in a given round.
	 */
	protected long electionNumber = 0;
	protected Participant votedFor;

	/**
	 * Keeps track of when the leader last called to say it was alive
	 */
	protected long lastPingTimeMs;

	/**
	 * Thread that times progress, and invokes actions when the timeouts occur.
	 */
	protected Thread timer;
	
	/**
	 * Sets up the algorithm for choosing the election winner
	 */
	protected Decider decider;
	
	/**
	 * Used for commmunicating with the rest of the cluster.
	 */
	protected Multicaster multicaster;

	public RaftClusterMember(Participant self, long timeoutMs, Decider d, Multicaster multicaster) {
		super();
		this.self = self;
		this.timeoutMs = timeoutMs;
		this.state = State.STOPPED;
		this.decider = d;
		this.multicaster = multicaster;
	}

	@Override
	public void startup() {
		state = State.SUPRESSED;
		timer = new Thread(() -> doListenOperation());
		timer.setDaemon(true);
		timer.setName("ClusterMember");
		timer.start();
	}

	protected void doListenOperation() {
		long nextSleepTime = timeoutMs;
		while (state != State.STOPPED) {
			try {
				Thread.sleep(nextSleepTime);
			} catch (InterruptedException e) {
			}

			LOG.debug("{} waking", self);

			if (state == State.SUPRESSED) {
				long timeNow = System.currentTimeMillis();
				long elapsedSinceLastPing = timeNow - lastPingTimeMs;
				if (elapsedSinceLastPing > timeoutMs) {
					// no recent pings - hold an election
					holdElection();
					nextSleepTime = timeoutMs;
				} else {
					nextSleepTime = timeoutMs - elapsedSinceLastPing;
				}
			} else if (state == State.PROPOSING_ELECTION) {
				// if this is still the case, then the last election didn't go to plan.
				// try again
				holdElection();
				nextSleepTime = timeoutMs;
			} else if (state == State.LEADER) {
				checkPing();
				nextSleepTime = (timeoutMs / 2);
			}

			LOG.debug("{} sleeping for {}ms", self, nextSleepTime);
		}

	}

	@Override
	public synchronized void shutdown() {
		try {
			state = State.STOPPED;
			timer.interrupt();
			while (timer.isAlive()) {
				Thread.sleep(timeoutMs);
			}
		} catch (InterruptedException ie) {
		}
	}

	public synchronized void receivePing(SuppressionMessage sm) {
		if (state == State.STOPPED) {
			return;
		}
			
		if (sm.getElectionNumber() >= electionNumber) {
			electionNumber = sm.getElectionNumber();
			LOG.debug("{} received {}", self, sm);
			
			if (decider.canSuppressWith(sm)) {
				if (state == State.LEADER) {
					LOG.debug("{} stepping down due to {}", self, sm);
				}
				
				lastPingTimeMs = System.currentTimeMillis();
				state = State.SUPRESSED;
			}
		}
	}

	public synchronized void holdElection() {
		if (state == State.STOPPED) {
			return;
		}
		
		state = State.PROPOSING_ELECTION;
		electionNumber++;
		votedFor = self;
		LOG.debug("{} holding election {} ", self, electionNumber);
		
		Consumer<ClusterMessage> vc = decider.createDecider(() -> {
			becomeLeader();
			checkPing();
		});
		
		if (vc != null) {
			multicaster.sendAsyncMessage(self, new VoteRequest(electionNumber, self), vc);
		}
	}

	protected float getVotes() {
		return 1;
	}

	public synchronized VoteResponse receiveVoteRequest(VoteRequest vr) {
		if (electionNumber < vr.getElectionNumber()) {
			electionNumber = vr.getElectionNumber();
			votedFor = decider.voteFor(vr);
			lastPingTimeMs = System.currentTimeMillis();
		}

		LOG.debug("{} voting for {} in election {}", self, votedFor, electionNumber);

		return new VoteResponse(electionNumber, votedFor, getVotes());
	}

	protected void checkPing() {
		if (state == State.STOPPED) {
			return;
		}
		
		long timeNow = System.currentTimeMillis();
		long elapsedSinceLastPing = timeNow - lastPingTimeMs;

		if (elapsedSinceLastPing > timeoutMs / 2) {
			lastPingTimeMs = timeNow;
			LOG.debug("{} sending ping", self);
			multicaster.sendAsyncMessage(self, new SuppressionMessage(self, electionNumber), c -> {});
		} else {
			LOG.debug("{} omitting ping", self);
		}
	}

	

	@Override
	public synchronized void becomeLeader() {
		this.state = State.LEADER;
	}

	@Override
	public Participant getSelfDetails() {
		return self;
	}

	@Override
	public State getState() {
		return this.state;
	}

	@Override
	public ClusterMessage receiveMessage(ClusterMessage cm) {
		if (cm instanceof SuppressionMessage) {
			receivePing((SuppressionMessage) cm);
			return null;
		} else if (cm instanceof VoteRequest) {
			return receiveVoteRequest((VoteRequest) cm);
		} else {
			throw new UnsupportedOperationException("Unknown message type: "+cm.getClass());
		}
	}

}
