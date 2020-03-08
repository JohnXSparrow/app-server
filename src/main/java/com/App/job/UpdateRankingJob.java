package com.App.job;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.App.model.Ranking;
import com.App.model.RankingPotCoin;
import com.App.model.UserCoin;
import com.App.model.UserLogin;
import com.App.model.UserRanking;
import com.App.model.UserStats;
import com.App.repository.LockJobRepository;
import com.App.repository.RankingPotCoinRepository;
import com.App.repository.RankingRepository;
import com.App.repository.UserCoinRepository;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserRankingRepository;
import com.App.repository.UserStatsRepository;

public class UpdateRankingJob extends QuartzJobBean {

	@Autowired
	UserStatsRepository userStatsRepository;

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	RankingRepository rankingRepository;
	
	@Autowired
	UserRankingRepository userRankingRepository;
	
	@Autowired
	RankingPotCoinRepository coinRankingRepository;
	
	@Autowired
	UserCoinRepository userCoinRepository;

	@Autowired
	LockJobRepository lockJobRepository;

	@Override
	public void executeInternal(JobExecutionContext updateRankingJob) throws JobExecutionException {
		
		ZonedDateTime sp = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("GMT-3"));
		
		LockJob lockJob = lockJobRepository.findById(6L).orElse(null);
		if (!lockJob.isLock()) {
			lockJob.setLock(true);
			lockJobRepository.saveAndFlush(lockJob);
			
			Ranking ranking = rankingRepository.findOneByDate(String.valueOf(sp.getMonthValue()) + String.valueOf(sp.getYear()));
			
			if (ranking != null) {
				if (ranking.isWasProccessed()) {
					lockJob.setLock(false);
					lockJobRepository.saveAndFlush(lockJob);
					return;
				} else {
					userRankingRepository.deleteUserRankingByIdRanking(ranking.getId_ranking());
				}
			} else {
				ranking = new Ranking();
				ranking.setDate(String.valueOf(sp.getMonthValue()) + String.valueOf(sp.getYear()));
				ranking = rankingRepository.saveAndFlush(ranking);
			}
			
			int position = 1;
			Collection<RankingPotCoin> coinRankingList = coinRankingRepository.findAll();
			Collection<UserStats> list = userStatsRepository.getUserStatsFilter();
			Collection<UserRanking> listRanking = new ArrayList<UserRanking>();
			RankingPotCoin goldCoinRanking = coinRankingList.stream().filter(x -> x.getId_rankingpotcoin() == 1).findAny().orElse(null);
			RankingPotCoin silverCoinRanking = coinRankingList.stream().filter(x -> x.getId_rankingpotcoin() == 2).findAny().orElse(null);
			for (UserStats userStats : list) {
				UserLogin userLogin = userLoginRepository.findById(userStats.getId_userstats()).orElse(null);
				UserRanking userRanking = new UserRanking();

				if (position == 1) {
					userRanking.setGoldReward(50 + goldCoinRanking.getAmountCoin() - ((goldCoinRanking.getAmountCoin() * 70) / 100));
					userRanking.setSilverReward(1000 + silverCoinRanking.getAmountCoin() - ((silverCoinRanking.getAmountCoin() * 70) / 100));
				} else if (position == 2) {
					userRanking.setGoldReward(50 + goldCoinRanking.getAmountCoin() - ((goldCoinRanking.getAmountCoin() * 75) / 100));
					userRanking.setSilverReward(1000 + silverCoinRanking.getAmountCoin() - ((silverCoinRanking.getAmountCoin() * 75) / 100));
				} else if (position == 3) {
					userRanking.setGoldReward(50 + goldCoinRanking.getAmountCoin() - ((goldCoinRanking.getAmountCoin() * 80) / 100));
					userRanking.setSilverReward(1000 + silverCoinRanking.getAmountCoin() - ((silverCoinRanking.getAmountCoin() * 80) / 100));
				} else if (position == 4) {
					userRanking.setGoldReward(50 + goldCoinRanking.getAmountCoin() - ((goldCoinRanking.getAmountCoin() * 85) / 100));
					userRanking.setSilverReward(1000 + silverCoinRanking.getAmountCoin() - ((silverCoinRanking.getAmountCoin() * 85) / 100));
				} else if (position == 5) {
					userRanking.setGoldReward(50 + goldCoinRanking.getAmountCoin() - ((goldCoinRanking.getAmountCoin() * 90) / 100));
					userRanking.setSilverReward(1000 + silverCoinRanking.getAmountCoin() - ((silverCoinRanking.getAmountCoin() * 90) / 100));
				} else {
					userRanking.setGoldReward(50);
					userRanking.setSilverReward(1000);
				}
				
				userRanking.setUserStats(userStats);
				userRanking.setUserLogin(userLogin);
				userRanking.setPosition(position);
				userRanking.setRanking(ranking);
				
				listRanking.add(userRanking);			
				position++;
			}
			
			if (sp.getHour() == 23) {
				LocalDate sizeDayMonth = sp.toLocalDate();
				if (sizeDayMonth.lengthOfMonth() == sp.getDayOfMonth()) {
					for (UserRanking userRanking : listRanking) {
						UserCoin userCoin = userCoinRepository.findById(userRanking.getUserLogin().getId_userlogin()).orElse(null);
						userCoin.setGoldCoin(userCoin.getGoldCoin() + userRanking.getGoldReward());
						userCoin.setSilverCoin(userCoin.getSilverCoin() + userRanking.getSilverReward());
						userCoinRepository.saveAndFlush(userCoin);
					}
					coinRankingRepository.resetRankingPotCoin();
					userStatsRepository.resetUserStats();
					ranking.setWasProccessed(true);
				}
			}
			
			ranking.setUserRanking(listRanking);
			rankingRepository.saveAndFlush(ranking);

			lockJob.setLock(false);
			lockJobRepository.saveAndFlush(lockJob);
		}
	}
}
