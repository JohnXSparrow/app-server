package com.App.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.App.enumeration.UserRoleEnum;
import com.App.enumeration.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class UserLogin {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_userlogin;

	@NotNull
	@Pattern(message = "O nome de usuário deve conter apenas letras e números", regexp = "^[A-Za-z0-9]*$")
	@Size(min = 4, max = 13, message = "O tamanho do nome de usuário deve estar entre 4 e 13")
	@Column(length = 13, unique = true, nullable = false)
	private String username;

	@NotNull
	@Size(min = 6, message = "A senha deve conter 6 ou mais caracteres")
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private UserRoleEnum userRole;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private UserStatusEnum userStatus;
	
	@Valid
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private User user;

	@Column
	private String tokenCode;

	@JsonIgnore
	@JsonProperty(value = "id_userlogin")
	public long getId_userlogin() {
		return id_userlogin;
	}

	public void setId_userlogin(long id_userlogin) {
		this.id_userlogin = id_userlogin;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonIgnore
	@JsonProperty(value = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonIgnore
	@JsonProperty(value = "userRole")
	public UserRoleEnum getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRoleEnum userRole) {
		this.userRole = userRole;
	}

	@JsonIgnore
	@JsonProperty(value = "userStatus")
	public UserStatusEnum getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatusEnum userStatus) {
		this.userStatus = userStatus;
	}

	@JsonIgnore
	@JsonProperty(value = "tokenCode")
	public String getTokenCode() {
		return tokenCode;
	}

	public void setTokenCode(String tokenCode) {
		this.tokenCode = tokenCode;
	}

	@JsonIgnore
	@JsonProperty(value = "user")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
