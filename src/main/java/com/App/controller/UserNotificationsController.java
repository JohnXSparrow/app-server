package com.App.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.model.UserLogin;
import com.App.model.UserNotifications;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping("/user/notifications")
public class UserNotificationsController {
	
	@Autowired
	UserLoginRepository userLoginRepository;
	
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid UserNotifications userNotifications) {

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

		if (userLogin.getUser().getUserProfile().getUserNotifications() == null) {
			userLogin.getUser().getUserProfile().setUserNotifications(userNotifications);
		} else {
			userLogin.getUser().getUserProfile().getUserNotifications().setMarketing(userNotifications.isMarketing());
			userLogin.getUser().getUserProfile().getUserNotifications().setNews(userNotifications.isNews());
			userLogin.getUser().getUserProfile().getUserNotifications().setOponentFound(userNotifications.isOponentFound());
			userLogin.getUser().getUserProfile().getUserNotifications().setMarketing(userNotifications.isMarketing());
		}

		userLoginRepository.save(userLogin);	
		
		return new ResponseEntity<>(userLogin.getUser().getUserProfile().getUserNotifications(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getUserInformation() {

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
		
		if (userLogin.getUser().getUserProfile().getUserNotifications() == null) {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}

		return new ResponseEntity<>(userLogin.getUser().getUserProfile().getUserNotifications(), HttpStatus.OK);
	}

}
