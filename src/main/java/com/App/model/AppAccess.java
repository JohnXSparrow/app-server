package com.App.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.App.serializer.CustomCalendarWeekSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class AppAccess {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_appaccess;
	
	@Column(unique = true)
	private long idUserLogin;
	
	@Column(unique = true)
	private String userName;
	
	@Column(unique = true)
	private String email;
	
	@Column
	private long qttAccess;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar lastAccess;

	public long getId_appaccess() {
		return id_appaccess;
	}

	public void setId_appaccess(long id_appaccess) {
		this.id_appaccess = id_appaccess;
	}

	public long getIdUserLogin() {
		return idUserLogin;
	}

	public void setIdUserLogin(long idUserLogin) {
		this.idUserLogin = idUserLogin;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getQttAccess() {
		return qttAccess;
	}

	public void setQttAccess(long qttAccess) {
		this.qttAccess = qttAccess;
	}

	@JsonSerialize(using = CustomCalendarWeekSerializer.class)
	public Calendar getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Calendar lastAccess) {
		this.lastAccess = lastAccess;
	}

}
