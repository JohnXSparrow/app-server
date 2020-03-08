package com.App.controller;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collection;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.PaymentStatusEnum;
import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.BuyCoin;
import com.App.model.PackageCoin;
import com.App.model.UserLogin;
import com.App.paypal.PayPalConection;
import com.App.paypal.PaypalPaymentDetails;
import com.App.paypal.PaypalRequestBean;
import com.App.paypal.PaypalResponseBean;
import com.App.repository.BuyCoinRepository;
import com.App.repository.PackageCoinRepository;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping(value = "/user/buyCoin")
public class UserBuyCoinController {

	private static final Logger LOG = LoggerFactory.getLogger(UserBuyCoinController.class.getName());

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	PackageCoinRepository packageCoinRepository;

	@Autowired
	BuyCoinRepository buyCoinRepository;

	@Autowired
	PayPalConection payPalClient;

	@Autowired
	MailClient mailClient;

	@Value("${url.site}")
	private String urlsite;

	@RequestMapping(value = "/paypal/buy", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> makePayment(@RequestBody @Valid BuyCoin buyCoin, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (buyCoin.getId_buycoin() != 0) {
			LOG.error("Processo proibido, você não pode fazer isso!");
			return new ResponseEntity<>(new GenericReturnMessage(25, "Processo proibido, você não pode fazer isso!"),
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

		PackageCoin packageCoin = packageCoinRepository.findById(buyCoin.getPackageCoin().getId_packagecoin()).orElse(null);;
		if (packageCoin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(13, "Pacote de Moedas não encontrado"),
					HttpStatus.BAD_REQUEST);
		}

		buyCoin.setUserLogin(userLogin);
		buyCoin.setPackageCoin(packageCoin);
		buyCoin.setStatus(PaymentStatusEnum.created);
		buyCoin.setDateBuy(Calendar.getInstance());

		PaypalRequestBean paypalRequestBean = payPalClient.createPayment(
				buyCoin.getPackageCoin().getNamePackage(), 
				String.valueOf(buyCoin.getPackageCoin().getTotalToPay()), 
				buyCoin.getUserLogin().getUser().getFirstName(),
				"buyCoin");
		if (paypalRequestBean == null) {
			return new ResponseEntity<>(new GenericReturnMessage(72, "Paypal Port Closed"), HttpStatus.BAD_REQUEST);
		}

		buyCoin.setPay_id(paypalRequestBean.getId_pay());
		buyCoinRepository.save(buyCoin);

		return new ResponseEntity<>(paypalRequestBean, HttpStatus.OK);
	}

	@RequestMapping(value = "/paypal/status", method = RequestMethod.GET)
	public ResponseEntity<Object> statusPaypal(
			@RequestParam(value = "paymentId", required = true) String paymentId,
			@RequestParam(value = "token", required = true) String token,
			@RequestParam(value = "PayerID", required = true) String payerID) {
		
		PaypalResponseBean paypalResponseBean = new PaypalResponseBean();

		paypalResponseBean.setPaymentId(paymentId);
		paypalResponseBean.setToken(token);
		paypalResponseBean.setPayerID(payerID);

		PaypalPaymentDetails paypalPaymentDetails = payPalClient.completePayment(paypalResponseBean);
		if (paypalPaymentDetails == null) {
			return new ResponseEntity<>(new GenericReturnMessage(73, "O pagamento já foi feito para essa compra."),
					HttpStatus.BAD_REQUEST);
		}

		if (paypalPaymentDetails.getStatus().equals(PaymentStatusEnum.approved.toString()) == false) {
			return new ResponseEntity<>(new GenericReturnMessage(74, "Pagamento não aprovado"), HttpStatus.BAD_REQUEST);
		} else if (paypalPaymentDetails.getStatus().equals(PaymentStatusEnum.approved.toString()) == true) {
			BuyCoin findBuyCoin = buyCoinRepository.findByPay_id(paypalPaymentDetails.getId_pay(),
					PaymentStatusEnum.created);
			if (findBuyCoin == null) {
				return new ResponseEntity<>(new GenericReturnMessage(75, "A solicitação de pagamento não existe"),
						HttpStatus.BAD_REQUEST);
			} else {
				if (findBuyCoin.getPackageCoin().getCoin().getId_coin() == 1) {
					findBuyCoin.getUserLogin().getUser().getUserProfile().getUserCoin()
							.setGoldCoin(findBuyCoin.getUserLogin().getUser().getUserProfile().getUserCoin().getGoldCoin()
									+ findBuyCoin.getPackageCoin().getAmountCoin());

				} else if (findBuyCoin.getPackageCoin().getCoin().getId_coin() == 2) {
					findBuyCoin.getUserLogin().getUser().getUserProfile().getUserCoin()
							.setSilverCoin(findBuyCoin.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin()
									+ findBuyCoin.getPackageCoin().getAmountCoin());
				}
				
				userLoginRepository.saveAndFlush(findBuyCoin.getUserLogin());
				
				findBuyCoin.setStatus(PaymentStatusEnum.approved);
				buyCoinRepository.save(findBuyCoin);
				
				MailConstrutorBean mailBean = new MailConstrutorBean();
				DecimalFormat money = new DecimalFormat("#,###,##0.00");
				mailBean.setFirstName(findBuyCoin.getUserLogin().getUser().getFirstName());
				mailBean.setRecipient(findBuyCoin.getUserLogin().getUser().getEmail());
				mailBean.setSubject("Compra de Moedas");
				mailBean.setMessage("Você comprou o pacote: <strong>" + findBuyCoin.getPackageCoin().getNamePackage()
						+ "</strong> no valor de R$ <strong>"
						+ money.format(findBuyCoin.getPackageCoin().getTotalToPay())
						+ "</strong> com sucesso!<br><br>As moedas já foram creditadas em sua carteira virtual.");
				mailBean.setLink(urlsite + "/login");
				mailBean.setButtonTitle("Entrar na Minha Conta");
				mailBean.setTemplateEngine("defaultMail");
				mailClient.prepareAndSendMail(mailBean);

			}
		}

		return new ResponseEntity<>(paypalPaymentDetails, HttpStatus.OK);
	}

	@RequestMapping(value = "/paypal/mobile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> makePaymentMobile(@RequestBody @Valid BuyCoin buyCoin, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (buyCoin.getId_buycoin() != 0) {
			LOG.error("Processo proibido, você não pode fazer isso!");
			return new ResponseEntity<>(new GenericReturnMessage(25, "Processo proibido, você não pode fazer isso!"),
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

		PackageCoin packageCoin = packageCoinRepository.findById(buyCoin.getPackageCoin().getId_packagecoin()).orElse(null);;
		if (packageCoin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(13, "Pacote de Moedas não encontrado"),
					HttpStatus.BAD_REQUEST);
		}

		PaypalPaymentDetails paypalPaymentDetails = payPalClient.verifyPayment(buyCoin.getPay_id());
		if (paypalPaymentDetails == null) {
			return new ResponseEntity<>(new GenericReturnMessage(75, "O pagamento não existe"), HttpStatus.BAD_REQUEST);
		}

		if (buyCoinRepository.findByPay_id(paypalPaymentDetails.getId_pay(), PaymentStatusEnum.approved) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(76, "Pagamento já foi confirmado"),
					HttpStatus.BAD_REQUEST);
		}

		if (paypalPaymentDetails.getStatus().equals(PaymentStatusEnum.approved.toString()) == false) {
			return new ResponseEntity<>(new GenericReturnMessage(77, "O pagamento não foi aprovado"),
					HttpStatus.BAD_REQUEST);
		}

		if (packageCoin.getTotalToPay().toString().equals(paypalPaymentDetails.getValuePayed()) == false) {
			return new ResponseEntity<>(new GenericReturnMessage(78, "Pacote não coincide com o comprado"),
					HttpStatus.BAD_REQUEST);
		}

		buyCoin.setDateBuy(Calendar.getInstance());
		buyCoin.setUserLogin(userLogin);
		buyCoin.setPackageCoin(packageCoin);
		buyCoin.setStatus(PaymentStatusEnum.approved);

		if (buyCoin.getPackageCoin().getCoin().getId_coin() == 1) {
			buyCoin.getUserLogin().getUser().getUserProfile().getUserCoin()
					.setGoldCoin(buyCoin.getUserLogin().getUser().getUserProfile().getUserCoin().getGoldCoin()
							+ buyCoin.getPackageCoin().getAmountCoin());

		} else if (buyCoin.getPackageCoin().getCoin().getId_coin() == 2) {
			buyCoin.getUserLogin().getUser().getUserProfile().getUserCoin()
					.setSilverCoin(buyCoin.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin()
							+ buyCoin.getPackageCoin().getAmountCoin());
		}

		userLoginRepository.saveAndFlush(buyCoin.getUserLogin());
		
		BuyCoin buyCoinSaved = buyCoinRepository.save(buyCoin);
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		DecimalFormat money = new DecimalFormat("#,###,##0.00");
		mailBean.setFirstName(buyCoin.getUserLogin().getUser().getFirstName());
		mailBean.setRecipient(buyCoin.getUserLogin().getUser().getEmail());
		mailBean.setSubject("Compra de Moedas");
		mailBean.setMessage("Você comprou o pacote: <strong>" + buyCoin.getPackageCoin().getNamePackage() 
				+ "</strong> no valor de R$ <strong>" + money.format(buyCoin.getPackageCoin().getTotalToPay()) 
				+ "</strong> com sucesso!<br><br>As moedas já foram creditadas em sua carteira virtual.");
		mailBean.setLink(urlsite + "/login");
		mailBean.setButtonTitle("Entrar na Minha Conta");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);

		return new ResponseEntity<>(buyCoinSaved, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public ResponseEntity<Object> listBuy() {

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

		Collection<BuyCoin> listUserBuyCoin = buyCoinRepository.findAllByUserLoginID(userLogin.getId_userlogin());

		return new ResponseEntity<>(listUserBuyCoin, HttpStatus.OK);
	}

}
