package com.App.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.App.enumeration.KickFriendEnum;
import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.logic.UserDebitCoinLogic;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.Coin;
import com.App.model.GameMatch;
import com.App.model.KickFriend;
import com.App.model.PushNotificationQueue;
import com.App.model.RankingPotCoin;
import com.App.model.UserLogin;
import com.App.push.PushNotification;
import com.App.repository.CoinRepository;
import com.App.repository.GameMatchRepository;
import com.App.repository.KickFriendRepository;
import com.App.repository.PushNotificationQueueRepository;
import com.App.repository.RankingPotCoinRepository;
import com.App.repository.UserLoginRepository;

@RestController
@RequestMapping("/user/kickfriend")
public class UserKickFriendController {
	private static final Logger LOG = LoggerFactory.getLogger(UserKickFriendController.class.getName());

	@Autowired
	UserLoginRepository userLoginRepository;
	
	@Autowired
	GameMatchRepository gameMatchRepository;
	
	@Autowired
	CoinRepository coinRepository;
	
	@Autowired
	KickFriendRepository kickFriendRepository;
	
	@Autowired
	RankingPotCoinRepository coinRankingRepository;
	
	@Autowired
	PushNotificationQueueRepository pushNotificationQueueRepository;
	
	@Autowired
	UserDebitCoinLogic userCoinLogic;

	@Autowired
	MailClient mailClient;
	
	@Autowired
	PushNotification pushNotification;
	
