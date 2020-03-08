package com.App.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.App.serializer.CustomCalendarWeekSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class GameMatch {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_gamematch;

	@Temporal(TemporalType.DATE)
	private Date dateTime;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm", locale = "pt-BR", timezone = "Brazil/East")
	private Calendar startTime;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm", locale = "pt-BR", timezone = "Brazil/East")
	private Calendar endTime;

	@Column
	@Type(type = "true_false")
	private boolean isKickDisabled;

	@Column
	@Type(type = "true_false")
	private boolean isClosed;

	@Column
	@Type(type = "true_false")
	private boolean isSetResult;

	@Column
	@Type(type = "true_false")
	private boolean isWeek;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Team teamA;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Team teamB;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Tourn tourn;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Stadium stadium;
	
	@Column(columnDefinition="TINYTEXT")
	private String bonus;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private boolean notify;

	public long getId_gamematch() {
		return id_gamematch;
	}

	public void setId_gamematch(long id_gamematch) {
		this.id_gamematch = id_gamematch;
	}

	public Team getTeamA() {
		return teamA;
	}

	public void setTeamA(Team teamA) {
		this.teamA = teamA;
	}

	public Team getTeamB() {
		return teamB;
	}

	public void setTeamB(Team teamB) {
		this.teamB = teamB;
	}

	public Tourn getTourn() {
		return tourn;
	}

	public void setTourn(Tourn tourn) {
		this.tourn = tourn;
	}

	@JsonIgnoreProperties({ "city" })
	public Stadium getStadium() {
		return stadium;
	}

	public void setStadium(Stadium stadium) {
		this.stadium = stadium;
	}

	@JsonIgnore
	@JsonProperty(value = "dateTime")
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@JsonIgnore
	@JsonProperty(value = "isClosed")
	public boolean getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	@JsonIgnore
	@JsonProperty(value = "isKickDisabled")
	public boolean getIsKickDisabled() {
		return isKickDisabled;
	}

	public void setIsKickDisabled(boolean isKickDisabled) {
		this.isKickDisabled = isKickDisabled;
	}

	@JsonIgnore
	@JsonProperty(value = "isSetResult")
	public boolean getIsSetResult() {
		return isSetResult;
	}

	public void setIsSetResult(boolean isSetResult) {
		this.isSetResult = isSetResult;
	}

	@JsonIgnore
	@JsonProperty(value = "isWeek")
	public boolean getIsWeek() {
		return isWeek;
	}

	public void setIsWeek(boolean isWeek) {
		this.isWeek = isWeek;
	}

	@JsonSerialize(using = CustomCalendarWeekSerializer.class)
	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	@JsonSerialize(using = CustomCalendarWeekSerializer.class)
	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public boolean getNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public GameMatch() {
		this.isKickDisabled = false;
		this.isClosed = false;
		this.isSetResult = false;
		this.isWeek = false;
	}
}
