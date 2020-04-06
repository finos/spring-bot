package com.github.deutschebank.symphony.stream.cluster;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;
import com.github.deutschebank.symphony.stream.cluster.transport.Multicaster;
import com.github.deutschebank.symphony.stream.cluster.voting.Decider;

public class RaftClusterMember implements ClusterMember {

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
		timer.setName("SymphonyClusterMemberTimer");
		timer.start();
	}

	protected void doListenOperation() {
		long nextSleepTime = timeoutMs;
		while (state != State.STOPPED) {
			try {
				Thread.sleep(nextSleepTime);
			} catch (InterruptedException e) {
			}

			System.out.println(self + " waking");

			if (state == State.SUPRESSED) {
				long timeNow = System.currentTimeMillis();
				long elapsedSinceLastPing = timeNow - lastPingTimeMs;
				if (elapsedSinceLastPing > timeoutMs) {
					// no recent pings - hold an election
					holdElection();
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

			System.out.println(self+" sleeping for "+nextSleepTime);
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
			System.out.println(self + " received " + sm);
			
			if (decider.canSuppressWith(sm)) {
				if (state == State.LEADER) {
					System.out.println(self + " stepping down due to " + sm);
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
		System.out.println(self + " holding election " + electionNumber);
		
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

		System.out.println(self + " voting for " + votedFor + " in election " + electionNumber);

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
			System.out.println(self+" sending ping");
			multicaster.sendAsyncMessage(self, new SuppressionMessage(self, electionNumber), c -> {});
		} else {
			System.out.println(self+" omitting ping");
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
