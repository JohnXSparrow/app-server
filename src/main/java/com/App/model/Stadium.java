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

@Entity
public class Stadium {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_stadium;

	@NotNull
	@Column(length = 30, nullable = false)
	private String nameStadium;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private City city;

	public long getId_stadium() {
		return id_stadium;
	}

	public void setId_stadium(long id_stadium) {
		this.id_stadium = id_stadium;
	}

	public String getNameStadium() {
		return nameStadium;
	}

	public void setNameStadium(String nameStadium) {
		this.nameStadium = nameStadium;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

}
