package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Country {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_country;

	@NotNull
	@Column(length = 30, unique = true, nullable = false)
	private String nameCountry;

	@NotNull
	@Column(length = 2, unique = true, nullable = false)
	private String initials;

	public long getId_country() {
		return id_country;
	}

	public void setId_country(long id_country) {
		this.id_country = id_country;
	}

	public String getNameCountry() {
		return nameCountry;
	}

	public void setNameCountry(String nameCountry) {
		this.nameCountry = nameCountry;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

}