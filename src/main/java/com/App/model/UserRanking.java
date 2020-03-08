package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class UserRanking {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_userranking;

	@ManyToOne(fetch = FetchType.EAGER)
	private UserLogin userLogin;

	@ManyToOne(fetch = FetchType.EAGER)
	private UserStats userStats;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "ranking.id_ranking")
	private Ranking ranking;

	@Column
	private long position;

	@Column
	private long goldReward;

	@Column
	private long silverReward;

	public long getId_userranking() {
		return id_userranking;
	}

	public void setId_userranking(long id_userranking) {
		this.id_userranking = id_userranking;
	}

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public long getGoldReward() {
		return goldReward;
	}

	public void setGoldReward(long goldReward) {
		this.goldReward = goldReward;
	}

	public long getSilverReward() {
		return silverReward;
	}

	public void setSilverReward(long silverReward) {
		this.silverReward = silverReward;
	}

	public UserStats getUserStats() {
		return userStats;
	}

	public void setUserStats(UserStats userStats) {
		this.userStats = userStats;
	}

	public Ranking getRanking() {
		return ranking;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

}