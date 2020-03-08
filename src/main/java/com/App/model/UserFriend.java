package com.App.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class UserFriend {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_userfriend;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin userLogin;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin friend;

	public long getId_userfriend() {
		return id_userfriend;
	}

	public void setId_userfriend(long id_userfriend) {
		this.id_userfriend = id_userfriend;
	}

	@JsonIgnoreProperties({ "user" })
	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	@JsonIgnoreProperties({ "user" })
	public UserLogin getFriend() {
		return friend;
	}

	public void setFriend(UserLogin friend) {
		this.friend = friend;
	}

}
