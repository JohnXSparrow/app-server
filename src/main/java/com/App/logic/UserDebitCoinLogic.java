package com.App.logic;

import org.springframework.stereotype.Service;

import com.App.model.Coin;
import com.App.model.UserCoin;

// Verifica o tipo de moeda e faz o desconto da moeda na carteira do usuario.

@Service
public class UserDebitCoinLogic {

	public Boolean debitCoin(long value, Coin coin, UserCoin userCoin) {
		boolean transaction = false;

		if (coin.getId_coin() == 1) {
			if (userCoin.getGoldCoin() >= value) {
				userCoin.setGoldCoin(userCoin.getGoldCoin() - value);
				transaction = true;
			} 

		} else if (coin.getId_coin() == 2) {
			if (userCoin.getSilverCoin() >= value) {
				userCoin.setSilverCoin(userCoin.getSilverCoin() - value);
				transaction = true;
			} 
		}
		return transaction;
	}

}
