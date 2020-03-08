package com.App.security.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class OauthAccessToken {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_oauthaccesstoken;
	
	@Column
	private String token_id;
	
	@Column
	@Lob
	private byte[] token;
	
	@Column
	private String authentication_id;
	
	@Column(unique = true)	
	private String user_name;
	
	@Column
	private String client_id;
	
	@Column
	@Lob
	private byte[] authentication;
	
	@Column
	private String refresh_token;

	public long getId_oauthaccesstoken() {
		return id_oauthaccesstoken;
	}

	public void setId_oauthaccesstoken(long id_oauthaccesstoken) {
		this.id_oauthaccesstoken = id_oauthaccesstoken;
	}

	public String getToken_id() {
		return token_id;
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public String getAuthentication_id() {
		return authentication_id;
	}

	public void setAuthentication_id(String authentication_id) {
		this.authentication_id = authentication_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}	

}
