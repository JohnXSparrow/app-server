package com.App.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.model.AppAccess;
import com.App.model.Coin;
import com.App.model.UserLogin;
import com.App.model.UserMarketing;
import com.App.repository.AppAccessRepository;
import com.App.repository.AppConfigRepository;
import com.App.repository.CoinRepository;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserMarketingRespository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class UserDetailsController {

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	CoinRepository coinRepository;
	
	@Autowired
	UserMarketingRespository userMarketingRespository;
	
	@Autowired
	AppConfigRepository appConfigRepository;
	
	@Autowired
	AppAccessRepository appAccessRepository;

	@RequestMapping(value = "/userDetails", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> userDetails() {

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

		BigDecimal goldCoinRealValue = null;
		BigDecimal silverCoinRealValue = null;
		Collection<Coin> coins = coinRepository.findAll();

		for (Coin coin : coins) {
			if (coin.getId_coin() == 1L) {
				goldCoinRealValue = new BigDecimal(
						userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin() * coin.getRealUnitvalue()).setScale(2, RoundingMode.HALF_EVEN);
			}
			if (coin.getId_coin() == 2L) {
				silverCoinRealValue = new BigDecimal(
						userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin() * coin.getRealUnitvalue()).setScale(2, RoundingMode.HALF_EVEN);
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode userCoin = mapper.createObjectNode();
		
		ObjectNode goldCoin = mapper.createObjectNode();
		goldCoin.put("goldCoin", userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin());
		goldCoin.put("goldCoinRealValue", goldCoinRealValue);
		
		ObjectNode silverCoin = mapper.createObjectNode();
		silverCoin.put("silverCoin", userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin());
		silverCoin.put("silverCoinRealValue", silverCoinRealValue);
		
		userCoin.putPOJO("goldCoin", goldCoin);
		userCoin.putPOJO("silverCoin", silverCoin);

		ObjectNode userLoginObj = mapper.createObjectNode();
		userLoginObj.put("username", userLogin.getUsername());

		ObjectNode body = mapper.createObjectNode();
		body.putPOJO("userCoin", userCoin);
		body.putPOJO("userLogin", userLoginObj);

		ObjectNode userDetails = mapper.createObjectNode();
		userDetails.putPOJO("userDetails", body);

		return new ResponseEntity<>(userDetails, HttpStatus.OK);
	}

	@RequestMapping(value = "/dateTimeServer", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> dateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Brazil/East"));
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = mapper.createObjectNode();
		objectNode.putPOJO("dateTime", dateFormat.format(new Date()));

		return new ResponseEntity<>(objectNode, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/unsubscribe")
	public ResponseEntity<Object> unsubscribe(
			@RequestParam(value = "tk", required = true) String token,
			@RequestParam(value = "auth", required = true) String userLogin) {

		if (userLogin.matches("^[0-9]*$") == false || userLogin.length() < 1) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}
		
		UserMarketing userMarketingFound = userMarketingRespository.getUserMarketing(Long.parseLong(userLogin), token);
		if (userMarketingFound == null) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}

		userMarketingFound.setUnsubscribe(true);		
		userMarketingRespository.save(userMarketingFound);

		return new ResponseEntity<>(new GenericReturnMessage(0, "E-mail removido da lista."), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/config")
	public ResponseEntity<Object> config() {
		
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
		
		AppAccess count = appAccessRepository.findByUserId(userLogin.getId_userlogin());		
		if (count == null) {
			count = new AppAccess();
			count.setIdUserLogin(userLogin.getId_userlogin());
			count.setUserName(userLogin.getUsername());
			count.setEmail(userLogin.getUser().getEmail());
			count.setQttAccess(count.getQttAccess() + 1);
			count.setLastAccess(Calendar.getInstance());
		} else {
			count.setQttAccess(count.getQttAccess() + 1);
			count.setLastAccess(Calendar.getInstance());
		}
		
		appAccessRepository.saveAndFlush(count);
		
		return new ResponseEntity<>(appConfigRepository.findById(1L).orElse(null), HttpStatus.OK);
	}

}
