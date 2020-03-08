package com.App.model;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.App.serializer.CustomCalendarWeekSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class Kick {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_kick;

	@NotNull
	@Column(nullable = false)
	private long valueKick;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin userLogin;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin oponent;

	@Column
	@Type(type = "true_false")
	private boolean wasProccessed;

	@Column(nullable = false)
	private Calendar dateKick;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private GameMatch gameMatch;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Team team;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Coin coin;

	@ManyToOne(fetch = FetchType.EAGER)
	private Result result;

	@Column
	private Long valuewinorlost;

	public long getId_kick() {
		return id_kick;
	}

	public void setId_kick(long id_kick) {
		this.id_kick = id_kick;
	}

	public long getValueKick() {
		return valueKick;
	}

	public void setValueKick(long valueKick) {
		this.valueKick = valueKick;
	}

	@JsonIgnore
	@JsonProperty(value = "userLogin")
	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	@JsonIgnoreProperties({ "user" })
	public UserLogin getOponent() {
		return oponent;
	}

	public void setOponent(UserLogin oponent) {
		this.oponent = oponent;
	}

	public GameMatch getGameMatch() {
		return gameMatch;
	}

	public void setGameMatch(GameMatch gameMatch) {
		this.gameMatch = gameMatch;
	}

	@JsonIgnoreProperties({ "city" })
	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@JsonIgnoreProperties({ "realUnitvalue" })
	public Coin getCoin() {
		return coin;
	}

	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	public boolean isWasProccessed() {
		return wasProccessed;
	}

	public void setWasProccessed(boolean wasProccessed) {
		this.wasProccessed = wasProccessed;
	}

	@JsonSerialize(using = CustomCalendarWeekSerializer.class)
	public Calendar getDateKick() {
		return dateKick;
	}

	public void setDateKick(Calendar dateKick) {
		this.dateKick = dateKick;
	}

	@JsonIgnoreProperties({ "gameMatch", "teamA", "teamB", "tourn", "stadium" })
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Long getValuewinorlost() {
		return valuewinorlost;
	}

	public void setValuewinorlost(Long valuewinorlost) {
		this.valuewinorlost = valuewinorlost;
	}

	public Kick() {
		this.wasProccessed = false;
		this.valuewinorlost = 0L;
		this.result = null;
	}

}