	@RequestMapping(value = "/challenge", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid KickFriend kickFriend, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (kickFriend.getId_kickfriend() != 0 ) {
			LOG.error("Processo proibido, você não pode fazer isso!");
			return new ResponseEntity<>(new GenericReturnMessage(25, "Processo proibido, você não pode fazer isso!"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (kickFriend.getCoin().getId_coin() != 1L) {
			return new ResponseEntity<>(new GenericReturnMessage(86, "Tipo de Moeda não disponível para esse modo de jogo."),
					HttpStatus.BAD_REQUEST);
		}

		if (kickFriend.getValueKick() != 50 
				&& kickFriend.getValueKick() != 100 
				&& kickFriend.getValueKick() != 250 
				&& kickFriend.getValueKick() != 500
				&& kickFriend.getValueKick() != 1000
				&& kickFriend.getValueKick() != 2500
				&& kickFriend.getValueKick() != 5000
				&& kickFriend.getValueKick() != 10000) {
			return new ResponseEntity<>(new GenericReturnMessage(27, "Valor do chute deve ser válido"),
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
		
		if (kickFriend.getChallenged().getUsername().equalsIgnoreCase(userLogin.getUsername())) {
			return new ResponseEntity<>(new GenericReturnMessage(93, "Gol contra não vale! Escolha um amigo."),
					HttpStatus.BAD_REQUEST);
		}

		if (kickFriend.getValueKick() <= 0) {
			return new ResponseEntity<>(new GenericReturnMessage(34, "Quantidade de moedas inválida"),
					HttpStatus.BAD_REQUEST);
		}		
		
		// Procura o amigo
		UserLogin challenged = userLoginRepository.findByUsername(kickFriend.getChallenged().getUsername());
		if (challenged == null) {
			return new ResponseEntity<>(new GenericReturnMessage(58, "Amigo não encontrado"), HttpStatus.BAD_REQUEST);
		}
		
		if (challenged.getUserStatus() != UserStatusEnum.ACTIVE) {
			return new ResponseEntity<>(new GenericReturnMessage(112, "Usuário não disponível para desafios."), HttpStatus.BAD_REQUEST);
		}

		GameMatch findGameMatch = gameMatchRepository.findById(kickFriend.getGameMatch().getId_gamematch()).orElse(null);
		if (findGameMatch == null) {
			return new ResponseEntity<>(new GenericReturnMessage(91, "O desafio solicitado não existe"),
					HttpStatus.BAD_REQUEST);
		}

		if ((findGameMatch.getIsClosed() == true) || (findGameMatch.getIsKickDisabled() == true)) {
			return new ResponseEntity<>(
					new GenericReturnMessage(49, "A partida solicitada está fechada ou não está mais disponível"),
					HttpStatus.BAD_REQUEST);
		}

		if ((kickFriend.getTeamChallenger().getId_team() != findGameMatch.getTeamA().getId_team())
				&& (kickFriend.getTeamChallenger().getId_team() != findGameMatch.getTeamB().getId_team())) {
			return new ResponseEntity<>(new GenericReturnMessage(28, "A equipe selecionada não está relacionada com esta partida"),
					HttpStatus.BAD_REQUEST);
		}

		Coin findCoin = coinRepository.findById(kickFriend.getCoin().getId_coin()).orElse(null);
		if (findCoin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(29, "Tipo de moeda inválida"), HttpStatus.BAD_REQUEST);
		}

		// Efetua o Debito na conta do user
		boolean transaction = userCoinLogic.debitCoin(kickFriend.getValueKick(), findCoin, userLogin.getUser().getUserProfile().getUserCoin());
		if (transaction == false) {
			return new ResponseEntity<>(new GenericReturnMessage(31, "Saldo de Moeda " + findCoin.getNameCoin() + " insuficiente"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (kickFriend.getTeamChallenger().getId_team() == findGameMatch.getTeamA().getId_team()) {
			kickFriend.setTeamChallenged(findGameMatch.getTeamB());
		} else if (kickFriend.getTeamChallenger().getId_team() == findGameMatch.getTeamB().getId_team()) {
			kickFriend.setTeamChallenged(findGameMatch.getTeamA());
		}

		kickFriend.setChallenged(challenged);
		kickFriend.setChallenger(userLogin);
		kickFriend.setDateKick(Calendar.getInstance());
		
		userLoginRepository.saveAndFlush(userLogin);
		KickFriend kickAdd = kickFriendRepository.saveAndFlush(kickFriend);
		
		if (pushNotificationQueueRepository.findByUserLoginAndGameMatch(userLogin.getId_userlogin(), findGameMatch.getId_gamematch()).isEmpty()) {
			pushNotificationQueueRepository.saveAndFlush(new PushNotificationQueue(userLogin.getId_userlogin(), findGameMatch.getId_gamematch(), false));
		}
		
		pushNotification.sendPushChallenge(challenged.getUsername(), userLogin.getUsername(), findGameMatch);				
		
		if (challenged.getUser().getUserProfile().getUserNotifications() != null) {
			if (challenged.getUser().getUserProfile().getUserNotifications().isOponentFound()) {
				MailConstrutorBean mailBean = new MailConstrutorBean();
				mailBean.setFirstName(challenged.getUser().getFirstName());
				mailBean.setRecipient(challenged.getUser().getEmail());
				mailBean.setSubject(userLogin.getUsername() + " te desafiou!");
				mailBean.setMessage(userLogin.getUsername() + " te desafiou para o jogo entre " + "<strong>" + findGameMatch.getTeamA().getNameTeam() + " X " + findGameMatch.getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" 
				+ "E ai, vai aceitar? <br/>" + " Para aceitar ou recusar o desafio, acesse: <br/>");
				mailBean.setLink("https://url");
				mailBean.setButtonTitle("Ver Desafios");
				mailBean.setTemplateEngine("defaultMail");
				mailClient.prepareAndSendMail(mailBean);
			}
		} else {
			MailConstrutorBean mailBean = new MailConstrutorBean();
			mailBean.setFirstName(challenged.getUser().getFirstName());
			mailBean.setRecipient(challenged.getUser().getEmail());
			mailBean.setSubject(userLogin.getUsername() + " te desafiou!");
			mailBean.setMessage(userLogin.getUsername() + " te desafiou para o jogo entre " + "<strong>" + findGameMatch.getTeamA().getNameTeam() + " X " + findGameMatch.getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" 
			+ "E ai, vai aceitar? <br/>" + " Para aceitar ou recusar o desafio, acesse: <br/>");
			mailBean.setLink("url");
			mailBean.setButtonTitle("Ver Desafios");
			mailBean.setTemplateEngine("defaultMail");
			mailClient.prepareAndSendMail(mailBean);
		}		
				
		return new ResponseEntity<>(kickAdd, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/accept", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> accept(@RequestParam(value = "kfid", required = true) String kickFriendID) {
		
		if (kickFriendID.matches("^[0-9]*$") == false || kickFriendID.length() < 1) {
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
		
		KickFriend kickFriend = kickFriendRepository.findKickFriendToAccept(Long.parseLong(kickFriendID), userLogin.getId_userlogin());
		if (kickFriend == null) {
			return new ResponseEntity<>(new GenericReturnMessage(22, "A partida solicitada não existe"),
					HttpStatus.BAD_REQUEST);
		}
		
		if ((kickFriend.getGameMatch().getIsClosed() == true) || (kickFriend.getGameMatch().getIsKickDisabled() == true)) {
			return new ResponseEntity<>(new GenericReturnMessage(49, "A partida solicitada está fechada ou não está mais disponível"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (kickFriend.getStatus().equals(KickFriendEnum.accepted)) {
			return new ResponseEntity<>(new GenericReturnMessage(90, "Esse jogo já foi aceito."), HttpStatus.BAD_REQUEST);
		}
		
		if (kickFriend.getStatus().equals(KickFriendEnum.refused)) {
			return new ResponseEntity<>(new GenericReturnMessage(91, "Esse jogo já foi recusado."), HttpStatus.BAD_REQUEST);
		}
		
		Coin findCoin = coinRepository.findById(1L).orElse(null);
		if (findCoin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(29, "Tipo de moeda inválida"), HttpStatus.BAD_REQUEST);
		}
		
		// Efetua o Debito na conta do user
		boolean transaction = userCoinLogic.debitCoin(kickFriend.getValueKick(), findCoin, kickFriend.getChallenged().getUser().getUserProfile().getUserCoin());
		if (transaction == false) {
			return new ResponseEntity<>(new GenericReturnMessage(31, "Saldo de Moeda " + findCoin.getNameCoin() + " insuficiente"),
					HttpStatus.BAD_REQUEST);
		}	
		
		kickFriend.setStatus(KickFriendEnum.accepted);
		kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setKicks(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getKicks() + 1);
		kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setKicks(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getKicks() + 1);		
		kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setKicksGeneral(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getKicksGeneral() + 1);
		kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setKicksGeneral(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getKicksGeneral() + 1);
		
		RankingPotCoin coinRanking = coinRankingRepository.findById(findCoin.getId_coin()).orElse(null);				
		coinRanking.setAmountCoin(coinRanking.getAmountCoin() + (((kickFriend.getValueKick() * (findCoin.getId_coin() == 1 ? 10 : 30) ) / 100) * 10 / 100));
		coinRankingRepository.saveAndFlush(coinRanking);	

		KickFriend kf = kickFriendRepository.saveAndFlush(kickFriend);
		
		pushNotification.sendPushChallengeAccepted(kickFriend.getChallenger().getUsername(), kickFriend.getChallenged().getUsername(), kickFriend.getGameMatch());
		
		if (kickFriend.getChallenger().getUser().getUserProfile().getUserNotifications() != null) {
			if (kickFriend.getChallenger().getUser().getUserProfile().getUserNotifications().isOponentFound()) {
				MailConstrutorBean mailBean = new MailConstrutorBean();
				mailBean.setFirstName(kickFriend.getChallenger().getUser().getFirstName());
				mailBean.setRecipient(kickFriend.getChallenger().getUser().getEmail());
				mailBean.setSubject(kickFriend.getChallenged().getUsername() + " aceitou seu desafio!");
				mailBean.setMessage(kickFriend.getChallenged().getUsername() + " aceitou o desafiou para o jogo entre " + "<strong>" + kickFriend.getGameMatch().getTeamA().getNameTeam() + " X " + kickFriend.getGameMatch().getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" 
				+ "Para mais detalhes acesse: <br/>");
				mailBean.setLink("url");
				mailBean.setButtonTitle("Ver meus jogos");
				mailBean.setTemplateEngine("defaultMail");
				mailClient.prepareAndSendMail(mailBean);
			}
		} else {
			MailConstrutorBean mailBean = new MailConstrutorBean();
			mailBean.setFirstName(kickFriend.getChallenger().getUser().getFirstName());
			mailBean.setRecipient(kickFriend.getChallenger().getUser().getEmail());
			mailBean.setSubject(kickFriend.getChallenged().getUsername() + " aceitou seu desafio!");
			mailBean.setMessage(kickFriend.getChallenged().getUsername() + " aceitou o desafiou para o jogo entre " + "<strong>" + kickFriend.getGameMatch().getTeamA().getNameTeam() + " X " + kickFriend.getGameMatch().getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" 
			+ "Para mais detalhes acesse: <br/>");
			mailBean.setLink("url");
			mailBean.setButtonTitle("Ver meus jogos");
			mailBean.setTemplateEngine("defaultMail");
			mailClient.prepareAndSendMail(mailBean);
		}
		
		return new ResponseEntity<>(kf, HttpStatus.OK);
	}
		
	@RequestMapping(value = "/findFriend", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> findFriend(@RequestParam(value = "friend", required = true) String username) {
		
		if (username.length() < 4) {
			return new ResponseEntity<>(new GenericReturnMessage(58, "Amigo não encontrado"), HttpStatus.BAD_REQUEST);
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

		if (username.equalsIgnoreCase(userLogin.getUsername())) {
			return new ResponseEntity<>(new GenericReturnMessage(93, "Gol contra não vale! Escolha um amigo."),
					HttpStatus.BAD_REQUEST);
		}
		
		UserLogin friend = userLoginRepository.findByUsername(username);
		if (friend == null) {
			return new ResponseEntity<>(new GenericReturnMessage(58, "Amigo não encontrado"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (friend.getUserStatus() != UserStatusEnum.ACTIVE) {
			return new ResponseEntity<>(new GenericReturnMessage(112, "Usuário não disponível para desafios."), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(friend, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/listAllToAccept", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> listAllToAccept() {

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

		Collection<KickFriend> listKickFriend = kickFriendRepository.findAllToAccept(userLogin.getId_userlogin(), false, KickFriendEnum.waiting);		
				
		return new ResponseEntity<>(listKickFriend, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<Object> getAllChallenge(@RequestParam(value = "listReturn", required = true) String listReturn) {

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
		start.add(Calendar.DAY_OF_MONTH, -10);
		Calendar end = Calendar.getInstance();
		end.add(Calendar.MONTH, 1);

		Collection<KickFriend> listKickFried = null;
		if (listReturn.equals("inprogress")) {
			listKickFried = kickFriendRepository.findAllKickFriendChallenge(userLogin.getId_userlogin(), false, KickFriendEnum.accepted, start, end);
		} else if (listReturn.equals("finalized")) {
			listKickFried = kickFriendRepository.findAllKickFriendChallenge(userLogin.getId_userlogin(), true, KickFriendEnum.accepted, start, end);
		} else if (listReturn.equals("waiting")) {
			listKickFried = kickFriendRepository.findAllKickFriendChallengeWaiting(userLogin.getId_userlogin(), false, KickFriendEnum.waiting, start, end);
		} else if (listReturn.equals("refused")) {
			listKickFried = kickFriendRepository.findAllKickFriendChallengeRefused(userLogin.getId_userlogin(), KickFriendEnum.refused, start, end);
		} 
		else {
			return new ResponseEntity<>(new GenericReturnMessage(53, "Metodo de Listagem não disponível"), HttpStatus.BAD_REQUEST);
		}
				
		return new ResponseEntity<>(listKickFried, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/refuse", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> refuse(@RequestParam(value = "kfid", required = true) String kickFriendID) {
		
		if (kickFriendID.matches("^[0-9]*$") == false || kickFriendID.length() < 1) {
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
		
		KickFriend kickFriend = kickFriendRepository.findKickFriendToAccept(Long.parseLong(kickFriendID), userLogin.getId_userlogin());
		if (kickFriend == null) {
			return new ResponseEntity<>(new GenericReturnMessage(91, "O desafio solicitado não existe"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (kickFriend.getStatus().equals(KickFriendEnum.accepted)) {
			return new ResponseEntity<>(new GenericReturnMessage(90, "Esse jogo já foi aceito."), HttpStatus.BAD_REQUEST);
		}
		
		if (kickFriend.getStatus().equals(KickFriendEnum.refused)) {
			return new ResponseEntity<>(new GenericReturnMessage(91, "Esse jogo já foi recusado."), HttpStatus.BAD_REQUEST);
		}
		
		// Devolve moedas do oponente		
		kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().setGoldCoin(kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().getGoldCoin() + kickFriend.getValueKick());
		
		kickFriend.setStatus(KickFriendEnum.refused);		
		KickFriend kf = kickFriendRepository.saveAndFlush(kickFriend);
		
		pushNotification.sendPushChallengeRefused(kickFriend.getChallenger().getUsername(), kickFriend.getChallenged().getUsername(), kickFriend.getGameMatch());				
		
		if (kickFriend.getChallenger().getUser().getUserProfile().getUserNotifications() != null) {
			if (kickFriend.getChallenger().getUser().getUserProfile().getUserNotifications().isOponentFound()) {
				MailConstrutorBean mailBean = new MailConstrutorBean();
				mailBean.setFirstName(kickFriend.getChallenger().getUser().getFirstName());
				mailBean.setRecipient(kickFriend.getChallenger().getUser().getEmail());
				mailBean.setSubject(kickFriend.getChallenged().getUsername() + " não aceitou seu desafio.");
				mailBean.setMessage(kickFriend.getChallenged().getUsername() + " não aceitou o desafiou para o jogo entre " + "<strong>" + kickFriend.getGameMatch().getTeamA().getNameTeam() + " X " + kickFriend.getGameMatch().getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" 
				+ "Para mais detalhes acesse: <br/>");
				mailBean.setLink("url");
				mailBean.setButtonTitle("Ver meus jogos");
				mailBean.setTemplateEngine("defaultMail");
				mailClient.prepareAndSendMail(mailBean);
			}
		} else {
			MailConstrutorBean mailBean = new MailConstrutorBean();
			mailBean.setFirstName(kickFriend.getChallenger().getUser().getFirstName());
			mailBean.setRecipient(kickFriend.getChallenger().getUser().getEmail());
			mailBean.setSubject(kickFriend.getChallenged().getUsername() + " não aceitou seu desafio.");
			mailBean.setMessage(kickFriend.getChallenged().getUsername() + " não aceitou o desafiou para o jogo entre " + "<strong>" + kickFriend.getGameMatch().getTeamA().getNameTeam() + " X " + kickFriend.getGameMatch().getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" 
			+ "Para mais detalhes acesse: <br/>");
			mailBean.setLink("https://url/kicks-friend");
			mailBean.setButtonTitle("Ver meus jogos");
			mailBean.setTemplateEngine("defaultMail");
			mailClient.prepareAndSendMail(mailBean);
		}
		
		return new ResponseEntity<>(kf, HttpStatus.OK);
	}	

}
