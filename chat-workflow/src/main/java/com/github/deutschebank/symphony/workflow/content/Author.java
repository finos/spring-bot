package com.github.deutschebank.symphony.workflow.content;

/**
 * Declare this class in a workflow object, and it will get populated automatically with the 
 * person submitting the object.
 */
public interface Author extends User {

	/**
	 * Keeps track of who is working on the workflow, so we can get the author details back.
	 */
	public static final ThreadLocal<Author> CURRENT_AUTHOR = new ThreadLocal<>();
}
