package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Tourn {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_tourn;

	@NotNull
	@Column(length = 30, nullable = false)
	private String nameTourn;
	
	@NotNull
	@Column(length = 6, nullable = false)
	private String initials;

	public long getId_tourn() {
		return id_tourn;
	}

	public void setId_tourn(long id_tourn) {
		this.id_tourn = id_tourn;
	}

	public String getNameTourn() {
		return nameTourn;
	}

	public void setNameTourn(String nameTourn) {
		this.nameTourn = nameTourn;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

}
