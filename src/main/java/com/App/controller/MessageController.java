package com.App.controller;

import java.util.Calendar;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.model.Message;
import com.App.model.UserLogin;
import com.App.repository.MessageRepository;
import com.App.repository.UserLoginRepository;

@Service
@RestController
@RequestMapping("/message")
public class MessageController {

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	MessageRepository messageRepository;

	@RequestMapping(value = "/listMessages", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listMessages() {
		
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
		end.add(Calendar.YEAR, 1);
		
		Collection<Message> listMessage = messageRepository.listMessages(start, end);

		return new ResponseEntity<>(listMessage, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Message message, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		messageRepository.save(message);
		return new ResponseEntity<>(new GenericReturnMessage(0, "Mensagem Adicionada"), HttpStatus.OK);
	}

}
