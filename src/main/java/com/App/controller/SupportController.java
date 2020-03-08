package com.App.controller;

import java.util.Collection;
import java.util.regex.Pattern;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.Support;
import com.App.model.UserLogin;
import com.App.repository.SupportRepository;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping("/support")
public class SupportController {

	Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}.[a-z]{0,2}$", Pattern.CASE_INSENSITIVE);

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	SupportRepository supportRepository;

	@Autowired
	MailClient mailClient;

	@RequestMapping(value = "/user", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> addLogged(@RequestBody @Valid Support support, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
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
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName("Adm");
		mailBean.setRecipient("email");
		mailBean.setSubject("Pedido de Suporte");
		mailBean.setMessage("Um pedido de suporte foi feito por um usuário.");
		mailBean.setLink("url");
		mailBean.setButtonTitle("Administrador");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);

		support.setUserLogin(userLogin);
		Support supportSaved = supportRepository.save(support);

		return new ResponseEntity<>(supportSaved, HttpStatus.OK);
	}

	@RequestMapping(value = "/listOpened", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> listOpened() {

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

		Collection<Support> listSupport = supportRepository.findAllOpenedByUser(userLogin.getId_userlogin(), false);

		return new ResponseEntity<>(listSupport, HttpStatus.OK);
	}

	@RequestMapping(value = "/removeOfList", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> removeOfList(@RequestParam(value = "supportId", required = true) String supportId) {

		if (supportId.matches("^[0-9]*$") == false) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
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

		Support support = supportRepository.findOneByUserAndSupportId(userLogin.getId_userlogin(), Long.parseLong(supportId));		
		if (support == null) {
			return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de Confirmação"), HttpStatus.BAD_REQUEST);
		}
		
		if (support.getAnswer().equals("Aguardando uma resposta.")) {
			return new ResponseEntity<>(new GenericReturnMessage(78, "Aguarde uma resposta"), HttpStatus.BAD_REQUEST);
		}
		
		support.setClosed(true);
		supportRepository.save(support);

		return new ResponseEntity<>(new GenericReturnMessage(0, "Retirado da Lista"), HttpStatus.OK);
	}

	@RequestMapping(value = "/visitor", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> addNotLogged(
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "question", required = true) String question) {

		if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
			return new ResponseEntity<>(new GenericReturnMessage(69, "Digite um email válido"), HttpStatus.BAD_REQUEST);
		}

		if (question.trim().length() <= 0 || question.length() > 1500) {
			return new ResponseEntity<>(new GenericReturnMessage(70, "Digite sua pergunta e não ultrapasse 1.500 caracteres"),
					HttpStatus.BAD_REQUEST);
		}

		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName("Adm");
		mailBean.setRecipient("email");
		mailBean.setSubject("Pedido de Suporte");
		mailBean.setMessage("<strong>Pergunta do usuário: </strong><br>" + question	+ "<br><br><strong>Email para resposta ao usuário: </strong>" + email);
		mailBean.setLink("url");
		mailBean.setButtonTitle("Administrador");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);

		return new ResponseEntity<>(new GenericReturnMessage(0, "Em breve entraremos em contato com uma resposta."),
				HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/listAll", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> listAllSupport() {

		Collection<Support> listSupport = supportRepository.findAllOpened("Aguardando uma resposta.");

		return new ResponseEntity<>(listSupport, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/reply", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> replySupport(@RequestBody Support supportAnswer) {
		
		Support support = supportRepository.findById(supportAnswer.getId_support()).orElse(null);
		support.setAnswer(supportAnswer.getAnswer());
		
		supportRepository.save(support);
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(support.getUserLogin().getUser().getFirstName());
		mailBean.setRecipient(support.getUserLogin().getUser().getEmail());
		mailBean.setSubject("Resposta de Suporte");
		mailBean.setMessage("Sua pergunta de número " + support.getId_support() + " foi respondida, veja: ");
		mailBean.setLink("url");
		mailBean.setButtonTitle("Ver Resposta");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);

		return new ResponseEntity<>(support, HttpStatus.OK);
	}

}
