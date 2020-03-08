package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CPF;

@Entity
public class UserInformation {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_userinformation;

	@CPF
	@Transient
	@Pattern(regexp = "([0-9]{11})", message = "CPF deve conter somente números")
	@Size(max = 11, message = "Não ultrapasse 11 caracteres")
	private String cpf;
	
	@Transient
	private String nomecompleto;

	@NotNull
	@Pattern(regexp = "([0-9]+)", message = "Telefone deve conter somente números")
	@Size(min = 10, max = 11, message = "Não ultrapasse 11 caracteres")
	@Column(length = 11, nullable = false)
	private String tel;

	@NotNull
	@Size(max = 60, message = "Endereço deve conter no maximo 60 caracteres")
	@Column(length = 60, nullable = false)
	private String endereco;

	@NotNull
	@Size(max = 10, message = "Numero deve conter no maximo 10 caracteres")
	@Column(length = 10, nullable = false)
	private String numero;

	@NotNull
	@Pattern(regexp = "([0-9]{8})", message = "CEP deve conter somente números")
	@Size(max = 8, message = "Não ultrapasse 8 caracteres")
	@Column(length = 8, nullable = false)
	private String cep;

	@NotNull
	@Size(max = 30, message = "Bairro deve conter no maximo 30 caracteres")
	@Column(length = 30, nullable = false)
	private String bairro;

	@NotNull
	@Size(max = 30, message = "Cidade deve conter no maximo 30 caracteres")
	@Column(length = 30, nullable = false)
	private String cidade;

	@NotNull
	@Size(max = 2, message = "Estado deve conter no maximo 2 caracteres")
	@Column(length = 2, nullable = false)
	private String estado;

	public long getId_userinformation() {
		return id_userinformation;
	}

	public void setId_userinformation(long id_userinformation) {
		this.id_userinformation = id_userinformation;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNomecompleto() {
		return nomecompleto;
	}

	public void setNomecompleto(String nomecompleto) {
		this.nomecompleto = nomecompleto;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
