package com.App.model;

import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Register {

	@Valid
	@Transient
	private UserLogin userLogin;

	@Transient
	@NotNull
	private String recaptchaReactive;

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	@JsonIgnore
	@JsonProperty(value = "recaptchaReactive")
	public String getRecaptchaReactive() {
		return recaptchaReactive;
	}

	public void setRecaptchaReactive(String recaptchaReactive) {
		this.recaptchaReactive = recaptchaReactive;
	}

}
