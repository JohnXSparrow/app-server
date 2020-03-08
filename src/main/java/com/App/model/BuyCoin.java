package com.App.model;

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
import com.App.serializer.CustomCalendarSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class BuyCoin {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_buycoin;

	@Column(nullable = false)
	private String pay_id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private PackageCoin packageCoin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatusEnum status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	@Column(nullable = false)
	private Calendar dateBuy;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin userLogin;

	public long getId_buycoin() {
		return id_buycoin;
	}

	public void setId_buycoin(long id_buycoin) {
		this.id_buycoin = id_buycoin;
	}

	public String getPay_id() {
		return pay_id;
	}

	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}

	public PackageCoin getPackageCoin() {
		return packageCoin;
	}

	public void setPackageCoin(PackageCoin packageCoin) {
		this.packageCoin = packageCoin;
	}

	public PaymentStatusEnum getStatus() {
		return status;
	}

	public void setStatus(PaymentStatusEnum status) {
		this.status = status;
	}

	@JsonSerialize(using = CustomCalendarSerializer.class)
	public Calendar getDateBuy() {
		return dateBuy;
	}

	public void setDateBuy(Calendar dateBuy) {
		this.dateBuy = dateBuy;
	}

	@JsonIgnore
	@JsonProperty(value = "userLogin")
	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

}
