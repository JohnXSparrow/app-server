package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class UserCoin {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_usercoin;

	@NotNull
	@Column(nullable = false)
	private long goldCoin;

	@NotNull
	@Column(nullable = false)
	private long silverCoin;

	@JsonIgnore
	@JsonProperty(value = "id_usercoin")
	public long getId_usercoin() {
		return id_usercoin;
	}

	public void setId_usercoin(long id_usercoin) {
		this.id_usercoin = id_usercoin;
	}

	public long getGoldCoin() {
		return goldCoin;
	}

	public void setGoldCoin(long goldCoin) {
		this.goldCoin = goldCoin;
	}

	public long getSilverCoin() {
		return silverCoin;
	}

	public void setSilverCoin(long silverCoin) {
		this.silverCoin = silverCoin;
	}

	public UserCoin() {
		this.goldCoin = 20;
		this.silverCoin = 500;
	}
	
}
