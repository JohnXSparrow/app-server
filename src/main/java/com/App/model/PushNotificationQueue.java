package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
public class PushNotificationQueue {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_pushnotificationqueue;

	@NotNull
	private long userLogin;

	@NotNull
	private long gameMatch;

	@Column
	@NotNull
	@Type(type = "true_false")
	private boolean wasSent;

	public long getId_pushnotificationqueue() {
		return id_pushnotificationqueue;
	}

	public void setId_pushnotificationqueue(long id_pushnotificationqueue) {
		this.id_pushnotificationqueue = id_pushnotificationqueue;
	}

	public long getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(long userLogin) {
		this.userLogin = userLogin;
	}

	public long getGameMatch() {
		return gameMatch;
	}

	public void setGameMatch(long gameMatch) {
		this.gameMatch = gameMatch;
	}

	public boolean isWasSent() {
		return wasSent;
	}

	public void setWasSent(boolean wasSent) {
		this.wasSent = wasSent;
	}

	public PushNotificationQueue(@NotNull long userLogin, @NotNull long gameMatch, @NotNull boolean wasSent) {
		this.userLogin = userLogin;
		this.gameMatch = gameMatch;
		this.wasSent = wasSent;
	}

	public PushNotificationQueue() {
		super();
	}	

}
