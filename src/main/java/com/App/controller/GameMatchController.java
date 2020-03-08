package com.App.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.model.GameMatch;
import com.App.model.Kick;
import com.App.model.UserLogin;
import com.App.push.PushNotification;
import com.App.repository.GameMatchRepository;
import com.App.repository.KickRepository;
import com.App.repository.TeamRepository;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserRepository;

@RestController
public class GameMatchController {

	@Autowired
	GameMatchRepository gameMatchRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	KickRepository kickRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Autowired
	PushNotification pushNotification;
	
	@RequestMapping(value = "/gamematch/list/public", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listGameMatchPublic() {
				
		Collection<GameMatch> listGameMatchs = gameMatchRepository.findListGameMatchOneWeek(false, false, true);
	
		return new ResponseEntity<>(listGameMatchs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/gamematch/list/logged", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listGameMatchLogged() {
		
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
		
		Collection<GameMatch> listGameMatchs = gameMatchRepository.findListGameMatchOneWeek(false, false, true);
		if (listGameMatchs.isEmpty()) {
			return new ResponseEntity<>(listGameMatchs, HttpStatus.OK);
		}
		
		Collection<Kick> listKicks = kickRepository.findKicksDoneGM(userLogin, false);

		if (!listKicks.isEmpty()) {
			List<GameMatch> found = new ArrayList<GameMatch>();
			for (Kick kick : listKicks) {
				for (GameMatch gameMatch : listGameMatchs) {
					if (gameMatch.getId_gamematch() == kick.getGameMatch().getId_gamematch()) {
						Collection<Kick> specificKick = kickRepository.findKicksRepet(gameMatch.getId_gamematch(), userLogin);
						if (specificKick.size() == 2) {
							found.add(gameMatch);
						}
					}
				}
			}
			listGameMatchs.removeAll(found);
		}

		return new ResponseEntity<>(listGameMatchs, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
	@RequestMapping(value = "/adm/gamematch/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid GameMatch gameMatch, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldError(), HttpStatus.BAD_REQUEST);
		}

		if (gameMatch.getTeamA().getId_team() == gameMatch.getTeamB().getId_team()) {
			return new ResponseEntity<>(new GenericReturnMessage(19, "As equipes não podem jogar entre si"),
					HttpStatus.BAD_REQUEST);
		}

		Collection<GameMatch> combination = gameMatchRepository.findGameMatchExist(gameMatch.getTeamA(),
				gameMatch.getTeamB(), gameMatch.getStartTime().getTime());

		if (combination.size() > 0) {
			return new ResponseEntity<>(new GenericReturnMessage(20, "As equipes já estão em uma partida nesta data"),
					HttpStatus.BAD_REQUEST);
		}

		Calendar endGame = (Calendar) gameMatch.getStartTime().clone();
		endGame.add(Calendar.MINUTE, 45);
		endGame.add(Calendar.MINUTE, 45);
		endGame.add(Calendar.MINUTE, 25);
		gameMatch.setEndTime(endGame);		
		gameMatch.setDateTime(gameMatch.getStartTime().getTime());
		
		if (gameMatch.getNotify() == true) {
			gameMatch.setIsWeek(true);
			pushNotification.sendPushGameTopAdd(teamRepository.findById(gameMatch.getTeamA().getId_team()).orElse(null).getNameTeam(), teamRepository.findById(gameMatch.getTeamB().getId_team()).orElse(null).getNameTeam());			
		}
		
		GameMatch gameMatchAdd = gameMatchRepository.saveAndFlush(gameMatch);
		
		return new ResponseEntity<>(gameMatchAdd, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/adm/gamematch/edit", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> edit(@RequestBody @Valid GameMatch gameMatch, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldError(), HttpStatus.BAD_REQUEST);
		}		
		
		if (gameMatch.getId_gamematch() < 0 || gameMatch.getId_gamematch() == 0) {
			return new ResponseEntity<>(new GenericReturnMessage(72, "ID deve ser especificada!"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (gameMatch.getTeamA().getId_team() == gameMatch.getTeamB().getId_team()) {
			return new ResponseEntity<>(new GenericReturnMessage(19, "As equipes não podem jogar entre si"),
					HttpStatus.BAD_REQUEST);
		}
		
		GameMatch editGameMatch = gameMatchRepository.findById(gameMatch.getId_gamematch()).orElse(null);;		

		Calendar endGame = (Calendar) gameMatch.getStartTime().clone();
		endGame.add(Calendar.MINUTE, 45);
		endGame.add(Calendar.MINUTE, 45);
		endGame.add(Calendar.MINUTE, 25);
		gameMatch.setEndTime(endGame);		
		gameMatch.setDateTime(gameMatch.getStartTime().getTime());	
		
		gameMatch.setStadium(editGameMatch.getStadium());
		gameMatch.setTourn(editGameMatch.getTourn());
		gameMatch.setTeamA(editGameMatch.getTeamA());
		gameMatch.setTeamB(editGameMatch.getTeamB());
		
		GameMatch gameMatchAdd = gameMatchRepository.save(gameMatch);
		
		return new ResponseEntity<>(gameMatchAdd, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
	@RequestMapping(value = "/adm/gamematch/list", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listMatchAdm() {

		Collection<GameMatch> listGameMatchs = gameMatchRepository.findListGameMatch(false, false);

		return new ResponseEntity<>(listGameMatchs, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
	@RequestMapping(value = "/adm/gamematch/result", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listResult() {

		Collection<GameMatch> listGameMatchs = gameMatchRepository.findListGameMatchByIsSetResult(true, false);

		return new ResponseEntity<>(listGameMatchs, HttpStatus.OK);
	}

}
