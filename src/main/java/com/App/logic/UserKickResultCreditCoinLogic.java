package com.App.logic;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.App.job.LockJob;
import com.App.model.Kick;
import com.App.model.PushNotificationQueue;
import com.App.model.Result;
import com.App.push.PushNotification;
import com.App.repository.KickRepository;
import com.App.repository.LockJobRepository;
import com.App.repository.PushNotificationQueueRepository;
import com.App.repository.UserCoinRepository;

@Service
public class UserKickResultCreditCoinLogic {
	
	@Autowired
	KickRepository kickRepository;
	
	@Autowired
	UserCoinRepository userCoinRepository;
	
	@Autowired
	LockJobRepository lockJobRepository;
	
	@Autowired
	PushNotificationQueueRepository pushNotificationQueueRepository;
	
	@Autowired
	PushNotification pushNotification;

	public void creditCoinKick(Collection<Kick> allKick, Result result, String bonus) {
		
		int idTeamBonus = bonus != null ? Integer.parseInt(bonus.substring(0, bonus.indexOf("-"))) : 0;
		int valueBonus = bonus != null ? Integer.parseInt(bonus.substring(bonus.indexOf("-") + 1)) : 0;		

		for (Kick kick : allKick) {
			long value = 0;

			// calcula empate com oponente
			if (result.getTeamWin() == null && kick.getOponent() != null) {
				value = (kick.getValueKick() * kick.getUserLogin().getUser().getUserProfile().getProfilePlan().getATiePer()) / 100;
				
				if (kick.getCoin().getId_coin() == 1) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getGoldCoin() + value);
				} else if (kick.getCoin().getId_coin() == 2) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getSilverCoin() + value);
				}
				
				kick.getUserLogin().getUser().getUserProfile().getUserStats().setAtie(kick.getUserLogin().getUser().getUserProfile().getUserStats().getAtie() + 1);
				kick.getUserLogin().getUser().getUserProfile().getUserStats().setAtieGeneral(kick.getUserLogin().getUser().getUserProfile().getUserStats().getAtieGeneral() + 1);

				// calcula empate sem oponente
			} else if (result.getTeamWin() == null && kick.getOponent() == null) {
				value = kick.getValueKick();
				
				if (kick.getCoin().getId_coin() == 1) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getGoldCoin() + kick.getValueKick());
				} else if (kick.getCoin().getId_coin() == 2) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getSilverCoin() + kick.getValueKick());
				}

				// calcular time vencedor com oponente
			} else if (kick.getTeam().getId_team() == result.getTeamWin().getId_team() && kick.getOponent() != null) {
				value = kick.getValueKick() + ((kick.getValueKick() * (kick.getUserLogin().getUser().getUserProfile().getProfilePlan().getWinPer() + (idTeamBonus == kick.getTeam().getId_team() ? valueBonus : 0))) / 100);
				
				if (kick.getCoin().getId_coin() == 1) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getGoldCoin() + value);
				} else if (kick.getCoin().getId_coin() == 2) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getSilverCoin() + value);
				}
				
				kick.getUserLogin().getUser().getUserProfile().getUserStats().setWin(kick.getUserLogin().getUser().getUserProfile().getUserStats().getWin() + kick.getUserLogin().getUser().getUserProfile().getProfilePlan().getWinForKick());
				kick.getUserLogin().getUser().getUserProfile().getUserStats().setWinGeneral(kick.getUserLogin().getUser().getUserProfile().getUserStats().getWinGeneral() + kick.getUserLogin().getUser().getUserProfile().getProfilePlan().getWinForKick());

				// calcula time vencedor sem oponente
			} else if (kick.getTeam().getId_team() == result.getTeamWin().getId_team() && kick.getOponent() == null) {
				value = kick.getValueKick();
				
				if (kick.getCoin().getId_coin() == 1) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getGoldCoin() + kick.getValueKick());
				} else if (kick.getCoin().getId_coin() == 2) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getSilverCoin() + kick.getValueKick());
				}
				
				// calcula time perdedor sem oponente
			} else if (kick.getTeam().getId_team() != result.getTeamWin().getId_team() && kick.getOponent() == null) {
				value = kick.getValueKick();
				
				if (kick.getCoin().getId_coin() == 1) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getGoldCoin() + kick.getValueKick());
				} else if (kick.getCoin().getId_coin() == 2) {
					kick.getUserLogin().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kick.getUserLogin().getId_userlogin()).orElse(null).getSilverCoin() + kick.getValueKick());
				}
			}
			
				//derrota
			else {
				value = kick.getValueKick() - kick.getValueKick() - kick.getValueKick();
				kick.getUserLogin().getUser().getUserProfile().getUserStats().setDefeat(kick.getUserLogin().getUser().getUserProfile().getUserStats().getDefeat() + 1);
				kick.getUserLogin().getUser().getUserProfile().getUserStats().setDefeatGeneral(kick.getUserLogin().getUser().getUserProfile().getUserStats().getDefeatGeneral() + 1);
			}
			
			kick.setResult(result);
			kick.setValuewinorlost(value);
			kick.setWasProccessed(true);
			
			kickRepository.saveAndFlush(kick);			
			
			Collection<PushNotificationQueue> pushNotificationQueueS = pushNotificationQueueRepository.findByUserLoginAndGameMatch(kick.getUserLogin().getId_userlogin(), result.getGameMatch().getId_gamematch());
			for (PushNotificationQueue pushNotificationQueue : pushNotificationQueueS) {
					if (!pushNotificationQueue.isWasSent()) {
						pushNotification.sendPushKickFinalized(kick.getUserLogin().getUsername(), kick.getGameMatch());
						pushNotificationQueue.setWasSent(true);
						pushNotificationQueueRepository.saveAndFlush(pushNotificationQueue);
					}
			}			
			
		}
				
		LockJob lockJobKick = lockJobRepository.findById(4L).orElse(null);
		lockJobKick.setLock(false);
		lockJobRepository.saveAndFlush(lockJobKick);
	}
}
