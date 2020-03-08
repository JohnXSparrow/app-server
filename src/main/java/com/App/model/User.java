package com.App.model;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class User {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_user;

	@NotNull
	@Size(min = 2, max = 30, message = "O tamanho do primeiro nome deve estar entre 2 e 30")
	@Pattern(message = "Primeiro nome deve conter apenas letras", regexp = "^[A-Za-záàâãéèêìíïóôõöúçñÁÀÂÃÉÈÌÍÏÓÔÕÖÚÇÑ]*$")
	@Column(length = 30, nullable = false)
	private String firstName;

	@NotNull
	@Size(min = 2, max = 30, message = "O tamanho do sobrenome deve estar entre 2 e 30")
	@Pattern(message = "Sobrenome deve conter apenas letras", regexp = "^[A-Za-záàâãéèêìíïóôõöúçñÁÀÂÃÉÈÌÍÏÓÔÕÖÚÇÑ]*$")
	@Column(length = 30, nullable = false)
	private String lastName;

	@Column(unique = true)
	@Size(max = 11)
	private String cpf;
	
	@NotNull
	@Pattern(message = "Fornecer um endereço de e-mail válido. Ex: usuario@email.com", regexp = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}.[a-z]{0,2}$")
	@Column(nullable = false, unique = true)
	private String email;

	@NotNull
	@Past
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(nullable = false)
	private Calendar dateBirth;

	// O sistema ira setar a data atual na hora do registro
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Calendar dateRegister;	
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private UserProfile userProfile;

	@JsonIgnore
	@JsonProperty(value = "id_user")
	public long getId_user() {
		return id_user;
	}

	public void setId_user(long id_user) {
		this.id_user = id_user;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Calendar getDateBirth() {
		return dateBirth;
	}

	public void setDateBirth(Calendar dateBirth) {
		this.dateBirth = dateBirth;
	}

	@JsonIgnore
	@JsonProperty(value = "dateRegister")
	public Calendar getDateRegister() {
		return dateRegister;
	}

	public void setDateRegister(Calendar dateRegister) {
		this.dateRegister = dateRegister;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	@JsonProperty(value = "cpf")
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public User() {
		this.cpf = null;
		this.userProfile = null;
	}	

}
