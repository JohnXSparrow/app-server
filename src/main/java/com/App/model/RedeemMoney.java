package com.App.model;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.App.enumeration.PaymentStatusEnum;
import com.App.serializer.CustomCalendarOnlyDateSerializer;
import com.App.serializer.CustomCalendarSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class RedeemMoney {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_redeemmoney;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin userLogin;

	@NotNull
	@Column(nullable = false)
	private long amountCoin;
	
	@NotNull
	@Column(nullable = false)
	private String emailForPayment;

	@Column(nullable = false)
	private BigDecimal valueRedeem;

	@Column(nullable = false)
	private BigDecimal valueToReceive;

	@Column(nullable = false)
	private BigDecimal tax;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	@Column(nullable = false)
	private Calendar dateRequest;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatusEnum status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	@Column(nullable = true)
	private Calendar dateUpdateStatus;

	public long getId_redeemmoney() {
		return id_redeemmoney;
	}

	public void setId_redeemmoney(long id_redeemmoney) {
		this.id_redeemmoney = id_redeemmoney;
	}

	@JsonIgnore
	@JsonProperty(value = "userLogin")
	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public long getAmountCoin() {
		return amountCoin;
	}

	public void setAmountCoin(long amountCoin) {
		this.amountCoin = amountCoin;
	}

	@JsonIgnore
	@JsonProperty(value = "valueRedeem")
	public BigDecimal getValueRedeem() {
		return valueRedeem;
	}

	public void setValueRedeem(BigDecimal valueRedeem) {
		this.valueRedeem = valueRedeem;
	}

	public BigDecimal getValueToReceive() {
		return valueToReceive;
	}

	public void setValueToReceive(BigDecimal valueToReceive) {
		this.valueToReceive = valueToReceive;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	@JsonSerialize(using = CustomCalendarOnlyDateSerializer.class)
	public Calendar getDateRequest() {
		return dateRequest;
	}

	public void setDateRequest(Calendar dateRequest) {
		this.dateRequest = dateRequest;
	}

	public PaymentStatusEnum getStatus() {
		return status;
	}

	public void setStatus(PaymentStatusEnum status) {
		this.status = status;
	}

	@JsonSerialize(using = CustomCalendarSerializer.class)
	public Calendar getDateUpdateStatus() {
		return dateUpdateStatus;
	}

	public void setDateUpdateStatus(Calendar dateUpdateStatus) {
		this.dateUpdateStatus = dateUpdateStatus;
	}

	public String getEmailForPayment() {
		return emailForPayment;
	}

	public void setEmailForPayment(String emailForPayment) {
		this.emailForPayment = emailForPayment;
	}

}
