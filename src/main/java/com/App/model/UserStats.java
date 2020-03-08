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
public class UserStats {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_userstats;

	@NotNull
	@Column(nullable = false)
	private long win;

	@NotNull
	@Column(nullable = false)
	private long atie;

	@NotNull
	@Column(nullable = false)
	private long defeat;

	@NotNull
	@Column(nullable = false)
	private long kicks;	
	
	@NotNull
	@Column(nullable = false)
	private long winGeneral;

	@NotNull
	@Column(nullable = false)
	private long atieGeneral;

	@NotNull
	@Column(nullable = false)
	private long defeatGeneral;

	@NotNull
	@Column(nullable = false)
	private long kicksGeneral;

	@JsonIgnore
	@JsonProperty(value = "id_userstats")
	public long getId_userstats() {
		return id_userstats;
	}

	public void setId_userstats(long id_userstats) {
		this.id_userstats = id_userstats;
	}

	public long getWin() {
		return win;
	}

	public void setWin(long win) {
		this.win = win;
	}

	public long getAtie() {
		return atie;
	}

	public void setAtie(long atie) {
		this.atie = atie;
	}

	public long getDefeat() {
		return defeat;
	}

	public void setDefeat(long defeat) {
		this.defeat = defeat;
	}

	public long getKicks() {
		return kicks;
	}

	public void setKicks(long kicks) {
		this.kicks = kicks;
	}

	public long getWinGeneral() {
		return winGeneral;
	}

	public void setWinGeneral(long winGeneral) {
		this.winGeneral = winGeneral;
	}

	public long getAtieGeneral() {
		return atieGeneral;
	}

	public void setAtieGeneral(long atieGeneral) {
		this.atieGeneral = atieGeneral;
	}

	public long getDefeatGeneral() {
		return defeatGeneral;
	}

	public void setDefeatGeneral(long defeatGeneral) {
		this.defeatGeneral = defeatGeneral;
	}

	public long getKicksGeneral() {
		return kicksGeneral;
	}

	public void setKicksGeneral(long kicksGeneral) {
		this.kicksGeneral = kicksGeneral;
	}

	public UserStats() {
		this.win = 0;
		this.atie = 0;
		this.defeat = 0;
		this.kicks = 0;
		this.winGeneral = 0;
		this.atieGeneral = 0;
		this.defeatGeneral = 0;
		this.kicksGeneral = 0;		
	}

}