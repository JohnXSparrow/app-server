package com.App.controller;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserRoleEnum;
import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.Register;
import com.App.model.UserLogin;
import com.App.model.UserProfile;
import com.App.recaptcha.RecaptchaService;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class UserRegisterController {

	private static final Logger LOG = LoggerFactory.getLogger(UserRegisterController.class.getName());

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	MailClient mailClient;
	
	@Autowired
	RecaptchaService recaptchaService;
	
	@Autowired
	AdmController admController;

	@Value("${url.site}")
	private String urlsite;;

	@Value("${codMobRegister}")
	private String codMobRegister;
	
	Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}.[a-z]{0,2}$", Pattern.CASE_INSENSITIVE);

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Register register, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldError(), HttpStatus.BAD_REQUEST);
		}

		// usuario nao pode setar um id, pois pode habilitar edição do cadastro
		if (register.getUserLogin().getUser().getId_user() != 0) {
			LOG.error("Processo proibido, você não pode fazer isso!");
			return new ResponseEntity<>(new GenericReturnMessage(25, "Processo proibido, você não pode fazer isso!"), HttpStatus.BAD_REQUEST);
		}

		if (userRepository.findByEmail(register.getUserLogin().getUser().getEmail()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(43, "E-mail já registrado. Tente novamente"),
					HttpStatus.BAD_REQUEST);
		}

		if (userLoginRepository.findByUsername(register.getUserLogin().getUsername()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(44, "Nome de usuário já registrado. Tente novamente"),
					HttpStatus.BAD_REQUEST);
		}

		if (!Objects.equals(register.getRecaptchaReactive(), codMobRegister)) {
			if (!recaptchaService.callRecaptcha(register.getRecaptchaReactive())) {
				return new ResponseEntity<>(new GenericReturnMessage(49, "Erro de Recaptcha"),
						HttpStatus.BAD_REQUEST);
			}
		}

		String passToEmail = register.getUserLogin().getPassword();

		register.getUserLogin().getUser().setUserProfile(new UserProfile());
		register.getUserLogin().getUser().setDateRegister(Calendar.getInstance());
		register.getUserLogin().setPassword(passwordEncoder.encode(register.getUserLogin().getPassword()));
		register.getUserLogin().setUserStatus(UserStatusEnum.WAITING);
		register.getUserLogin().setUserRole(UserRoleEnum.USER);
		register.getUserLogin().setTokenCode(UUID.randomUUID().toString().replace("-", "").substring(0, 20));

		UserLogin newUser = userLoginRepository.save(register.getUserLogin());
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(register.getUserLogin().getUser().getFirstName());
		mailBean.setRecipient(register.getUserLogin().getUser().getEmail());
		mailBean.setSubject("Site - Confirme sua Conta");
		mailBean.setMessage("Bem vindo ao Site!" + "<br/>" + "Seu nome de usuário é: " + "<strong>" + register.getUserLogin().getUsername()	+ "</strong><br/>" + "Sua senha é: " + "<strong>" + passToEmail + "</strong><br/><br>" + " Para confirmar sua conta, acesse o botão abaixo: ");
		mailBean.setLink(urlsite + "/confirmAccount?tk=" + register.getUserLogin().getTokenCode() + "&auth=" + newUser.getId_userlogin());
		mailBean.setButtonTitle("Confirmar Minha Conta");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);

		return new ResponseEntity<>(newUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/disableAccount", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> disableAccount(@RequestParam(value = "pass", required = true) String password) {

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

		if (!userLogin.getUserStatus().equals(UserStatusEnum.ACTIVE)) {
			return new ResponseEntity<>(new GenericReturnMessage(57, "Usuário não permitido"), HttpStatus.BAD_REQUEST);
		}
		
		if(!BCrypt.checkpw(password, userLogin.getPassword())) {
			return new ResponseEntity<>(new GenericReturnMessage(67, "A senha informada está incorreta"), HttpStatus.BAD_REQUEST);
		}

		userLogin.setUserStatus(UserStatusEnum.DISABLED);
		userLoginRepository.save(userLogin);
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(userLogin.getUser().getFirstName());
		mailBean.setRecipient(userLogin.getUser().getEmail());
		mailBean.setSubject("Site - Conta Desativada");
		mailBean.setMessage("Sua conta foi desativada como solicitado, caso queira ativar sua conta futuramente entre contado com nosso suporte no botão: ");
		mailBean.setLink(urlsite + "/support");
		mailBean.setButtonTitle("Entrar em contato");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);

		return new ResponseEntity<>(new GenericReturnMessage(0, "Sua conta foi desativada como solicitado, caso queira ativar sua conta futuramente entre contado conosco."),
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/resendEmail", method = RequestMethod.GET)
	public ResponseEntity<Object> resendEmail(
			@RequestParam(value = "email", required = true) String email) {
		
		if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
			return new ResponseEntity<>(new GenericReturnMessage(69, "Digite um email válido"), HttpStatus.BAD_REQUEST);
		}
		
		UserLogin userLogin = userLoginRepository.findOneByEmail(email);		
		if (userLogin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(59, "Este e-mail não está cadastrado no site"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (userLogin.getUserStatus().equals(UserStatusEnum.BLOCKED ) || userLogin.getUserStatus().equals(UserStatusEnum.DISABLED)) {
			return new ResponseEntity<>(new GenericReturnMessage(60, "Conta bloqueada ou desativada"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (userLogin.getUserStatus().equals(UserStatusEnum.ACTIVE)) {
			return new ResponseEntity<>(new GenericReturnMessage(55, "Conta já está ativa"),
					HttpStatus.BAD_REQUEST);
		}
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(userLogin.getUser().getFirstName());
		mailBean.setRecipient(userLogin.getUser().getEmail());
		mailBean.setSubject("site - Confirme sua Conta");
		mailBean.setMessage("Bem vindo ao site!" + "<br/>" + "Seu nome de usuário é: " + "<strong>" + userLogin.getUsername() + "</strong><br/>" + "Para confirmar sua conta, acesse o botão abaixo: ");
		mailBean.setLink(urlsite + "/confirmAccount?tk=" + userLogin.getTokenCode() + "&auth=" + userLogin.getId_userlogin());
		mailBean.setButtonTitle("Confirmar Minha Conta");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);		
		
		return new ResponseEntity<>(new GenericReturnMessage(0, "E-mail enviado. Caso não receba, verifique sua caixa de spam."),
				HttpStatus.OK);	
	}

	@RequestMapping(value = "/confirmAccount", method = RequestMethod.GET)
	public ResponseEntity<Object> confirmAccount(
			@RequestParam(value = "tk", required = true) String tokenCode,
			@RequestParam(value = "auth", required = true) String userLogin) {

		if (userLogin.matches("^[0-9]*$") == false || userLogin.length() < 1) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}
		
		UserLogin userLoginTokenFound = userLoginRepository.findById(Long.parseLong(userLogin)).orElse(null);
		if (userLoginTokenFound == null) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}
		
		if (!userLoginTokenFound.getTokenCode().contentEquals(tokenCode)) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}

		if (userLoginTokenFound.getUserStatus().equals(UserStatusEnum.ACTIVE)) {
			return new ResponseEntity<>(new GenericReturnMessage(55, "Conta já está ativa"),
					HttpStatus.BAD_REQUEST);
		}

		if (userLoginTokenFound.getUserStatus().equals(UserStatusEnum.BLOCKED) || userLoginTokenFound.getUserStatus().equals(UserStatusEnum.DISABLED)) {
			return new ResponseEntity<>(
					new GenericReturnMessage(56, "Conta não permitida, entre em contato com o nosso suporte"),
					HttpStatus.BAD_REQUEST);
		}

		userLoginTokenFound.setTokenCode(UUID.randomUUID().toString().replace("-", "").substring(0, 20) + new Random().nextLong());
		userLoginTokenFound.setUserStatus(UserStatusEnum.ACTIVE);
		userLoginRepository.save(userLoginTokenFound);

		return new ResponseEntity<>(new GenericReturnMessage(0, "Sua conta foi confirmada com sucesso. Divirta-se!"), HttpStatus.OK);
	}

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> forgotPassword(@RequestParam(value = "email", required = true) String email) {
		
		if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
			return new ResponseEntity<>(new GenericReturnMessage(69, "Digite um email válido"), HttpStatus.BAD_REQUEST);
		}

		UserLogin userLogin = userLoginRepository.findOneByEmail(email);
		if (userLogin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(59, "E-mail não está cadastrado no site"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (userLogin.getUserStatus().equals(UserStatusEnum.BLOCKED ) || userLogin.getUserStatus().equals(UserStatusEnum.DISABLED)) {
			return new ResponseEntity<>(new GenericReturnMessage(60, "Conta bloqueada ou desativada"),
					HttpStatus.BAD_REQUEST);
		}

		userLogin.setTokenCode(UUID.randomUUID().toString().replace("-", "").substring(0, 20) + new Random().nextLong());
		userLoginRepository.save(userLogin);

		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(userLogin.getUser().getFirstName());
		mailBean.setRecipient(userLogin.getUser().getEmail());
		mailBean.setSubject("site - Reset de Senha");
		mailBean.setMessage("Uma nova senha foi solicitada. Acesse o botão abaixo e confirme seu pedido: ");
		mailBean.setLink(urlsite + "/resetForgottenPassword?tk=" + userLogin.getTokenCode() + "&auth=" + userLogin.getId_userlogin());
		mailBean.setButtonTitle("Confirmar Pedido");		
		mailBean.setTemplateEngine("passwordMail");
		mailClient.prepareAndSendMail(mailBean);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode emailReturn = mapper.createObjectNode();
		emailReturn.put("email", userLogin.getUser().getEmail());

		return new ResponseEntity<>(emailReturn, HttpStatus.OK);
	}

	@RequestMapping(value = "/resetForgottenPassword", method = RequestMethod.GET)
	public ResponseEntity<Object> resetForgottenPassword(
			@RequestParam(value = "tk", required = true) String tokenCode,
			@RequestParam(value = "auth", required = true) String userLoginID,
			@RequestParam(value = "np", required = true) String newPassword) {

		if (userLoginID.matches("^[0-9]*$") == false || userLoginID.length() < 1) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}

		if (newPassword.isEmpty() || newPassword == null) {
			return new ResponseEntity<>(new GenericReturnMessage(61, "Defina uma nova senha valida"), HttpStatus.BAD_REQUEST);
		}

		if (newPassword.length() < 6) {
			return new ResponseEntity<>(new GenericReturnMessage(62, "Senha deve conter 6 ou mais caracteres"),
					HttpStatus.BAD_REQUEST);
		}

		UserLogin userLogin = userLoginRepository.confirmAccount(Long.parseLong(userLoginID), tokenCode);
		if (userLogin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}

		if (!userLogin.getUserStatus().equals(UserStatusEnum.ACTIVE)) {
			return new ResponseEntity<>(new GenericReturnMessage(60, "Sua conta deve estar ativa"),
					HttpStatus.BAD_REQUEST);
		}

		admController.deleteToken(userLogin.getUsername());
		userLogin.setPassword(passwordEncoder.encode(newPassword));
		userLogin.setTokenCode(UUID.randomUUID().toString().replace("-", "").substring(0, 20) + new Random().nextLong());
		userLoginRepository.save(userLogin);
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(userLogin.getUser().getFirstName());
		mailBean.setRecipient(userLogin.getUser().getEmail());
		mailBean.setSubject("site - Senha Alterada");
		mailBean.setMessage("Segue seu nome de usuário: <strong>" + userLogin.getUsername() + "</strong><br/>" + "Segue sua nova senha: " + "<strong>" + newPassword + "</strong><br/>" + " Agora vamos jogar, acesse sua conta: ");
		mailBean.setLink(urlsite + "/login");
		mailBean.setButtonTitle("Acessar Minha Conta");
		mailBean.setTemplateEngine("passwordMail");
		mailClient.prepareAndSendMail(mailBean);

		return new ResponseEntity<>(new GenericReturnMessage(0, "Senha alterada com sucesso."), HttpStatus.OK);
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> changePassword(
			@RequestParam(value = "aPass", required = true) String atualPassword, 
			@RequestParam(value = "nPass", required = true) String newPassword) {
		
		if (newPassword.isEmpty() || newPassword == null) {
			return new ResponseEntity<>(new GenericReturnMessage(61, "Defina uma nova senha valida"), HttpStatus.BAD_REQUEST);
		}

		if (newPassword.length() < 6) {
			return new ResponseEntity<>(new GenericReturnMessage(62, "Senha deve conter 6 ou mais caracteres"),
					HttpStatus.BAD_REQUEST);
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
		if (!userLogin.getUserStatus().equals(UserStatusEnum.ACTIVE)) {
			return new ResponseEntity<>(new GenericReturnMessage(60, "Sua conta deve estar ativa"),
					HttpStatus.BAD_REQUEST);
		}
		
		if(!BCrypt.checkpw(atualPassword, userLogin.getPassword())) {
			return new ResponseEntity<>(new GenericReturnMessage(68, "A senha atual informada está incorreta"), HttpStatus.BAD_REQUEST);
		}
		
		admController.deleteToken(userLogin.getUsername());
		userLogin.setPassword(passwordEncoder.encode(newPassword));
		userLoginRepository.save(userLogin);
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(userLogin.getUser().getFirstName());
		mailBean.setRecipient(userLogin.getUser().getEmail());
		mailBean.setSubject("site - Senha Alterada");
		mailBean.setMessage("Segue sua nova senha: " + "<strong>" + newPassword + "</strong><br/>" + " Agora vamos jogar, acesse sua conta: ");
		mailBean.setLink(urlsite + "/login");
		mailBean.setButtonTitle("Acessar Minha Conta");
		mailBean.setTemplateEngine("passwordMail");
		mailClient.prepareAndSendMail(mailBean);
		
		return new ResponseEntity<>(new GenericReturnMessage(0, "Senha alterada com sucesso."), HttpStatus.OK);
	}

}
