package org.finos.springbot.teams.data;

import com.fasterxml.jackson.databind.JsonNode;

public interface DataTransport<X> {

	/**
	 * Retrieves the data payload of a message from an adaptive card's JSON Definition.
	 */
	public X retrieveFromCard(JsonNode adaptiveCard);
	
	/**
	 * Retrieves the data payload of a message from an adaptive card's JSON Definition.
	 */
	public X retrieveFromCard(String adaptiveCard);
	
	/**
	 * Retrieves the data payload of a message from it's xml body.
	 */
	public X retrieveFromXML(String xmlBody);
	
	/**
	 * Adds the data to the outgoing adaptive card
	 */
	public void introduceIntoCard(JsonNode adaptiveCard, X data);
	
	/**
	 * Adds the data to the outgoing xml
	 */
	public void introduceIntoXML(String xml, X data);
	
}
