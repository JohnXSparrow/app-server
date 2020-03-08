package com.App.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.Kick;
import com.App.model.UserLogin;
import com.App.repository.AppAccessRepository;
import com.App.repository.KickRepository;
import com.App.repository.OauthAccessTokenRepository;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserMarketingRespository;
import com.App.repository.UserRepository;
import com.App.utils.CPFValidator;
import com.App.utils.EmailMarketingAsync;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
@RestController
@RequestMapping("/adm")
public class AdmController {

	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	KickRepository kickRepository;

	@Autowired
	UserMarketingRespository userMarketingRespository;

	@Autowired
	OauthAccessTokenRepository oauthAccessTokenRepository;
	
	@Autowired
	AppAccessRepository appAccessRepository;

	@Autowired
	MailClient mailClient;
	
	@Autowired
	EmailMarketingAsync emailMarketingAsync;

	@Value("${url.site}")
	private String urlsite;

	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
	@RequestMapping(value = "/countTotalUser", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> countTotal() {

		return new ResponseEntity<>(userLoginRepository.count(), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
	@RequestMapping(value = "/countActiveUser", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> countActive() {

		return new ResponseEntity<>(userLoginRepository.countUserActive(UserStatusEnum.ACTIVE), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
	@RequestMapping(value = "/countLoggedUser", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> countLogged() {

		return new ResponseEntity<>(userLoginRepository.countLoggedUser(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/findUser", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> replySupport(@RequestParam(value = "param", required = true) String param) {
				
		UserLogin userLogin;
		
		if (param.contains("@")) {
			userLogin = userLoginRepository.findOneByEmail(param);
			if (userLogin == null) {
				return new ResponseEntity<>(new GenericReturnMessage(59, "E-mail não está cadastrado no App"),
						HttpStatus.BAD_REQUEST);
			}
		} else {
			userLogin = userLoginRepository.findByUsername(param);
			if (userLogin == null) {
				return new ResponseEntity<>(new GenericReturnMessage(58, "Nome de usuário não encontrado"),
						HttpStatus.BAD_REQUEST);
			}
		}
		
		userLogin.getUser().setDateBirth(null);
		userLogin.getUser().setDateRegister(null);
		userLogin.getUser().getUserProfile().setUserInformation(null);
		userLogin.getUser().getUserProfile().setFavoriteTeam(null);
		userLogin.getUser().getUserProfile().setUserStats(null);
		userLogin.getUser().getUserProfile().setUserNotifications(null);
		userLogin.setTokenCode(null);
		userLogin.setPassword(null);
		userLogin.setUserRole(null);
		
		GsonBuilder gsonBuilder = new GsonBuilder();   
		Gson gson = gsonBuilder.create();
			
		return new ResponseEntity<>(gson.toJson(userLogin), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> updateUser(@RequestBody UserLogin userLoginUpdate) {
		
		UserLogin userLogin = userLoginRepository.findById(userLoginUpdate.getId_userlogin()).orElse(null);
		
		if (!userLoginUpdate.getUser().getEmail().equals(userLogin.getUser().getEmail())) {
			if (userRepository.findByEmail(userLoginUpdate.getUser().getEmail()) != null) {
				return new ResponseEntity<>(new GenericReturnMessage(43, "E-mail já registrado. Tente novamente"),
						HttpStatus.BAD_REQUEST);
			} else {
				userLogin.getUser().setEmail(userLoginUpdate.getUser().getEmail());
			}
		}		
		
		if (userLoginUpdate.getUser().getCpf() != null) {
			if (!userLoginUpdate.getUser().getCpf().equals(userLogin.getUser().getCpf())) {
				if (userRepository.findByCPF(userLoginUpdate.getUser().getCpf()) == null) {					
					if (CPFValidator.isValidCPF(userLoginUpdate.getUser().getCpf())) {
						userLogin.getUser().setCpf(userLoginUpdate.getUser().getCpf());
					} else {
						return new ResponseEntity<>(new GenericReturnMessage(65, "CPF inválido"),
								HttpStatus.BAD_REQUEST);
					}
				} else {
					return new ResponseEntity<>(new GenericReturnMessage(65, "CPF já está sendo utilizado"),
							HttpStatus.BAD_REQUEST);
				}
			}
		}
			
		userLogin.setUserStatus(userLoginUpdate.getUserStatus());
		userLogin.getUser().setFirstName(userLoginUpdate.getUser().getFirstName());
		userLogin.getUser().setLastName(userLoginUpdate.getUser().getLastName());
		userLogin.getUser().getUserProfile().getUserCoin().setGoldCoin(userLoginUpdate.getUser().getUserProfile().getUserCoin().getGoldCoin());
		userLogin.getUser().getUserProfile().getUserCoin().setSilverCoin(userLoginUpdate.getUser().getUserProfile().getUserCoin().getSilverCoin());
		userLogin.getUser().getUserProfile().setProfilePlan(userLoginUpdate.getUser().getUserProfile().getProfilePlan());
		
		userLoginRepository.save(userLogin);
		
		return new ResponseEntity<>(new GenericReturnMessage(0, "Salvo com Sucesso"), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/resendEmailUserRegister", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> resendEmailUserRegister() {

		Collection<UserLogin> usersLogin = userLoginRepository.findUsersByStatus(UserStatusEnum.WAITING);

		int x = 1;
		if (usersLogin != null) {
			if (usersLogin.size() > 0) {
				for (UserLogin userLogin : usersLogin) {

					MailConstrutorBean mailBean = new MailConstrutorBean();
					mailBean.setFirstName(userLogin.getUser().getFirstName());
					mailBean.setRecipient(userLogin.getUser().getEmail());
					mailBean.setSubject(userLogin.getUser().getFirstName() + ", Confirme sua Conta");
					mailBean.setMessage("Bem vindo ao Site!" + "<br/>" + "Seu nome de usuário é: " + "<strong>"	+ userLogin.getUsername() + "</strong><br/>" + " Para confirmar sua conta, clique no botão que segue logo abaixo: ");
					mailBean.setLink(urlsite + "/confirmAccount?tokenCode=" + userLogin.getTokenCode() + "&auth=" + userLogin.getId_userlogin());
					mailBean.setButtonTitle("Confirmar Conta");
					mailBean.setTemplateEngine("defaultMail");
					mailClient.prepareAndSendMail(mailBean);

					x++;
				}
			}
		}

		return new ResponseEntity<>(new GenericReturnMessage(0, "Foram enviados " + x + " e-mails."), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/token/revoke/{username:.*}", method = RequestMethod.GET)
	public ResponseEntity<Object> admRevokeToken(@PathVariable String username) {

		if (!deleteToken(username)) {
			return new ResponseEntity<>(new GenericReturnMessage(36, "Bad Credentials"), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(new GenericReturnMessage(0, "Token de Acesso Revogado"), HttpStatus.OK);
	}

	public boolean deleteToken(String username) {
		try {
			oauthAccessTokenRepository.delete(oauthAccessTokenRepository.findByUsername(username));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
		
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/appMarketing", method = RequestMethod.GET)
	public ResponseEntity<Object> marketingApp() {

		emailMarketingAsync.sendEmailMarketingAppNotUser();

		return new ResponseEntity<>(new GenericReturnMessage(0, "Processo Iniciado"), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/sorte", method = RequestMethod.GET)
	public ResponseEntity<Object> sorte() {

		Collection<UserLogin> users = userLoginRepository.findAll();
		Random r = new Random();
		long result = r.nextInt(users.size()) + 1;

		UserLogin userChoise = null;
		for (UserLogin user : users) {
			if (user.getId_userlogin() == result) {
				userChoise = user;
			}
		}

		userChoise.getUser().getUserProfile().getUserCoin().setGoldCoin(userChoise.getUser().getUserProfile().getUserCoin().getGoldCoin() + 500L);
		userLoginRepository.save(userChoise);

		return new ResponseEntity<>(userChoise, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/setcoins", method = RequestMethod.GET)
	public ResponseEntity<Object> setCoinByGameMatch(@PathVariable String gameMatch, @PathVariable String value) {
		
		Collection<Kick> kicks = kickRepository.findAllByGameMatch(Long.parseLong(gameMatch), true);
		
		for(Kick kick : kicks) {
			kick.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin() + Long.parseLong(value));
		}
		
		kickRepository.saveAll(kicks);
		
		return new ResponseEntity<>(new GenericReturnMessage(0, "OK"), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/appaccess", method = RequestMethod.GET)
	public ResponseEntity<Object> getAppAccess() {

		Calendar start = Calendar.getInstance();
		start.add(Calendar.DAY_OF_MONTH, -7);
		
		return new ResponseEntity<>(appAccessRepository.findByLastAccess(start, Calendar.getInstance()), HttpStatus.OK);
	}

}
