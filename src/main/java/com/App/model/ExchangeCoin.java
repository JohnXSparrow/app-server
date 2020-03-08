package com.App.model;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.App.serializer.CustomCalendarOnlyDateSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class ExchangeCoin {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_exchangecoin;
	
	@NotNull
	@Column(nullable = false)
	private long amountCoinFrom;
	
	@Column(nullable = false)
	private long amountCoinTo;
			
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	@Column(nullable = false)
	private Calendar dateExchange;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin userLogin;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Coin fromCoin;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Coin toCoin;

	public long getId_exchangecoin() {
		return id_exchangecoin;
	}

	public void setId_exchangecoin(long id_exchangecoin) {
		this.id_exchangecoin = id_exchangecoin;
	}

	public long getAmountCoinFrom() {
		return amountCoinFrom;
	}

	public void setAmountCoinFrom(long amountCoinFrom) {
		this.amountCoinFrom = amountCoinFrom;
	}

	public long getAmountCoinTo() {
		return amountCoinTo;
	}

	public void setAmountCoinTo(long amountCoinTo) {
		this.amountCoinTo = amountCoinTo;
	}

	@JsonSerialize(using = CustomCalendarOnlyDateSerializer.class)
	public Calendar getDateExchange() {
		return dateExchange;
	}

	public void setDateExchange(Calendar dateExchange) {
		this.dateExchange = dateExchange;
	}

	@JsonIgnore
	@JsonProperty(value = "fromCoin")
	public Coin getFromCoin() {
		return fromCoin;
	}

	@JsonIgnore
	@JsonProperty(value = "userLogin")
	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public void setFromCoin(Coin fromCoin) {
		this.fromCoin = fromCoin;
	}

	@JsonIgnore
	@JsonProperty(value = "toCoin")
	public Coin getToCoin() {
		return toCoin;
	}

	public void setToCoin(Coin toCoin) {
		this.toCoin = toCoin;
	}

}
