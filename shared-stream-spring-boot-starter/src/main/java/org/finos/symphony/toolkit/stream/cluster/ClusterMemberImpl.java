package org.finos.symphony.toolkit.stream.cluster;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal implementation of Raft's Cluster Member, where 
 * @author robmoffat
 *
 */
public class ClusterMemberImpl implements ClusterMember {
	
	public static Logger LOG = LoggerFactory.getLogger(ClusterMemberImpl.class);

	protected final Participant self;
	protected final long timeoutMs;

	/**
	 * Keeps track of when the leader last called to say it was alive
	 */
	protected long lastPingTimeMs;

	/**
	 * Thread that times progress, and invokes actions when the timeouts occur.
	 */
	protected Thread timer;
	
	/**
	 * Used for suppressing the rest of the cluster.
	 */
	protected Multicaster multicaster;
	
	/**
	 * Which bot we are clustering for
	 */
	protected String clusterName;
	
	/**
	 * Allows us to know whether this leader is still active on symphony
	 */
	protected HealthSupplier health;
	
	/**
	 * Allows us to try and become leader, and find out who the leader is
	 */
	protected LeaderService leaderService;
	
	public ClusterMemberImpl(String clusterName, Participant self, long timeoutMs, Multicaster multicaster, HealthSupplier health, LeaderService ls) {
		super();
		this.self = self;
		this.timeoutMs = timeoutMs;
		this.multicaster = multicaster;
		this.clusterName = clusterName;
		this.health = health;
		this.leaderService = ls;
	}

	@Override
	public String getClusterName() {
		return clusterName;
	}

	@Override
	public void startup() {
		timer = new Thread(() -> doListenOperation());
		timer.setDaemon(true);
		timer.setName("ClusterMember-"+clusterName);
		timer.start();
	}

	protected void doListenOperation() {
		long nextSleepTime = timeoutMs;
		State state = getState();
		
		while (state != State.STOPPED) {
			try {
				Thread.sleep(nextSleepTime);
			} catch (InterruptedException e) {
			}
			
			state = getState();

			LOG.debug("{} waking in state {}", self, state);

			if (state == State.SUPRESSED) {
				long timeNow = System.currentTimeMillis();
				long elapsedSinceLastPing = timeNow - lastPingTimeMs;
				if (elapsedSinceLastPing > timeoutMs) {
					if (health.getAsBoolean()) {
						// no recent pings - take over
						becomeLeader();
					}
					
					nextSleepTime = timeoutMs;
				} else {
					nextSleepTime = timeoutMs - elapsedSinceLastPing;
				}
			} else if (state == State.LEADER) {
				if (health.getAsBoolean()) {
					sendSuppressionMessage();
				} else {
					state = State.INOPERABLE;
				}
				nextSleepTime = (timeoutMs / 2);
			} else if (state == State.INOPERABLE) {
				if (health.getAsBoolean()) {
					state = State.SUPRESSED;
				}
				nextSleepTime = timeoutMs;
			}

			LOG.debug("{} sleeping for {}ms", self, nextSleepTime);
		}

	}

	@Override
	public synchronized void shutdown() {
		try {
			Thread temp = timer;
			timer.interrupt();
			timer = null;
			while (temp.isAlive()) {
				Thread.sleep(timeoutMs);
			}
		} catch (InterruptedException ie) {
		}
	}

	public synchronized void receivePing(SuppressionMessage sm) {
		LOG.debug("{} received {}", self, sm);	
		lastPingTimeMs = System.currentTimeMillis();
	}

	protected void sendSuppressionMessage() {
		long timeNow = System.currentTimeMillis();
		long elapsedSinceLastPing = timeNow - lastPingTimeMs;

		if (elapsedSinceLastPing > timeoutMs / 2) {
			lastPingTimeMs = timeNow;
			LOG.debug("{} sending ping", self);
			multicaster.sendAsyncMessage(self, leaderService.getRecentParticipants(), new SuppressionMessage(clusterName, self), c -> {});
		} else {
			LOG.debug("{} omitting ping", self);
		}
	}

	public synchronized void becomeLeader() {
		leaderService.becomeLeader(this.self);
	}

	@Override
	public Participant getSelfDetails() {
		return self;
	}

	@Override
	public State getState() {
		if (timer == null) {
			return State.STOPPED;
		}
		
		if (!health.getAsBoolean()) {
			return State.INOPERABLE;
		}
		
		if (leaderService.isLeader(this.self)) {
			return State.LEADER;
		}
		
		return State.SUPRESSED;
	}

	@Override
	public ClusterMessage receiveMessage(ClusterMessage cm) {
		if (cm instanceof SuppressionMessage) {
			receivePing((SuppressionMessage) cm);
			return null;
		} else {
			throw new UnsupportedOperationException("Unknown message type: "+cm.getClass());
		}
	}


}
