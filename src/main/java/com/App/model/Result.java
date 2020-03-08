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
public class Result {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_result;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private GameMatch gameMatch;

	@NotNull
	@Column(nullable = false)
	private int goalsTeamA;

	@NotNull
	@Column(nullable = false)
	private int goalsTeamB;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Team teamWin;

	public long getId_result() {
		return id_result;
	}

	public void setId_result(long id_result) {
		this.id_result = id_result;
	}

	@JsonIgnoreProperties({"dateTimeString"})
	public GameMatch getGameMatch() {
		return gameMatch;
	}

	public void setGameMatch(GameMatch gameMatch) {
		this.gameMatch = gameMatch;
	}

	public int getGoalsTeamA() {
		return goalsTeamA;
	}

	public void setGoalsTeamA(int goalsTeamA) {
		this.goalsTeamA = goalsTeamA;
	}

	public int getGoalsTeamB() {
		return goalsTeamB;
	}

	public void setGoalsTeamB(int goalsTeamB) {
		this.goalsTeamB = goalsTeamB;
	}

	@JsonIgnoreProperties({"city"})
	public Team getTeamWin() {
		return teamWin;
	}

	public void setTeamWin(Team teamWin) {
		this.teamWin = teamWin;
	}

}
