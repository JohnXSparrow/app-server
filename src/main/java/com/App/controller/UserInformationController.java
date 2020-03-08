package com.App.controller;

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
import com.App.model.UserInformation;
import com.App.model.UserLogin;
import com.App.repository.UserInformationRepository;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserRepository;

@RestController
@RequestMapping("/user/information")
public class UserInformationController {
	private static final Logger LOG = LoggerFactory.getLogger(UserInformationController.class.getName());

	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserInformationRepository userInformationRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid UserInformation userInformation, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldError(), HttpStatus.BAD_REQUEST);
		}

		if (userInformation.getId_userinformation() > 0 || userInformation.getId_userinformation() < 0) {
			LOG.error("Processo proibido, você não pode fazer isso!");
			return new ResponseEntity<>(new GenericReturnMessage(25, "Processo proibido, você não pode fazer isso!"), HttpStatus.BAD_REQUEST);
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

		if (userLogin.getUser().getUserProfile().getUserInformation() == null) {
			userLogin.getUser().getUserProfile().setUserInformation(userInformation);
			if (userLogin.getUser().getCpf() == null) {
				if (userRepository.findByCPF(userInformation.getCpf()) == null) {
					userLogin.getUser().setCpf(userInformation.getCpf());
				} else {
					return new ResponseEntity<>(new GenericReturnMessage(65, "CPF já está sendo utilizado"),
							HttpStatus.BAD_REQUEST);
				}
			} else {
				userInformation.setCpf(userLogin.getUser().getCpf());
			}
			
			userLogin.getUser().getUserProfile().setUserInformation(userInformation);			
			userRepository.save(userLogin.getUser());
			
			userInformation.setNomecompleto(userLogin.getUser().getFirstName() + " " + userLogin.getUser().getLastName());

		} else {
			UserInformation userInfoFound = userInformationRepository
					.findById(userLogin.getUser().getUserProfile().getUserInformation().getId_userinformation()).orElse(null);
			userInformation.setId_userinformation(userInfoFound.getId_userinformation());

			userLogin.getUser().getUserProfile().setUserInformation(userInformation);			
			userRepository.save(userLogin.getUser());
			userInformation.setNomecompleto(userLogin.getUser().getFirstName() + " " + userLogin.getUser().getLastName());
		}
		
		return new ResponseEntity<>(userInformation, HttpStatus.OK);
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

		UserInformation userInformation = null;
		if (userLogin.getUser().getUserProfile().getUserInformation() != null) {
			userInformation = userInformationRepository.findById(userLogin.getUser().getUserProfile().getUserInformation().getId_userinformation()).orElse(null);
			userInformation.setCpf(userLogin.getUser().getCpf());
			userInformation.setNomecompleto(userLogin.getUser().getFirstName() + " " + userLogin.getUser().getLastName());
		} else if (userLogin.getUser().getCpf() != null) {
			UserInformation noUserInformation = new UserInformation();
			noUserInformation.setCpf(userLogin.getUser().getCpf());
			noUserInformation.setNomecompleto(userLogin.getUser().getFirstName() + " " + userLogin.getUser().getLastName());
			return new ResponseEntity<>(noUserInformation, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}

		return new ResponseEntity<>(userInformation, HttpStatus.OK);
	}
}
