package com.App.logic;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.App.enumeration.KickFriendEnum;
import com.App.job.LockJob;
import com.App.model.KickFriend;
import com.App.model.PushNotificationQueue;
import com.App.model.Result;
import com.App.push.PushNotification;
import com.App.repository.KickFriendRepository;
import com.App.repository.LockJobRepository;
import com.App.repository.PushNotificationQueueRepository;
import com.App.repository.UserCoinRepository;

@Service
public class UserKickFriendResultCreditCoinLogic {
	
	@Autowired
	KickFriendRepository kickFriendRepository;

	@Autowired
	UserCoinRepository userCoinRepository;
	
	@Autowired
	LockJobRepository lockJobRepository;
	
	@Autowired
	PushNotificationQueueRepository pushNotificationQueueRepository;
	
	@Autowired
	PushNotification pushNotification;	
	
	final int winPerGold = 80;
	final int aTiePerGold = 80;

	final int winPerSilver = 50;
	final int aTiePerSilver = 50;
	
	public void creditCoinKickFriend(Collection<KickFriend> allKickFriend, Result result) {

		for (KickFriend kickFriend : allKickFriend) {			
			if (kickFriend.getStatus().equals(KickFriendEnum.accepted)) {
				
				if (result.getTeamWin() == null) {					
					if (kickFriend.getCoin().getId_coin() == 1) {
						kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kickFriend.getChallenger().getId_userlogin()).orElse(null).getGoldCoin() + ((kickFriend.getValueKick() * aTiePerGold) / 100));
						kickFriend.getChallenged().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kickFriend.getChallenged().getId_userlogin()).orElse(null).getGoldCoin() + ((kickFriend.getValueKick() * aTiePerGold) / 100));
						
					} else if (kickFriend.getCoin().getId_coin() == 2) {
						kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kickFriend.getChallenger().getId_userlogin()).orElse(null).getSilverCoin() + ((kickFriend.getValueKick() * aTiePerSilver) / 100));
						kickFriend.getChallenged().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kickFriend.getChallenged().getId_userlogin()).orElse(null).getSilverCoin() + ((kickFriend.getValueKick() * aTiePerSilver) / 100));
					}
					
					kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setAtie(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getAtie() + 1);
					kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setAtie(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getAtie() + 1);					
					kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setAtieGeneral(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getAtieGeneral() + 1);
					kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setAtieGeneral(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getAtieGeneral() + 1);
				
				} else if (kickFriend.getTeamChallenger().getId_team() == result.getTeamWin().getId_team()) {
					
					if (kickFriend.getCoin().getId_coin() == 1) {
						kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().setGoldCoin((userCoinRepository.findById(kickFriend.getChallenger().getId_userlogin()).orElse(null).getGoldCoin() + kickFriend.getValueKick()) + ((kickFriend.getValueKick() * winPerGold) / 100));

					} else if (kickFriend.getCoin().getId_coin() == 2) {
						kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().setSilverCoin((userCoinRepository.findById(kickFriend.getChallenger().getId_userlogin()).orElse(null).getSilverCoin() + kickFriend.getValueKick()) + ((kickFriend.getValueKick() * winPerSilver) / 100));
					}
					
					kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setWin(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getWin() + 1);
					kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setDefeat(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getDefeat() + 1);
					kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setWinGeneral(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getWinGeneral() + 1);
					kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setDefeatGeneral(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getDefeatGeneral() + 1);
					
				} else if (kickFriend.getTeamChallenged().getId_team() == result.getTeamWin().getId_team()) {
					
					if (kickFriend.getCoin().getId_coin() == 1) {
						kickFriend.getChallenged().getUser().getUserProfile().getUserCoin().setGoldCoin((userCoinRepository.findById(kickFriend.getChallenged().getId_userlogin()).orElse(null).getGoldCoin() + kickFriend.getValueKick()) + ((kickFriend.getValueKick() * winPerGold) / 100));

					} else if (kickFriend.getCoin().getId_coin() == 2) {
						kickFriend.getChallenged().getUser().getUserProfile().getUserCoin().setSilverCoin((userCoinRepository.findById(kickFriend.getChallenged().getId_userlogin()).orElse(null).getSilverCoin() + kickFriend.getValueKick()) + ((kickFriend.getValueKick() * winPerSilver) / 100));
					}
					
					kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setWin(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getWin() + 1);
					kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setDefeat(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getDefeat() + 1);
					
					kickFriend.getChallenged().getUser().getUserProfile().getUserStats().setWinGeneral(kickFriend.getChallenged().getUser().getUserProfile().getUserStats().getWinGeneral() + 1);
					kickFriend.getChallenger().getUser().getUserProfile().getUserStats().setDefeatGeneral(kickFriend.getChallenger().getUser().getUserProfile().getUserStats().getDefeatGeneral() + 1);
				}				

			} else if (kickFriend.getStatus().equals(KickFriendEnum.waiting)) {
				
				if (kickFriend.getCoin().getId_coin() == 1) {
					kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().setGoldCoin(userCoinRepository.findById(kickFriend.getChallenger().getId_userlogin()).orElse(null).getGoldCoin() + kickFriend.getValueKick());

				} else if (kickFriend.getCoin().getId_coin() == 2) {
					kickFriend.getChallenger().getUser().getUserProfile().getUserCoin().setSilverCoin(userCoinRepository.findById(kickFriend.getChallenger().getId_userlogin()).orElse(null).getSilverCoin() + kickFriend.getValueKick());
				}
				
				kickFriend.setStatus(KickFriendEnum.refused);
				
			}
			
			kickFriend.setResult(result);
			kickFriend.setWasProccessed(true);			
			kickFriendRepository.saveAndFlush(kickFriend);			
			
			if (kickFriend.getStatus().equals(KickFriendEnum.accepted)) {				
				Collection<PushNotificationQueue> pushNotificationQueueS1 = pushNotificationQueueRepository.findByUserLoginAndGameMatch(kickFriend.getChallenged().getId_userlogin(), result.getGameMatch().getId_gamematch());
				for (PushNotificationQueue pushNotificationQueue : pushNotificationQueueS1) {
						if (!pushNotificationQueue.isWasSent()) {
							pushNotification.sendPushKickFinalized(kickFriend.getChallenged().getUsername(), kickFriend.getGameMatch());
							pushNotificationQueue.setWasSent(true);
							pushNotificationQueueRepository.saveAndFlush(pushNotificationQueue);
						}
				}
				
				Collection<PushNotificationQueue> pushNotificationQueueS2 = pushNotificationQueueRepository.findByUserLoginAndGameMatch(kickFriend.getChallenger().getId_userlogin(), result.getGameMatch().getId_gamematch());
				for (PushNotificationQueue pushNotificationQueue : pushNotificationQueueS2) {
						if (!pushNotificationQueue.isWasSent()) {
							pushNotification.sendPushKickFinalized(kickFriend.getChallenger().getUsername(), kickFriend.getGameMatch());
							pushNotificationQueue.setWasSent(true);
							pushNotificationQueueRepository.saveAndFlush(pushNotificationQueue);
						}
				}				
			}
		}
		
		LockJob lockJobKickFriend = lockJobRepository.findById(5L).orElse(null);
		lockJobKickFriend.setLock(false);
		lockJobRepository.saveAndFlush(lockJobKickFriend);

	}

}
