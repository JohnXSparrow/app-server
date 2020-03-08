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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Team {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_team;

	@NotNull
	@Column(length = 30, unique = true, nullable = false)
	private String nameTeam;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int goalScored;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int goalSuffered;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int winHome;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int winOut;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int tieHome;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int tieOut;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int lostHome;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int lostOut;

	@NotNull
	@JsonIgnore
	@Column(nullable = false)
	private int matchesPlayed;
	
	@Column(nullable = false)
	private String imageTeam;

	@NotNull
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private City city;

	public long getId_team() {
		return id_team;
	}

	public void setId_team(long id_team) {
		this.id_team = id_team;
	}

	public String getNameTeam() {
		return nameTeam;
	}

	public void setNameTeam(String nameTeam) {
		this.nameTeam = nameTeam;
	}

	public int getGoalScored() {
		return goalScored;
	}

	public void setGoalScored(int goalScored) {
		this.goalScored = goalScored;
	}

	public int getGoalSuffered() {
		return goalSuffered;
	}

	public void setGoalSuffered(int goalSuffered) {
		this.goalSuffered = goalSuffered;
	}

	public int getWinHome() {
		return winHome;
	}

	public void setWinHome(int winHome) {
		this.winHome = winHome;
	}

	public int getWinOut() {
		return winOut;
	}

	public void setWinOut(int winOut) {
		this.winOut = winOut;
	}

	public int getTieHome() {
		return tieHome;
	}

	public void setTieHome(int tieHome) {
		this.tieHome = tieHome;
	}

	public int getTieOut() {
		return tieOut;
	}

	public void setTieOut(int tieOut) {
		this.tieOut = tieOut;
	}

	public int getLostHome() {
		return lostHome;
	}

	public void setLostHome(int lostHome) {
		this.lostHome = lostHome;
	}

	public int getLostOut() {
		return lostOut;
	}

	public void setLostOut(int lostOut) {
		this.lostOut = lostOut;
	}

	public int getMatchesPlayed() {
		return matchesPlayed;
	}

	public void setMatchesPlayed(int matchesPlayed) {
		this.matchesPlayed = matchesPlayed;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getImageTeam() {
		return imageTeam;
	}

	public void setImageTeam(String imageTeam) {
		this.imageTeam = imageTeam;
	}

	public Team() {
		this.goalScored = 0;
		this.goalSuffered = 0;
		this.winHome = 0;
		this.winOut = 0;
		this.tieHome = 0;
		this.tieOut = 0;
		this.lostHome = 0;
		this.lostOut = 0;
		this.matchesPlayed = 0;
	}

}
