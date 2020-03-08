package com.App.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.model.Team;
import com.App.model.UserLogin;
import com.App.model.UserProfile;
import com.App.repository.TeamRepository;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping("/user/profile")
public class UserProfileController {
	
	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> userProfile(@PathVariable String username) {
		
		if (username.length() < 4 ||  username.length() > 13) {
			return new ResponseEntity<>(new GenericReturnMessage(110, "Nome de usuário inválido"), HttpStatus.BAD_REQUEST);
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
		
		UserProfile userProfile = new UserProfile();		
		if (!authentication.getName().equalsIgnoreCase(username)) {
			userLogin = userLoginRepository.findByUsername(username);			
			if (userLogin == null) {
				return new ResponseEntity<>(new GenericReturnMessage(110, "Nome de usuário inválido"), HttpStatus.BAD_REQUEST);
			}
			userProfile = userLogin.getUser().getUserProfile();
			userProfile.setUsername(userLogin.getUsername());
			userProfile.setUserCoin(null);
			userProfile.setUserInformation(null);
			userProfile.setUserNotifications(null);
		} else {
			userProfile = userLogin.getUser().getUserProfile();
			userProfile.setUsername(userLogin.getUsername());
			userProfile.setUserInformation(null);
			userProfile.setUserNotifications(null);
		}		

		return new ResponseEntity<>(userProfile, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/findTeam", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> findAll(@RequestParam(value = "team", required = true) String nameTeam) {	
		
		if (nameTeam == null || nameTeam.length() <= 0) {
			return new ResponseEntity<>(new GenericReturnMessage(71, "Nome Inválido"), HttpStatus.BAD_REQUEST);
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
		
		Collection<Team> foundTeams = teamRepository.findAllByName(nameTeam);
		
		return new ResponseEntity<>(foundTeams, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateTeam/{idTeam}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> findAll(@PathVariable Long idTeam) {		
		
		if (idTeam == null || idTeam <= 0) {
			return new ResponseEntity<>(new GenericReturnMessage(71, "Time Inválido"), HttpStatus.BAD_REQUEST);
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
		
		Team team = teamRepository.findById(idTeam).orElse(null);
		if (team == null) {
			return new ResponseEntity<>(new GenericReturnMessage(12, "Time não Encontrado."), HttpStatus.BAD_REQUEST);
		}
				
		if (userLogin.getUser().getUserProfile().getFavoriteTeam() == null) {
			userLogin.getUser().getUserProfile().getUserCoin().setSilverCoin(userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin() + 500);
		}
		userLogin.getUser().getUserProfile().setFavoriteTeam(team);
		userLoginRepository.saveAndFlush(userLogin);
				
		return new ResponseEntity<>(new GenericReturnMessage(00, "Time foi Salvo."), HttpStatus.OK);
	}

}
