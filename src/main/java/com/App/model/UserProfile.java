package com.App.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.App.enumeration.ProfilePlanEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class UserProfile {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_userprofile;	
	
	@Transient
	private String username;
	
	private ProfilePlanEnum profilePlan;

	@OneToOne(fetch = FetchType.EAGER)
	private Team favoriteTeam;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private UserCoin userCoin;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private UserStats userStats;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private UserInformation userInformation;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private UserNotifications userNotifications;
	
	@JsonIgnore
	@JsonProperty(value = "id_userprofile")
	public long getId_userprofile() {
		return id_userprofile;
	}

	public void setId_userprofile(long id_userprofile) {
		this.id_userprofile = id_userprofile;
	}

	public ProfilePlanEnum getProfilePlan() {
		return profilePlan;
	}

	public void setProfilePlan(ProfilePlanEnum profilePlan) {
		this.profilePlan = profilePlan;
	}

	public Team getFavoriteTeam() {
		return favoriteTeam;
	}

	public void setFavoriteTeam(Team favoriteTeam) {
		this.favoriteTeam = favoriteTeam;
	}
	
	public UserCoin getUserCoin() {
		return userCoin;
	}

	public void setUserCoin(UserCoin userCoin) {
		this.userCoin = userCoin;
	}

	public UserStats getUserStats() {
		return userStats;
	}

	public void setUserStats(UserStats userStats) {
		this.userStats = userStats;
	}

	@JsonIgnore
	@JsonProperty(value = "userInformation")
	public UserInformation getUserInformation() {
		return userInformation;
	}

	public void setUserInformation(UserInformation userInformation) {
		this.userInformation = userInformation;
	}

	@JsonIgnore
	@JsonProperty(value = "userNotifications")
	public UserNotifications getUserNotifications() {
		return userNotifications;
	}

	public void setUserNotifications(UserNotifications userNotifications) {
		this.userNotifications = userNotifications;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserProfile() {
		this.profilePlan = ProfilePlanEnum.Basico;
		this.favoriteTeam = null;
		this.userInformation = null;
		this.userCoin = new UserCoin();
		this.userStats = new UserStats();
		this.userNotifications = new UserNotifications();
		
	}

}
