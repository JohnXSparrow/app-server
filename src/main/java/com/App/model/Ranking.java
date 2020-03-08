package com.App.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Ranking {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_ranking;

	@Column(length = 6, unique = true, nullable = false)
	private String date;

	@Column
	@Type(type = "true_false")
	private boolean wasProccessed;

	@JsonManagedReference
	@OneToMany(mappedBy = "ranking", targetEntity = UserRanking.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Collection<UserRanking> userRanking;

	public long getId_ranking() {
		return id_ranking;
	}

	public void setId_ranking(long id_ranking) {
		this.id_ranking = id_ranking;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Collection<UserRanking> getUserRanking() {
		return userRanking;
	}

	public void setUserRanking(Collection<UserRanking> userRanking) {
		this.userRanking = userRanking;
	}

	public boolean isWasProccessed() {
		return wasProccessed;
	}

	public void setWasProccessed(boolean wasProccessed) {
		this.wasProccessed = wasProccessed;
	}

}
