package com.App.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Collections;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.PaymentStatusEnum;
import com.App.enumeration.ProfilePlanEnum;
import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.BuyPlanGP;
import com.App.model.BuyPlanPP;
import com.App.model.UserLogin;
import com.App.paypal.PayPalConection;
import com.App.paypal.PaypalPaymentDetails;
import com.App.paypal.PaypalRequestBean;
import com.App.paypal.PaypalResponseBean;
import com.App.repository.BuyPlanGPRepository;
import com.App.repository.BuyPlanPPRepository;
import com.App.repository.UserLoginRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;

@RestController
@RequestMapping(value = "/user/buyPlan")
public class UserBuyPlanController {
	
	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	BuyPlanGPRepository buyPlanGPRepository;
	
	@Autowired
	BuyPlanPPRepository buyPlanPPRepository;
	
	@Autowired
	PayPalConection payPalClient;
	
	@Autowired
	MailClient mailClient;

	@RequestMapping(value = "/check", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> buyPlanCheck(@RequestBody BuyPlanGP responseBuyPlan)
			throws GeneralSecurityException, IOException {

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

		InputStream resourceAsStream = UserBuyPlanController.class.getClassLoader().getResourceAsStream("purchase.json");
		GoogleCredential credential = GoogleCredential.fromStream(resourceAsStream).createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));

		credential.refreshToken();

		HttpClient client = HttpClients.custom().build();
		HttpUriRequest request = RequestBuilder
				.get("url"
				+ responseBuyPlan.getProductId() + "/tokens/" + responseBuyPlan.getPurchaseToken())
				.addHeader("Authorization", "Bearer " + credential.getAccessToken()).build();

		JsonNode jsonResponse = new ObjectMapper().readTree(EntityUtils.toString(client.execute(request).getEntity(), "UTF-8"));

		if (jsonResponse == null || jsonResponse.has("error")) {
			return new ResponseEntity<>(new GenericReturnMessage(99, "Erro ao confirmar a compra."),
					HttpStatus.BAD_REQUEST);
		}

		if (!jsonResponse.get("purchaseState").asText().equalsIgnoreCase("0")) {
			return new ResponseEntity<>(new GenericReturnMessage(100, "Essa compra não foi confirmada pelo Google Play."),
					HttpStatus.BAD_REQUEST);
		}

		if (!responseBuyPlan.getOrderId().equals(jsonResponse.get("orderId").asText())) {
			return new ResponseEntity<>(new GenericReturnMessage(101, "Essa compra não está relacionada ao pedido confirmado pelo Google Play."),
					HttpStatus.BAD_REQUEST);
		}
		
		if (buyPlanGPRepository.findByOrderId(responseBuyPlan.getOrderId()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(104, "Essa compra já foi processada."),
					HttpStatus.BAD_REQUEST);
		}
				
