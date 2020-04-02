package com.github.deutschebank.symphony.stream.fixture;

import java.util.HashSet;
import java.util.Set;

import com.github.deutschebank.symphony.stream.msg.Participant;

/**
 * Allows us to model a network where various 
 * components appear and disappear on a network.
 * 
 * @author robmoffat
 *
 */
public class Connectivity {

	Set<Set<Participant>> networks = new HashSet<Set<Participant>>();
		
	public boolean canTalkTo(Participant a, Participant b) {
		for (Set<Participant> nw : networks) {
			if (nw.contains(a) && (nw.contains(b))) {
				return true;
			}
		}
		
		return false;
	}
	
	public void set(Set<Set<Participant>> nw) {
		this.networks = nw;
	}

	public void isolate(Participant p) {
		System.out.println("Isolating "+p);
		for (Set<Participant> set : networks) {
			set.remove(p);
		}
	}

	public void connect(Participant p) {
		System.out.println("Connecting "+p);
		for (Set<Participant> set : networks) {
			set.add(p);
		}
	}
}
