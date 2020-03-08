package com.App.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.App.model.GameMatch;
import com.App.repository.GameMatchRepository;
import com.App.repository.LockJobRepository;

public class IsWeekGameMatchJob extends QuartzJobBean {

	@Autowired
	GameMatchRepository gameMatchRepository;

	@Autowired
	LockJobRepository lockJobRepository;

	@Override
	public void executeInternal(JobExecutionContext isWeekGameMatchJob) throws JobExecutionException {

		LockJob lockJob = lockJobRepository.findById(2L).orElse(null);
		if (!lockJob.isLock()) {
			lockJob.setLock(true);
			lockJobRepository.saveAndFlush(lockJob);

			Collection<GameMatch> gameMatchs = gameMatchRepository.findListGameMatch(false, false);
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

			if (gameMatchs != null) {
				if (gameMatchs.size() > 0) {
					for (GameMatch gameMatch : gameMatchs) {

						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_YEAR, 7);

						if (Long.parseLong(dateFormat.format(gameMatch.getDateTime().getTime())) <= Long
								.parseLong(dateFormat.format(cal.getTime().getTime()))) {

							gameMatch.setIsWeek(true);
							gameMatchRepository.save(gameMatch);
						}
					}
				}
			}
			lockJob.setLock(false);
			lockJobRepository.saveAndFlush(lockJob);
		}
	}
}
