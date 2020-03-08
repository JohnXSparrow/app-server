package com.App.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class RankingPotCoin {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_rankingpotcoin;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Coin coin;

	@NotNull
	@Column(nullable = false)
	private long amountCoin;

	public long getId_rankingpotcoin() {
		return id_rankingpotcoin;
	}

	public void setId_rankingpotcoin(long id_rankingpotcoin) {
		this.id_rankingpotcoin = id_rankingpotcoin;
	}

	public Coin getCoin() {
		return coin;
	}

	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	public long getAmountCoin() {
		return amountCoin;
	}

	public void setAmountCoin(long amountCoin) {
		this.amountCoin = amountCoin;
	}

}
