package com.App.model;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.App.serializer.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class PackageCoin {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_packagecoin;

	@NotNull
	@Column(length = 30, nullable = false)
	private String namePackage;

	@Column(nullable = false, precision = 6, scale = 2)
    @JsonSerialize(using = MoneySerializer.class)
	private BigDecimal valuePackage;
	
	@Column(nullable = false, precision = 5, scale = 2)
    @JsonSerialize(using = MoneySerializer.class)
	private BigDecimal tax;
	
	@Column(nullable = false, precision = 6, scale = 2)
    @JsonSerialize(using = MoneySerializer.class)
	private BigDecimal totalToPay;
	
	@NotNull
	@Column(nullable = false)
	private long amountCoin;

	@NotNull
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Coin coin;	

	public long getId_packagecoin() {
		return id_packagecoin;
	}

	public void setId_packagecoin(long id_packagecoin) {
		this.id_packagecoin = id_packagecoin;
	}

	public String getNamePackage() {
		return namePackage;
	}

	public void setNamePackage(String namePackage) {
		this.namePackage = namePackage;
	}

	@JsonSerialize(using = MoneySerializer.class)
	public BigDecimal getValuePackage() {
		return valuePackage;
	}

	@JsonSerialize(using = MoneySerializer.class)
	public void setValuePackage(BigDecimal valuePackage) {
		this.valuePackage = valuePackage;
	}

	public long getAmountCoin() {
		return amountCoin;
	}

	public void setAmountCoin(long amountCoin) {
		this.amountCoin = amountCoin;
	}

	public Coin getCoin() {
		return coin;
	}

	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getTotalToPay() {
		return totalToPay;
	}

	public void setTotalToPay(BigDecimal totalToPay) {
		this.totalToPay = totalToPay;
	}

}
