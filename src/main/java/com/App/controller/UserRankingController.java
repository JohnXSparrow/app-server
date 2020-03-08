package com.App.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.model.RankingPotCoin;
import com.App.model.Top100;
import com.App.model.UserLogin;
import com.App.model.UserRanking;
import com.App.model.UserStats;
import com.App.repository.RankingPotCoinRepository;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserRankingRepository;
import com.App.repository.UserStatsRepository;

@RestController
@RequestMapping("/user/ranking")
public class UserRankingController {

	@Autowired
	UserRankingRepository userRankingRepository;

	@Autowired
	UserStatsRepository userStatsRepository;

	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	RankingPotCoinRepository coinRankingRepository;

	@RequestMapping(value = "/public", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listUserRankingPublic() {
		ZonedDateTime sp = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));		
		Collection<UserRanking> listUserRanking = userRankingRepository.findAllByDate(String.valueOf(sp.getMonthValue()) + String.valueOf(sp.getYear()));
		return new ResponseEntity<>(listUserRanking, HttpStatus.OK);
	}

	@RequestMapping(value = "/logged", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listUserRankingLogged() {
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

		UserRanking userRanking = new UserRanking();
		userRanking.setId_userranking(userLogin.getId_userlogin());
		userRanking.setUserStats(userLogin.getUser().getUserProfile().getUserStats());
		userRanking.setUserLogin(userLogin);
		userRanking.setGoldReward(0);
		userRanking.setSilverReward(0);
		
		boolean isInRanking = false;
		ZonedDateTime sp = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));
		Collection<UserRanking> listUserRanking = userRankingRepository.findAllByDate(String.valueOf(sp.getMonthValue()) + String.valueOf(sp.getYear()));
		for (UserRanking x : listUserRanking) {
			if (x.getUserLogin().getId_userlogin() == userLogin.getId_userlogin()) {
				userRanking.setGoldReward(x.getGoldReward());
				userRanking.setSilverReward(x.getSilverReward());
				userRanking.setPosition(x.getPosition());
				isInRanking = true;
			}
		}

		if (!isInRanking) {
			userRanking.setPosition(userStatsRepository.findUserPosition(userLogin.getId_userlogin()));
		}

		listUserRanking.add(userRanking);
		return new ResponseEntity<>(listUserRanking, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/last", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listUserRankingLast() {		
		ZonedDateTime sp = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));
		Collection<UserRanking> listUserRanking;
		
		if (sp.getMonthValue() == 1) {
			listUserRanking = userRankingRepository.findAllByDate("12" + String.valueOf(sp.getYear() - 1));
		} else {
			listUserRanking = userRankingRepository.findAllByDate(String.valueOf(sp.getMonthValue() - 1) + String.valueOf(sp.getYear()));
		}

		return new ResponseEntity<>(listUserRanking, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/top100", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> generalTop100Ranking() {
		
		int position = 1;
		UserLogin userLogin;
		Top100 top100;
		Collection<Top100> listTop100 = new ArrayList<Top100>();
		Collection<UserStats> listStats = userStatsRepository.getUserStatsTop100();
		
		for (UserStats userStats : listStats) {
			userLogin = userLoginRepository.findById(userStats.getId_userstats()).orElse(null);
			
			top100 = new Top100();
			top100.setPosition(position);
			top100.setUsername(userLogin.getUsername());
			top100.setUserStats(userStats);
			
			listTop100.add(top100);
			position++;
		}			
			
		return new ResponseEntity<>(listTop100, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/pot-coin", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> potCoins() {
		Collection<RankingPotCoin> coinRankingList = coinRankingRepository.findAll();
		return new ResponseEntity<>(coinRankingList, HttpStatus.OK);
	}

}
