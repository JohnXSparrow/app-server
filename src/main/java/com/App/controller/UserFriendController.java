package com.App.controller;

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
import com.App.model.UserFriend;
import com.App.model.UserLogin;
import com.App.repository.UserFriendRepository;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping("/user/friend")
public class UserFriendController {
	
	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	UserFriendRepository userFriendRepository;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add( @RequestParam(value = "addFriend", required = true) String username) {
		
		if (username.length() < 4) {
			return new ResponseEntity<>(new GenericReturnMessage(58, "Amigo não encontrado"), HttpStatus.BAD_REQUEST);
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
		
		UserLogin friend = userLoginRepository.findByUsername(username);
		if (friend == null) {
			return new ResponseEntity<>(new GenericReturnMessage(58, "Amigo não encontrado"), HttpStatus.BAD_REQUEST);
		} else if  (username.equalsIgnoreCase(userLogin.getUsername())) {
			return new ResponseEntity<>(new GenericReturnMessage(93, "Gol contra não vale! Escolha um amigo."),	HttpStatus.BAD_REQUEST);
		}		
		
		UserFriend userFriend = new UserFriend();
		userFriend.setUserLogin(userLogin);
		userFriend.setFriend(friend);
		
		return new ResponseEntity<>(new GenericReturnMessage(00, "Amigo Adicionado."), HttpStatus.OK);
	}

}
