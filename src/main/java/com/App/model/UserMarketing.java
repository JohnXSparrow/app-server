package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
public class UserMarketing {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_usermarketing;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;
	
	@Type(type = "true_false")
	@Column(nullable = false)
	private boolean wasSent;
	
	@Type(type = "true_false")
	@Column(nullable = false)
	private boolean isUnsubscribe;
	
	@Column(nullable = false)
	private String tokenToUnsubscribe;

	public long getId_usermarketing() {
		return id_usermarketing;
	}

	public void setId_usermarketing(long id_usermarketing) {
		this.id_usermarketing = id_usermarketing;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isWasSent() {
		return wasSent;
	}

	public void setWasSent(boolean wasSent) {
		this.wasSent = wasSent;
	}

	public boolean isUnsubscribe() {
		return isUnsubscribe;
	}

	public void setUnsubscribe(boolean isUnsubscribe) {
		this.isUnsubscribe = isUnsubscribe;
	}

	public String getTokenToUnsubscribe() {
		return tokenToUnsubscribe;
	}

	public void setTokenToUnsubscribe(String tokenToUnsubscribe) {
		this.tokenToUnsubscribe = tokenToUnsubscribe;
	}

}
