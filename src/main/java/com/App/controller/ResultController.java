package com.App.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.exception.GenericReturnMessage;
import com.App.job.LockJob;
import com.App.logic.GameMatchSetResultLogic;
import com.App.logic.ResultCreditCoinService;
import com.App.model.GameMatch;
import com.App.model.Result;
import com.App.repository.GameMatchRepository;
import com.App.repository.LockJobRepository;
import com.App.repository.ResultRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
@RequestMapping("/adm/result")
public class ResultController {

	@Autowired
	ResultRepository resultRepository;

	@Autowired
	GameMatchRepository gameMatchRepository;
	
	@Autowired
	LockJobRepository lockJobRepository;

	@Autowired
	GameMatchSetResultLogic gameMatchResultLogic;
	
	@Autowired
	ResultCreditCoinService resultCreditCoinService;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Result result, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		GameMatch findGameMatch = gameMatchRepository.findById(result.getGameMatch().getId_gamematch()).orElse(null);
		if (findGameMatch == null) {
			return new ResponseEntity<>(new GenericReturnMessage(22, "A partida solicitada não existe"),
					HttpStatus.BAD_REQUEST);
		}

		// Verifica se já existe um resultado para essa gamaMatch
		if (resultRepository.findByGameMatchId(result.getGameMatch()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(23, "Já existe um resultado para esta partida"),
					HttpStatus.BAD_REQUEST);
		}

		if (findGameMatch.getIsSetResult() == false) {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm");
			Calendar cal = findGameMatch.getEndTime();	
			return new ResponseEntity<>(new GenericReturnMessage(24, "A partida ainda não terminou, aguarde até: " 
					+ dateFormat.format(cal.getTime())), HttpStatus.BAD_REQUEST);
		}
		
		LockJob lockJobKick = lockJobRepository.findById(4L).orElse(null);
		if (!lockJobKick.isLock()) {
			lockJobKick.setLock(true);
			lockJobRepository.saveAndFlush(lockJobKick);
		} else {
			return new ResponseEntity<>(new GenericReturnMessage(85, "Existe um Job de Resultado sendo executado, aguarde o e-mail."),
					HttpStatus.BAD_REQUEST);
		}
		
		LockJob lockJobKickFriend = lockJobRepository.findById(5L).orElse(null);
		if (!lockJobKickFriend.isLock()) {
			lockJobKickFriend.setLock(true);
			lockJobRepository.saveAndFlush(lockJobKickFriend);
		} else {
			lockJobKick.setLock(false);
			lockJobRepository.saveAndFlush(lockJobKick);
			return new ResponseEntity<>(new GenericReturnMessage(85, "Existe um Job de Resultado de Amigo sendo executado, aguarde o e-mail."),
					HttpStatus.BAD_REQUEST);
		}

		// Chama a logica de time vencedor conforme gols de cada time
		gameMatchResultLogic.teamWin(findGameMatch, result);
		
		gameMatchRepository.save(findGameMatch);
		Result resultAdd = resultRepository.save(result);
		
		// chama serviço de distribuição de moedas async
		resultCreditCoinService.creditCoinKick(findGameMatch.getId_gamematch(), resultAdd, findGameMatch.getBonus());		
		
		return new ResponseEntity<>(resultAdd, HttpStatus.OK);
	}

}
