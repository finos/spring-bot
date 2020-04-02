package com.github.deutschebank.symphony.stream.cluster.events;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.AbstractRaftClusterMember;
import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;
import com.github.deutschebank.symphony.stream.cluster.voting.MajorityDecider;
import com.github.deutschebank.symphony.stream.log.SharedLog;

public abstract class AbstractEventDrivenRaftClusterMember<ID> extends AbstractRaftClusterMember<ID> {
	
	protected final SharedLog log; 
	protected final String memberName;
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
	 * Keeps track of events that need responding to
	 */
	protected long lastEventTimeMs = Long.MAX_VALUE;
	protected ID lastEventId;
	protected long lastPingTimeMs;
	
	protected Thread timer;

	public AbstractEventDrivenRaftClusterMember(SharedLog log, String memberName, Participant self, long timeoutMs) {
		super();
		this.log = log;
		this.self = self;
		this.timeoutMs = timeoutMs;
		this.state = State.STOPPED;
		this.memberName = memberName;
	}

	@Override
	public void startup() {
		Optional<Participant> leader = log.getLeader(self);
		if (leader.isPresent()) {
			if (leader.get().equals(self)) {
				state = State.LEADER;
			} else {
				state = State.SUPRESSED;
				log.writeParticipantMessage(self);
			}
		} else {
			// no leader set - so become the leader
			log.writeLeaderMessage(self);
			state = State.LEADER;
		}
		
		Thread timer = new Thread(() -> doListenOperation());
		timer.setDaemon(true);
		timer.setName("SymphonyClusterMember: "+memberName);
		timer.start();
	}

	protected void doListenOperation() {
		long nextSleepTime = timeoutMs;
		while (state != State.STOPPED) {
			try {
				Thread.sleep(nextSleepTime);
			} catch (InterruptedException e) {
			}
			
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
				nextSleepTime = timeoutMs;
			}
		}
	}

	@Override
	public void shutdown() {
		state = State.STOPPED;
		timer.interrupt();
	}

	@Override
	public void receivePing(SuppressionMessage sm) {
		lastPingTimeMs = System.currentTimeMillis();
	}
	
	public synchronized void holdElection() {
		state = State.PROPOSING_ELECTION;
		electionNumber ++;
		votedFor = self;
		MajorityDecider vc = new MajorityDecider(log.getRegisteredParticipants(self).size(), self, getVotes()) {
			
			@Override
			protected void win() {
				log.writeLeaderMessage(self);
			}
			
		};
		sendAsyncMessage(new VoteRequest(electionNumber, self), vc); 
	}

	protected float getVotes() {
		return amIleader() ? 1.5f : 1;
	}

	private boolean amIleader() {
		return self.equals(log.getLeader(self).orElse(null));
	}
	
	@Override
	public synchronized VoteResponse receiveVoteRequest(VoteRequest vr) {
		if (electionNumber < vr.getElectionNumber()) {
			electionNumber = vr.getElectionNumber();
			votedFor = vr.getCandidate();
		}

		return new VoteResponse(electionNumber, votedFor, getVotes());
	}

	@Override
	public void receiveEvent(ID id) {
		lastEventId = id;
		lastEventTimeMs = System.currentTimeMillis();
		
		if (state == State.LEADER) {
			checkPing();
		}
	}

	protected void checkPing() {
		long timeNow = System.currentTimeMillis();
		long elapsedSinceLastPing = timeNow - lastPingTimeMs;
		
		if (elapsedSinceLastPing > timeoutMs / 2) {
			lastPingTimeMs = timeNow;
			sendAsyncMessage(new SuppressionMessage(self), c -> {});
		}
	}

	protected <R extends ClusterMessage> void sendAsyncMessage(ClusterMessage cm, Consumer<R> consumer) {
		for (Participant p : log.getRegisteredParticipants(self)) {
			if (p != self) {
				sendAsyncMessage(p, cm, consumer);
			}
		}
	} 

	protected abstract <REQ extends ClusterMessage, RES extends ClusterMessage> void sendAsyncMessage(Participant to, REQ r, Consumer<RES> consumer);

	@Override
	public synchronized void becomeLeader() {
		this.state = State.LEADER;
	}

	@Override
	public Participant getSelfDetails() {
		return self;
	}
	
}
