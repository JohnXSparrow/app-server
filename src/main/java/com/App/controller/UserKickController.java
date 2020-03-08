package com.App.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

import com.App.enumeration.UserStatusEnum;
import com.App.exception.GenericReturnMessage;
import com.App.logic.UserDebitCoinLogic;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.Coin;
import com.App.model.GameMatch;
import com.App.model.Kick;
import com.App.model.PushNotificationQueue;
import com.App.model.RankingPotCoin;
import com.App.model.UserLogin;
import com.App.push.PushNotification;
import com.App.repository.CoinRepository;
import com.App.repository.GameMatchRepository;
import com.App.repository.KickRepository;
import com.App.repository.PushNotificationQueueRepository;
import com.App.repository.RankingPotCoinRepository;
import com.App.repository.UserLoginRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/user/kick")
public class UserKickController {
	private static final Logger LOG = LoggerFactory.getLogger(UserKickController.class.getName());

	@Autowired
	KickRepository kickRepository;

	@Autowired
	GameMatchRepository gameMatchRepository;

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	CoinRepository coinRepository;
	
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

	@Value("${url.site}")
	private String urlsite;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Kick kick, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (kick.getId_kick() > 0 || kick.getId_kick() < 0) {
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

		if (kick.getValueKick() <= 0) {
			return new ResponseEntity<>(new GenericReturnMessage(34, "Quantidade de moedas inválida"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (kick.getCoin().getId_coin() == 2L) {
			if (kick.getValueKick() != 100  && kick.getValueKick() != 250) {
				return new ResponseEntity<>(new GenericReturnMessage(27, "Valor do chute deve ser válido"),
						HttpStatus.BAD_REQUEST);
			}
		}
		
		if (kick.getCoin().getId_coin() == 1L) {
			if (kick.getValueKick() != 50 && kick.getValueKick() != 100 && kick.getValueKick() != 250) {
				return new ResponseEntity<>(new GenericReturnMessage(27, "Valor do chute deve ser válido"),
						HttpStatus.BAD_REQUEST);
			}
		}

		GameMatch findGameMatch = gameMatchRepository.findById(kick.getGameMatch().getId_gamematch()).orElse(null);
		if (findGameMatch == null) {
			return new ResponseEntity<>(new GenericReturnMessage(22, "A partida solicitada não existe"),
					HttpStatus.BAD_REQUEST);
		}

		if ((findGameMatch.getIsClosed() == true) || (findGameMatch.getIsKickDisabled() == true)) {
			return new ResponseEntity<>(
					new GenericReturnMessage(49, "A partida solicitada está fechada ou não está mais disponível"),
					HttpStatus.BAD_REQUEST);
		}

		if ((kick.getTeam().getId_team() != findGameMatch.getTeamA().getId_team())
				&& (kick.getTeam().getId_team() != findGameMatch.getTeamB().getId_team())) {
			return new ResponseEntity<>(
					new GenericReturnMessage(28, "A equipe selecionada não está relacionada com esta partida"),
					HttpStatus.BAD_REQUEST);
		}

		Coin findCoin = coinRepository.findById(kick.getCoin().getId_coin()).orElse(null);
		if (findCoin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(29, "Tipo de moeda inválida"), HttpStatus.BAD_REQUEST);
		}

		if (kickRepository.findValidKick(kick.getGameMatch().getId_gamematch(), kick.getCoin(), userLogin).size() > 0) {
			return new ResponseEntity<>(
					new GenericReturnMessage(35, "Já existe um chute para essa partida com o tipo de moeda escolhida"),
						HttpStatus.BAD_REQUEST);
		}

		// Procura um oponente
		Collection<Kick> oponents = null;
		if (kick.getTeam().getId_team() == findGameMatch.getTeamA().getId_team()) {
			oponents = kickRepository.findOponent(findGameMatch.getId_gamematch(), findGameMatch.getTeamB(), findCoin, kick.getValueKick());
		} else if (kick.getTeam().getId_team() == findGameMatch.getTeamB().getId_team()) {
			oponents = kickRepository.findOponent(findGameMatch.getId_gamematch(), findGameMatch.getTeamA(), findCoin, kick.getValueKick());
		}

		Kick oponent = null;
		RankingPotCoin coinRanking = null;
		if (oponents != null) {
			if (oponents.size() > 0) {
				Collections.shuffle((List<Kick>) oponents);
				oponent = oponents.iterator().next();
				kick.setOponent(oponent.getUserLogin());
				oponent.setOponent(userLogin);				
				
				coinRanking = coinRankingRepository.findById(findCoin.getId_coin()).orElse(null);
				coinRanking.setAmountCoin(coinRanking.getAmountCoin() + (((kick.getValueKick() * (findCoin.getId_coin() == 1 ? 10 : 30) ) / 100) * 10 / 100));		
				
			} else {
				kick.setOponent(null);
			}
		}
		
		userLogin.getUser().getUserProfile().getUserStats().setKicks(userLogin.getUser().getUserProfile().getUserStats().getKicks() + 1);
		userLogin.getUser().getUserProfile().getUserStats().setKicksGeneral(userLogin.getUser().getUserProfile().getUserStats().getKicksGeneral() + 1);
		kick.setUserLogin(userLogin);
		kick.setDateKick(Calendar.getInstance());
		
		// Efetua o Debito na conta do user
		boolean transaction = userCoinLogic.debitCoin(kick.getValueKick(), findCoin, userLogin.getUser().getUserProfile().getUserCoin());
		if (transaction == false) {
			return new ResponseEntity<>(new GenericReturnMessage(31, "Saldo de Moeda " + findCoin.getNameCoin() + " insuficiente"),	HttpStatus.BAD_REQUEST);
		}

		userLoginRepository.saveAndFlush(userLogin);
		Kick kickAdd = kickRepository.saveAndFlush(kick);
				
		if (pushNotificationQueueRepository.findByUserLoginAndGameMatch(userLogin.getId_userlogin(), findGameMatch.getId_gamematch()).isEmpty()) {
			pushNotificationQueueRepository.saveAndFlush(new PushNotificationQueue(userLogin.getId_userlogin(), findGameMatch.getId_gamematch(), false));
		}
		
		if (oponent != null) {			
			kickRepository.saveAndFlush(oponent);
			coinRankingRepository.saveAndFlush(coinRanking);
			
			pushNotification.sendPushOponent(userLogin.getUsername(), oponent.getUserLogin().getUsername(), findGameMatch);				
			
			if (oponent.getUserLogin().getUser().getUserProfile().getUserNotifications() != null) {
				if (oponent.getUserLogin().getUser().getUserProfile().getUserNotifications().isOponentFound()) {
					MailConstrutorBean mailBean = new MailConstrutorBean();
					mailBean.setFirstName(oponent.getUserLogin().getUser().getFirstName());
					mailBean.setRecipient(oponent.getUserLogin().getUser().getEmail());
					mailBean.setSubject("Oponente Encontrado!");
					mailBean.setMessage("Um oponente foi encontrado para o jogo entre " + "<strong>" + findGameMatch.getTeamA().getNameTeam() + " x " + findGameMatch.getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" + "O nome do seu oponente é: " + "<strong>" + userLogin.getUsername() + "</strong><br/>" + " Agora é só torcer!!! Para ver mais sobre o status do seu jogo, acesse: ");
					mailBean.setLink(urlsite + "/kicks");
					mailBean.setButtonTitle("Ver meus jogos");
					mailBean.setTemplateEngine("defaultMail");
					mailClient.prepareAndSendMail(mailBean);
				}
			} else {
				MailConstrutorBean mailBean = new MailConstrutorBean();
				mailBean.setFirstName(oponent.getUserLogin().getUser().getFirstName());
				mailBean.setRecipient(oponent.getUserLogin().getUser().getEmail());
				mailBean.setSubject("Oponente Encontrado!");
				mailBean.setMessage("Um oponente foi encontrado para o jogo entre " + "<strong>" + findGameMatch.getTeamA().getNameTeam() + " x " + findGameMatch.getTeamB().getNameTeam() + "</strong><br/>" + "<br/>" + "O nome do seu oponente é: " + "<strong>" + userLogin.getUsername() + "</strong><br/>" + " Agora é só torcer!!! Para ver mais sobre o status do seu jogo, acesse: ");
				mailBean.setLink(urlsite + "/kicks");
				mailBean.setButtonTitle("Ver meus jogos");
				mailBean.setTemplateEngine("defaultMail");
				mailClient.prepareAndSendMail(mailBean);
			}			
		}
		
		return new ResponseEntity<>(kickAdd, HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> listKicks(@RequestParam(value = "listReturn", required = true) String listReturn) {

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

		Collection<Kick> listKicks = null;
		if (listReturn.equals("all") || listReturn.equals("inprogress")) {
			listKicks = kickRepository.findKicksDoneInProgress(userLogin, start, end);
		} else if (listReturn.equals("finalized")) {
			listKicks = kickRepository.findKicksDoneFinalized(userLogin, start, end);
		} else if (listReturn.matches("[0-9]+")) {
			GameMatch gameMatch = gameMatchRepository.findById(Long.parseLong(listReturn)).orElse(null);
			listKicks = kickRepository.findKicksDoneByGameMatch(userLogin, gameMatch);
		} else {
			return new ResponseEntity<>(new GenericReturnMessage(53, "Metodo de Listagem não disponível"), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(listKicks, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/amountKicks", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getAmountKikcs(
			@RequestParam(value = "gameMatch", required = true) String gameMatch,
			@RequestParam(value = "coin", required = true) String coin,
			@RequestParam(value = "value", required = true) String value) {
		
		if (gameMatch.matches("^[0-9]*$") == false || gameMatch.length() < 1) {
			return new ResponseEntity<>(new GenericReturnMessage(22, "A partida solicitada não existe"), HttpStatus.BAD_REQUEST);
		}			
		if (coin.matches("^[0-9]*$") == false || coin.length() < 1 || coin.length() > 1) {
			return new ResponseEntity<>(new GenericReturnMessage(29, "Tipo de moeda inválida"), HttpStatus.BAD_REQUEST);
		}		
		if (value.matches("^[0-9]*$") == false || value.length() < 1) {
			return new ResponseEntity<>(new GenericReturnMessage(27, "Valor do chute deve ser válido"), HttpStatus.BAD_REQUEST);
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
		
		GameMatch gameMatchFound = gameMatchRepository.findById(Long.parseLong(gameMatch)).orElse(null);
		if (gameMatchFound == null) {
			return new ResponseEntity<>(new GenericReturnMessage(22, "A partida solicitada não existe"),
					HttpStatus.BAD_REQUEST);
		}
		
		int kicksA = kickRepository.countAmountKick(gameMatchFound.getId_gamematch(), gameMatchFound.getTeamA().getId_team(), Long.parseLong(coin), Long.parseLong(value));
		int kicksB = kickRepository.countAmountKick(gameMatchFound.getId_gamematch(), gameMatchFound.getTeamB().getId_team(), Long.parseLong(coin), Long.parseLong(value));
		
		ObjectMapper mapper = new ObjectMapper();		
		ObjectNode teamA = mapper.createObjectNode();
		teamA.put("id", gameMatchFound.getTeamA().getId_team());
		teamA.put("kicks", kicksA);
		teamA.put("disabled", kicksA - kicksB >= 20 ? true : false);
		ObjectNode teamB = mapper.createObjectNode();
		teamB.put("id", gameMatchFound.getTeamB().getId_team());
		teamB.put("kicks", kicksB);
		teamB.put("disabled", kicksB - kicksA >= 20 ? true : false);
		
		ObjectNode amountKicks = mapper.createObjectNode();
		amountKicks.putPOJO("teamA", teamA);
		amountKicks.putPOJO("teamB", teamB);
		
		return new ResponseEntity<>(amountKicks, HttpStatus.OK);
	}

}
