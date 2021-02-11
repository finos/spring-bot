package org.finos.symphony.toolkit.stream.cluster.voting;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteRequest;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteResponse;

/**
 * Invokes the "win" method on the client if a majority of votes arrive 
 * for this member of the cluster.  This means clusters must be of odd-sizes,
 * to ensure there is always a majority to be had.
 * 
 * @author robmoffat
 */
public class MajorityDecider implements Decider {
	
	private final Participant self;
	
	public MajorityDecider(Participant self) {
		super();
		this.self = self;
	}
	
	class MajorityConsumer implements Consumer<ClusterMessage> {
		
		int votes = 1;
		boolean finished = false;
		Runnable r;
		ClusterMember cm;
		
		public MajorityConsumer(ClusterMember cm, int votes, boolean finished, Runnable r) {
			super();
			this.votes = votes;
			this.finished = finished;
			this.r = r;
			this.cm = cm;
			checkWin();
		}

		@Override
		public void accept(ClusterMessage t) {
			if ((t instanceof VoteResponse) && (((VoteResponse) t).getCandidate().equals(self))) {
				votes += ((VoteResponse) t).getVotes();
				checkWin();
			}
		}
		
		protected void checkWin() {
			if (votes > (cm.getSizeOfCluster() / 2f)) {
				if (!finished) {
					finished = true;
					r.run();
				}
			}
		}
	}
		
	

	@Override
	public Consumer<ClusterMessage> createDecider(ClusterMember cm, Runnable r) {
		return new MajorityConsumer(cm, 1, false, r);
	}

	@Override
	public Participant voteFor(VoteRequest vr) {
		return vr.getCandidate();
	}

	@Override
	public boolean canSuppressWith(ClusterMember cm, SuppressionMessage sm) {
		return true;
	}
}
