package com.App.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

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

import com.App.enumeration.PaymentStatusEnum;
import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.logic.UserDebitCoinLogic;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.Coin;
import com.App.model.RedeemMoney;
import com.App.model.UserLogin;
import com.App.repository.CoinRepository;
import com.App.repository.RedeemMoneyRepository;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping("/user/redeem/money")
public class UserRedeemMoneyController {
	private static final Logger LOG = LoggerFactory.getLogger(UserRedeemMoneyController.class.getName());

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	CoinRepository coinRepository;

	@Autowired
	RedeemMoneyRepository redeemMoneyRepository;

	@Autowired
	UserDebitCoinLogic userCoinLogic;
	
	@Autowired
	MailClient mailClient;

	@RequestMapping(value = "/request", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid RedeemMoney redeemMoney, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (redeemMoney.getId_redeemmoney() > 0 || redeemMoney.getId_redeemmoney() < 0) {
			LOG.error("Processo proibido, você não pode fazer isso!");
			return new ResponseEntity<>(new GenericReturnMessage(25, "Processo proibido, você não pode fazer isso!"),
					HttpStatus.BAD_REQUEST);
		}

		if (redeemMoney.getAmountCoin() < 400) {
			return new ResponseEntity<>(new GenericReturnMessage(47, "Mínimo para saque é de 400 moedas ouro. (R$ 20,00)"),
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
		
		if (userLogin.getUser().getUserProfile().getUserInformation() == null) {
			return new ResponseEntity<>(new GenericReturnMessage(46, "Você precisa cadastrar suas informações pessoais"),
					HttpStatus.BAD_REQUEST);
		}

		Coin goldCoin = coinRepository.findById(1L).orElse(null);
		boolean transaction = userCoinLogic.debitCoin(redeemMoney.getAmountCoin(), goldCoin, userLogin.getUser().getUserProfile().getUserCoin());
		if (transaction == false) {
			return new ResponseEntity<>(new GenericReturnMessage(31, "Saldo de Moeda " + goldCoin.getNameCoin() + " insuficiente"),
					HttpStatus.BAD_REQUEST);
		}

		redeemMoney.setTax(new BigDecimal(2.00));
		redeemMoney.setValueRedeem(new BigDecimal(redeemMoney.getAmountCoin() * goldCoin.getRealUnitvalue()).setScale(2, RoundingMode.HALF_EVEN));
		redeemMoney.setValueToReceive(redeemMoney.getValueRedeem().subtract(redeemMoney.getTax()));
		redeemMoney.setUserLogin(userLogin);
		redeemMoney.setStatus(PaymentStatusEnum.waiting);
		redeemMoney.setDateUpdateStatus(Calendar.getInstance());
		redeemMoney.setDateRequest(Calendar.getInstance());

		userLoginRepository.saveAndFlush(userLogin);
		RedeemMoney rds = redeemMoneyRepository.saveAndFlush(redeemMoney);
		
		DecimalFormat money = new DecimalFormat("#,###,##0.00");
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName(rds.getUserLogin().getUser().getFirstName());
		mailBean.setRecipient(rds.getUserLogin().getUser().getEmail());
		mailBean.setSubject("Resgate em Dinheiro");
		mailBean.setTotalMoney("R$ " + money.format(rds.getValueRedeem()));
		mailBean.setValueToReceive("R$ "+ money.format(rds.getValueToReceive()));
		mailBean.setTax("- R$ " + money.format(rds.getTax()));
		mailBean.setTemplateEngine("redeemMoneyMail");
		mailClient.prepareAndSendMail(mailBean);
			
		mailBean = new MailConstrutorBean();
		mailBean.setFirstName("Adm");
		mailBean.setRecipient("email");
		mailBean.setSubject("Pedido de Resgate - Dinheiro");
		mailBean.setMessage("<b>Nº:</b> " + rds.getId_redeemmoney() 
		+ "<br/> <b>Valor do resgate:</b> R$ " + money.format(rds.getValueToReceive()) 
		+ "<br/> <b>E-mail paypal:</b> " + rds.getEmailForPayment() 
		+ "<br/> <b>Nome de usuário:</b> " + rds.getUserLogin().getUsername()
		+ "<br/> <b>Nome:</b> " + rds.getUserLogin().getUser().getFirstName() + " " + rds.getUserLogin().getUser().getLastName()
		+ "<br/> <b>CPF:</b> " + rds.getUserLogin().getUser().getCpf()
		+ "<br/> <b>Data de nascimento:</b> " + new SimpleDateFormat("dd/MM/yyyy").format(rds.getUserLogin().getUser().getDateBirth().getTime()));
		mailBean.setLink("url");
		mailBean.setButtonTitle("Ver");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);

		return new ResponseEntity<>(rds, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/history", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listRedeemMoney() {

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

		Collection<RedeemMoney> listRedeemMoney = redeemMoneyRepository.findAllByUser(userLogin);

		return new ResponseEntity<>(listRedeemMoney, HttpStatus.OK);
	}

}