		if (responseBuyPlan.getProductId().contentEquals("com.App.app.consumable.campeao")) {
			userLogin.getUser().getUserProfile().getUserCoin().setGoldCoin(userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Campeao.getGoldCoin());
			userLogin.getUser().getUserProfile().getUserCoin().setSilverCoin(userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Campeao.getSilverCoin());
			if (userLogin.getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Campeao.getId()) {
				userLogin.getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Campeao);
			}
			sendMail(userLogin.getUsername(), ProfilePlanEnum.Campeao.getNamePlan());
		} else if (responseBuyPlan.getProductId().contentEquals("com.App.app.consumable.artilheiro")) {
			userLogin.getUser().getUserProfile().getUserCoin().setGoldCoin(userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Artilheiro.getGoldCoin());
			userLogin.getUser().getUserProfile().getUserCoin().setSilverCoin(userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Artilheiro.getSilverCoin());
			if (userLogin.getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Artilheiro.getId()) {
				userLogin.getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Artilheiro);
			}
			sendMail(userLogin.getUsername(), ProfilePlanEnum.Artilheiro.getNamePlan());
		} else if (responseBuyPlan.getProductId().contentEquals("com.App.app.consumable.atacante")) {
			userLogin.getUser().getUserProfile().getUserCoin().setGoldCoin(userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Atacante.getGoldCoin());
			userLogin.getUser().getUserProfile().getUserCoin().setSilverCoin(userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Atacante.getSilverCoin());
			if (userLogin.getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Atacante.getId()) {
				userLogin.getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Atacante);
			}
			sendMail(userLogin.getUsername(), ProfilePlanEnum.Atacante.getNamePlan());
		} else if (responseBuyPlan.getProductId().contentEquals("com.App.app.consumable.armador")) {
			userLogin.getUser().getUserProfile().getUserCoin().setGoldCoin(userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Armador.getGoldCoin());
			userLogin.getUser().getUserProfile().getUserCoin().setSilverCoin(userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Armador.getSilverCoin());
			if (userLogin.getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Armador.getId()) {
				userLogin.getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Armador);
			}
			sendMail(userLogin.getUsername(), ProfilePlanEnum.Armador.getNamePlan());
		} else if (responseBuyPlan.getProductId().contentEquals("com.App.app.consumable.moedaouro")) {
			userLogin.getUser().getUserProfile().getUserCoin().setGoldCoin(userLogin.getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.MoedaOuro.getGoldCoin());
			sendMail(userLogin.getUsername(), ProfilePlanEnum.MoedaOuro.getNamePlan());
		} else if (responseBuyPlan.getProductId().contentEquals("com.App.app.consumable.moedaprata")) {
			userLogin.getUser().getUserProfile().getUserCoin().setSilverCoin(userLogin.getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.MoedaPrata.getSilverCoin());
			sendMail(userLogin.getUsername(), ProfilePlanEnum.MoedaPrata.getNamePlan());
		} else {
			return new ResponseEntity<>(new GenericReturnMessage(102, "Plano inválido."), HttpStatus.BAD_REQUEST);
		}
		
		responseBuyPlan.setUserLogin(userLogin);
		buyPlanGPRepository.saveAndFlush(responseBuyPlan);
	
		return new ResponseEntity<>(new GenericReturnMessage(00, "Compra efetuada com sucesso."), HttpStatus.OK);
	}
			
		@RequestMapping(value = "/paypal/buy", method = RequestMethod.GET)
		public ResponseEntity<Object> makePayment(@RequestParam(value = "plan", required = true) String plan) {
		
		int planId;
		if (plan.matches("^[0-9]*$") == false || plan.length() < 1) {
				return new ResponseEntity<>(new GenericReturnMessage(45, "Erro de confirmação"), HttpStatus.BAD_REQUEST);
		} else {
			planId = Integer.parseInt(plan);
		}		
		
		if (planId <= 0 || planId  > 6) {
			return new ResponseEntity<>(new GenericReturnMessage(102, "Plano inválido."), HttpStatus.BAD_REQUEST);
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
		
		BuyPlanPP buyPlanPP = new BuyPlanPP();
		buyPlanPP.setProfilePlan(ProfilePlanEnum.getProfilePlanById(planId));
		buyPlanPP.setUserLogin(userLogin);
		buyPlanPP.setStatus(PaymentStatusEnum.created);
		buyPlanPP.setDateBuy(Calendar.getInstance());

		PaypalRequestBean paypalRequestBean = payPalClient.createPayment(
				buyPlanPP.getProfilePlan().getNamePlan(), 
				buyPlanPP.getProfilePlan().getPrice(), 
				buyPlanPP.getUserLogin().getUser().getFirstName(),
				"buyPlan");
		if (paypalRequestBean == null) {
			return new ResponseEntity<>(new GenericReturnMessage(72, "Paypal Port Closed"), HttpStatus.BAD_REQUEST);
		}

		buyPlanPP.setPay_id(paypalRequestBean.getId_pay());
		buyPlanPPRepository.save(buyPlanPP);

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
			
			BuyPlanPP foundBuyPlan = buyPlanPPRepository.findByPay_id(paypalPaymentDetails.getId_pay(),	PaymentStatusEnum.created);
			
			if (foundBuyPlan == null) {
				return new ResponseEntity<>(new GenericReturnMessage(75, "A solicitação de pagamento não existe"),
						HttpStatus.BAD_REQUEST);
			} else {			
				
				if (foundBuyPlan.getProfilePlan().equals(ProfilePlanEnum.Campeao)) {
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Campeao.getGoldCoin());
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Campeao.getSilverCoin());
					if (foundBuyPlan.getUserLogin().getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Campeao.getId()) {
						foundBuyPlan.getUserLogin().getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Campeao);
					}					
				} else if (foundBuyPlan.getProfilePlan().equals(ProfilePlanEnum.Artilheiro)) {
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Artilheiro.getGoldCoin());
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Artilheiro.getSilverCoin());
					if (foundBuyPlan.getUserLogin().getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Artilheiro.getId()) {
						foundBuyPlan.getUserLogin().getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Artilheiro);
					}
				} else if (foundBuyPlan.getProfilePlan().equals(ProfilePlanEnum.Atacante)) {
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Atacante.getGoldCoin());
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Atacante.getSilverCoin());
					if (foundBuyPlan.getUserLogin().getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Atacante.getId()) {
						foundBuyPlan.getUserLogin().getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Atacante);
					}
				} else if (foundBuyPlan.getProfilePlan().equals(ProfilePlanEnum.Armador)) {
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.Armador.getGoldCoin());
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.Armador.getSilverCoin());
					if (foundBuyPlan.getUserLogin().getUser().getUserProfile().getProfilePlan().getId() < ProfilePlanEnum.Armador.getId()) {
						foundBuyPlan.getUserLogin().getUser().getUserProfile().setProfilePlan(ProfilePlanEnum.Armador);
					}
				} else if(foundBuyPlan.getProfilePlan().equals(ProfilePlanEnum.MoedaOuro)) {
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getGoldCoin() + ProfilePlanEnum.MoedaOuro.getGoldCoin());
				} else if(foundBuyPlan.getProfilePlan().equals(ProfilePlanEnum.MoedaPrata)) {
					foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(foundBuyPlan.getUserLogin().getUser().getUserProfile().getUserCoin().getSilverCoin() + ProfilePlanEnum.MoedaPrata.getSilverCoin());
				}
				
				userLoginRepository.saveAndFlush(foundBuyPlan.getUserLogin());
				
				foundBuyPlan.setStatus(PaymentStatusEnum.approved);
				buyPlanPPRepository.saveAndFlush(foundBuyPlan);
			}
		}

		return new ResponseEntity<>(paypalPaymentDetails, HttpStatus.OK);
	}
	
	public void sendMail(String username, String plan) {		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName("Adm");
		mailBean.setRecipient("email");
		mailBean.setSubject("Compra de Plano");
		mailBean.setMessage("Usuário: " + username + "<br>Plano: " + plan);
		mailBean.setLink("url/user/edit");
		mailBean.setButtonTitle("Administrador");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);
	}


}
