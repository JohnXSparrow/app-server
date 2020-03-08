package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
public class UserNotifications {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_usernotifications;
	
	@Column
	@Type(type = "true_false")
	private boolean oponentFound;
	
	@Column
	@Type(type = "true_false")
	private boolean news;
	
	@Column
	@Type(type = "true_false")
	private boolean marketing;

	public long getId_usernotifications() {
		return id_usernotifications;
	}

	public void setId_usernotifications(long id_usernotifications) {
		this.id_usernotifications = id_usernotifications;
	}

	public boolean isOponentFound() {
		return oponentFound;
	}

	public void setOponentFound(boolean oponentFound) {
		this.oponentFound = oponentFound;
	}

	public boolean isNews() {
		return news;
	}

	public void setNews(boolean news) {
		this.news = news;
	}

	public boolean isMarketing() {
		return marketing;
	}

	public void setMarketing(boolean marketing) {
		this.marketing = marketing;
	}

	public UserNotifications() {
		this.oponentFound = true;
		this.news = true;
		this.marketing = true;
	}	

}
