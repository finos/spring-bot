package com.github.deutschebank.symphony.stream.lq;

public interface LeaderOnlyQueue<X, ID> {

	void accept(X t);

	void makeLeader();

	void noLongerLeader();
	
	void remove(ID i);

}