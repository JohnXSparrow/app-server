package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Coin {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_coin;

	@NotNull
	@Column(length = 30, nullable = false)
	private String nameCoin;

	@NotNull
	@Column(nullable = false)
	private double realUnitvalue;

	public long getId_coin() {
		return id_coin;
	}

	public void setId_coin(long id_coin) {
		this.id_coin = id_coin;
	}

	public String getNameCoin() {
		return nameCoin;
	}

	public void setNameCoin(String nameCoin) {
		this.nameCoin = nameCoin;
	}

	public double getRealUnitvalue() {
		return realUnitvalue;
	}

	public void setRealUnitvalue(double realUnitvalue) {
		this.realUnitvalue = realUnitvalue;
	}

}
