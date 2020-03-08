package com.App.controller;

import java.util.Calendar;
import java.util.Collection;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.logic.UserDebitCoinLogic;
import com.App.model.Coin;
import com.App.model.ExchangeCoin;
import com.App.model.UserLogin;
import com.App.repository.CoinRepository;
import com.App.repository.ExchangeCoinRepository;
import com.App.repository.UserCoinRepository;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping("/user/exchangecoin")
public class UserExchangeCoinController {
	private static final Logger LOG = LoggerFactory.getLogger(UserExchangeCoinController.class.getName());

	@Autowired
	ExchangeCoinRepository exchangeCoinRepository;

	@Autowired
	UserCoinRepository userCoinRepository;

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	CoinRepository coinRepository;

	@Autowired
	UserDebitCoinLogic userCoinLogic;

	@RequestMapping(value = "/exchange", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid ExchangeCoin exchangeCoin, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}
		
		if (exchangeCoin.getId_exchangecoin() > 0 || exchangeCoin.getId_exchangecoin() < 0) {
			LOG.error("Prohibited Process, You Can not Do It!");
			return new ResponseEntity<>(new GenericReturnMessage(25, "Processo proibido, você não pode fazer isso!"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (exchangeCoin.getAmountCoinFrom() < 1000) {
			return new ResponseEntity<>(new GenericReturnMessage(32, "Mínimo de 1.000 Moedas"),
					HttpStatus.BAD_REQUEST);
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserLogin userLogin = userLoginRepository.findByUsername(authentication.getName());
		if (userLogin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(36, "Bad Credentials"), HttpStatus.BAD_REQUEST);
		}
		if (userLogin.getUserStatus().equals(UserStatusEnum.BLOCKED)) {
			return new ResponseEntity<>(new GenericReturnMessage(37, "Conta Bloqueada"), HttpStatus.BAD_REQUEST);
		}
		if (userLogin.getUserStatus().equals(UserStatusEnum.DISABLED)) {
			return new ResponseEntity<>(new GenericReturnMessage(38, "Conta Desativada"), HttpStatus.BAD_REQUEST);
		}

		if (exchangeCoin.getAmountCoinFrom() % 50 != 0) {
			return new ResponseEntity<>(new GenericReturnMessage(33, "Valor deve ser multiplo de 50"),
					HttpStatus.BAD_REQUEST);
		}

		Collection<Coin> coins = coinRepository.findAll();
		Coin goldCoin = null, silverCoin = null;
		for (Coin coin : coins) {
			if (coin.getId_coin() == 1) {
				goldCoin = coin;
			} else if (coin.getId_coin() == 2) {
				silverCoin = coin;
			}
		}

		boolean transaction = userCoinLogic.debitCoin(exchangeCoin.getAmountCoinFrom(), silverCoin,
				userLogin.getUser().getUserProfile().getUserCoin());
		if (transaction == false) {
			return new ResponseEntity<>(
					new GenericReturnMessage(31, "Saldo de Moeda " + silverCoin.getNameCoin() + " Insuficiente"),
					HttpStatus.BAD_REQUEST);
		}

		//Logica que calcula a quantidade de moedas ouro a receber na troca por prata
		long valueToReceiver = (long) (exchangeCoin.getAmountCoinFrom() * silverCoin.getRealUnitvalue() / goldCoin.getRealUnitvalue());
		userLogin.getUser().getUserProfile().getUserCoin().setGoldCoin(userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin() + valueToReceiver);

		exchangeCoin.setAmountCoinTo(valueToReceiver);
		exchangeCoin.setFromCoin(silverCoin);
		exchangeCoin.setToCoin(goldCoin);
		exchangeCoin.setUserLogin(userLogin);
		exchangeCoin.setDateExchange(Calendar.getInstance());

		userLoginRepository.saveAndFlush(userLogin);
		ExchangeCoin exchangeCoinSaved = exchangeCoinRepository.save(exchangeCoin);

		return new ResponseEntity<>(exchangeCoinSaved, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listExchanges() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserLogin userLogin = userLoginRepository.findByUsername(authentication.getName());
		if (userLogin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(36, "Bad Credentials"), HttpStatus.BAD_REQUEST);
		}
		if (userLogin.getUserStatus().equals(UserStatusEnum.BLOCKED)) {
			return new ResponseEntity<>(new GenericReturnMessage(37, "Conta Bloqueada"), HttpStatus.BAD_REQUEST);
		}
		if (userLogin.getUserStatus().equals(UserStatusEnum.DISABLED)) {
			return new ResponseEntity<>(new GenericReturnMessage(38, "Conta Desativada"), HttpStatus.BAD_REQUEST);
		}

		Calendar start = Calendar.getInstance();
		start.add(Calendar.DAY_OF_MONTH, -30);
		Calendar end = Calendar.getInstance();
		end.add(Calendar.MONTH, 1);

		Collection<ExchangeCoin> listExchangeCoin = exchangeCoinRepository.findAllExchangeCoinByUserAndTime(userLogin, start, end);

		return new ResponseEntity<>(listExchangeCoin, HttpStatus.OK);
	}

}
