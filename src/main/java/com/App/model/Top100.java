package com.App.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Top100 {

	private int position;
	private String username;
	private UserStats userStats;
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonIgnoreProperties({ "win", "atie", "defeat", "kicks" })
	public UserStats getUserStats() {
		return userStats;
	}

	public void setUserStats(UserStats userStats) {
		this.userStats = userStats;
	}

}
