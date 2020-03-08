package com.App.model;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.App.enumeration.KickFriendEnum;
import com.App.serializer.CustomCalendarWeekSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class KickFriend {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_kickfriend;

	@NotNull
	@Column(nullable = false)
	private long valueKick;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin challenger;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin challenged;

	@Column
	@Type(type = "true_false")
	private boolean wasProccessed;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private KickFriendEnum status;

	@Column(nullable = false)
	private Calendar dateKick;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private GameMatch gameMatch;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Team teamChallenger;

	@ManyToOne(fetch = FetchType.EAGER)
	private Team teamChallenged;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Coin coin;

	@ManyToOne(fetch = FetchType.EAGER)
	private Result result;

	public long getId_kickfriend() {
		return id_kickfriend;
	}

	public void setId_kickfriend(long id_kickfriend) {
		this.id_kickfriend = id_kickfriend;
	}

	public long getValueKick() {
		return valueKick;
	}

	public void setValueKick(long valueKick) {
		this.valueKick = valueKick;
	}

	@JsonIgnoreProperties({ "user" })
	public UserLogin getChallenger() {
		return challenger;
	}

	public void setChallenger(UserLogin challenger) {
		this.challenger = challenger;
	}

	@JsonIgnoreProperties({ "user" })
	public UserLogin getChallenged() {
		return challenged;
	}

	public void setChallenged(UserLogin challenged) {
		this.challenged = challenged;
	}

	public boolean isWasProccessed() {
		return wasProccessed;
	}

	public void setWasProccessed(boolean wasProccessed) {
		this.wasProccessed = wasProccessed;
	}

	public KickFriendEnum getStatus() {
		return status;
	}

	public void setStatus(KickFriendEnum status) {
		this.status = status;
	}

	@JsonSerialize(using = CustomCalendarWeekSerializer.class)
	public Calendar getDateKick() {
		return dateKick;
	}

	public void setDateKick(Calendar dateKick) {
		this.dateKick = dateKick;
	}

	public GameMatch getGameMatch() {
		return gameMatch;
	}

	public void setGameMatch(GameMatch gameMatch) {
		this.gameMatch = gameMatch;
	}

	@JsonIgnoreProperties({ "city" })
	public Team getTeamChallenger() {
		return teamChallenger;
	}

	public void setTeamChallenger(Team teamChallenger) {
		this.teamChallenger = teamChallenger;
	}

	@JsonIgnoreProperties({ "city" })
	public Team getTeamChallenged() {
		return teamChallenged;
	}

	public void setTeamChallenged(Team teamChallenged) {
		this.teamChallenged = teamChallenged;
	}

	@JsonIgnoreProperties({ "realUnitvalue" })
	public Coin getCoin() {
		return coin;
	}

	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	@JsonIgnoreProperties({ "gameMatch", "teamA", "teamB", "tourn", "stadium" })
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public KickFriend() {
		this.status = KickFriendEnum.waiting;
		this.wasProccessed = false;
		this.result = null;
	}

}
