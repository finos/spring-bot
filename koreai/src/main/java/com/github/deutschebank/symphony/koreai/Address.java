package com.github.deutschebank.symphony.koreai;

public class Address {

	private final Long userId;
	private final String firstName; 
	private final String lastName;
	private final String email;
	private final String roomStreamID;
	
	public Address(Long userId, String firstName, String lastName, String email, String roomStreamID) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.roomStreamID = roomStreamID;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getRoomStreamID() {
		return roomStreamID;
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public String toString() {
		return "Address [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", roomStreamID=" + roomStreamID + "]";
	}


}
