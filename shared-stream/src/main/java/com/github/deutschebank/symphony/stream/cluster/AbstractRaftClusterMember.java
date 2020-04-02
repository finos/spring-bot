package com.github.deutschebank.symphony.stream.cluster;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;
import com.github.deutschebank.symphony.stream.cluster.voting.VoteCounter;
import com.github.deutschebank.symphony.stream.msg.Participant;

public abstract class AbstractRaftClusterMember implements ClusterMember {

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
	 * Keeps track of when the leader last called to say it was alive
	 */
	protected long lastPingTimeMs;

	/**
	 * Thread that times progress, and invokes actions when the timeouts occur.
	 */
	protected Thread timer;

	public AbstractRaftClusterMember(String memberName, Participant self, long timeoutMs) {
		super();
		this.self = self;
		this.timeoutMs = timeoutMs;
		this.state = State.STOPPED;
		this.memberName = memberName;
	}

	@Override
	public void startup() {
		state = State.SUPRESSED;
		timer = new Thread(() -> doListenOperation());
		timer.setDaemon(true);
		timer.setName("SymphonyClusterMember: " + memberName);
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
				nextSleepTime = timeoutMs / 2;
			}
		}
		System.out.println(self+" sleeping for "+timeoutMs);

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

	@Override
	public synchronized void receivePing(SuppressionMessage sm) {
		if (state == State.STOPPED) {
			return;
		}
			
		if (sm.getElectionNumber() >= electionNumber) {
			System.out.println(self + " received " + sm);
			if (state == State.LEADER) {
				System.out.println(self + " stepping down due to " + sm);
			}
			
			lastPingTimeMs = System.currentTimeMillis();
			state = State.SUPRESSED;
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
		VoteCounter vc = new VoteCounter(getClusterSize(), self, getVotes()) {

			@Override
			protected void win() {
				becomeLeader();
				checkPing();
			}

		};
		sendAsyncMessage(new VoteRequest(electionNumber, self), vc);
	}

	protected float getVotes() {
		return 1;
	}

	@Override
	public synchronized VoteResponse receiveVoteRequest(VoteRequest vr) {
		if (electionNumber < vr.getElectionNumber()) {
			electionNumber = vr.getElectionNumber();
			votedFor = vr.getCandidate();
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
			sendAsyncMessage(new SuppressionMessage(self, electionNumber), c -> {
			});
		}
	}

	protected abstract <R extends ClusterMessage> void sendAsyncMessage(ClusterMessage cm,
			Consumer<R> responsesConsumer);

	protected abstract int getClusterSize();

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

}
