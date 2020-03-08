package com.App.logic;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.App.job.LockJob;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.Kick;
import com.App.model.KickFriend;
import com.App.model.Result;
import com.App.repository.KickFriendRepository;
import com.App.repository.KickRepository;
import com.App.repository.LockJobRepository;

@Service
public class ResultCreditCoinService {
	
	@Autowired
	KickRepository kickRepository;
	
	@Autowired
	KickFriendRepository kickFriendRepository;

	@Autowired
	UserKickResultCreditCoinLogic userKickResultCoinLogic;
	
	@Autowired
	UserKickFriendResultCreditCoinLogic userKickFriendResultCoinLogic;
	
	@Autowired
	LockJobRepository lockJobRepository;	
	
	@Autowired
	MailClient mailClient;
	
	@Async
	public void creditCoinKick(Long IDGameMatch, Result result, String bonus) {
		
		// Chama a logica de time vencedor conforme gols de cada time
		Collection<Kick> allKick = kickRepository.findAllByGameMatch(IDGameMatch, false);
		if (!allKick.isEmpty()) {
			userKickResultCoinLogic.creditCoinKick(allKick, result, bonus);
		} else {
			LockJob lockJobKick = lockJobRepository.findById(4L).orElse(null);
			lockJobKick.setLock(false);
			lockJobRepository.saveAndFlush(lockJobKick);
		}
		
		// faz a distribuição de coins KickFriend
		Collection<KickFriend> allKickFriend = kickFriendRepository.findAllByGameMatch(IDGameMatch, false);
		if (!allKickFriend.isEmpty()) {
			userKickFriendResultCoinLogic.creditCoinKickFriend(allKickFriend, result);
		} else {
			LockJob lockJobKickFriend = lockJobRepository.findById(5L).orElse(null);
			lockJobKickFriend.setLock(false);
			lockJobRepository.saveAndFlush(lockJobKickFriend);
		}
		
		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName("Adm");
		mailBean.setRecipient("email");
		mailBean.setSubject("Resultado Finalizado");
		mailBean.setMessage("Inserção de resultado da partida número: " 
		+ result.getGameMatch().getId_gamematch() + " foi finalizado com sucesso.");
		mailBean.setLink("url");
		mailBean.setButtonTitle("Adm");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);
		
	}

}
