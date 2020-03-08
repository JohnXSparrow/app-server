package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class AppConfig {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_appconfig;

	@Column
	@Type(type = "true_false")
	private Boolean active;

	@Column(nullable = false, columnDefinition = "text")
	private String url;

	@Column(nullable = false)
	private int version;

	@JsonIgnore
	@JsonProperty(value = "id_appconfig")
	public long getId_appconfig() {
		return id_appconfig;
	}

	public void setId_appconfig(long id_appconfig) {
		this.id_appconfig = id_appconfig;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
