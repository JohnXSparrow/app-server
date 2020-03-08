package com.App.controller;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.model.UserLogin;
import com.App.repository.UserLoginRepository;
import com.App.security.service.AccessToken;

@RestController
@RequestMapping("/login")
public class UserLoginController {

	@Autowired
	private UserLoginRepository userLoginRepository;

	@Autowired
	private DefaultTokenServices tokenServices;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private OAuth2RequestFactory defaultOAuth2RequestFactory;

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid UserLogin login, BindingResult bResult) throws Exception {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		UserLogin userLogin = userLoginRepository.findByUsername(login.getUsername());
		if (userLogin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(39, "Nome de Usuário ou Senha está incorreto."),
					HttpStatus.BAD_REQUEST);
		}

		if (!BCrypt.checkpw(login.getPassword(), userLogin.getPassword())) {
			return new ResponseEntity<>(new GenericReturnMessage(39, "Nome de Usuário ou Senha está incorreto."),
					HttpStatus.BAD_REQUEST);
		}

		if (userLogin.getUserStatus().equals(UserStatusEnum.WAITING)) {
			return new ResponseEntity<>(
					new GenericReturnMessage(40, "Você precisa confirmar sua conta. Verifique seu E-mail."),
					HttpStatus.UNAUTHORIZED);
		}

		if (userLogin.getUserStatus().equals(UserStatusEnum.BLOCKED)) {
			return new ResponseEntity<>(
					new GenericReturnMessage(41, "Sua conta foi bloqueada, para saber mais, entre em contato conosco."),
					HttpStatus.BAD_REQUEST);
		}

		if (userLogin.getUserStatus().equals(UserStatusEnum.DISABLED)) {
			return new ResponseEntity<>(
					new GenericReturnMessage(42, "Sua conta está desativada, para ativá-la, entre em contato conosco."),
					HttpStatus.UNAUTHORIZED);
		}

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", "api");
		parameters.put("client_secret", "secret");
		parameters.put("grant_type", "password");
		parameters.put("username", userLogin.getUsername());
		parameters.put("password", login.getPassword());
		parameters.put("scope", "read write");

		AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(parameters);
		authorizationRequest.setApproved(true);

		OAuth2Request oauth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);
				
		// Cria o principal e o auth token
		final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(userLogin.getUsername(), login.getPassword());		
		Authentication authentication = authenticationManager.authenticate(loginToken);		
		
		OAuth2Authentication authenticationRequest = new OAuth2Authentication(oauth2Request, authentication);
		authenticationRequest.setAuthenticated(true);

		OAuth2AccessToken accessToken = tokenServices.createAccessToken(authenticationRequest);

		return new ResponseEntity<>(new AccessToken(accessToken.getValue()), HttpStatus.OK);
	}

}
