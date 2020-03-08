package com.App.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.App.model.GameMatch;
import com.App.repository.GameMatchRepository;

public class CloseGameMatchJob implements Job {
	private static final Logger LOG = LoggerFactory.getLogger(CloseGameMatchJob.class.getName());

	@Autowired
	GameMatchRepository gameMatchRepository;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {

		Collection<GameMatch> gameMatchs = gameMatchRepository.findGameMatchJob(new Date(), false, true, false);
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

		if (gameMatchs != null) {
			if (gameMatchs.size() > 0) {
				for (GameMatch gameMatch : gameMatchs) {

					if (gameMatch.getIsClosed() == true) {
						gameMatch.setIsKickDisabled(true);
						gameMatch.setIsWeek(false);
						gameMatchRepository.save(gameMatch);
						LOG.info("Fechando getIsClosed, IsKickDisabled GameMatch True: " + gameMatch.getId_gamematch());
					}

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MINUTE, 5);

					// Faltando 5 minutos ele entra no if
					if (Long.parseLong(dateFormat.format(gameMatch.getStartTime().getTime().getTime())) <= Long
							.parseLong(dateFormat.format(cal.getTime().getTime()))) {
						gameMatch.setIsKickDisabled(true);
						gameMatch.setIsWeek(false);
						gameMatchRepository.save(gameMatch);
						LOG.info("Fechando IsKickDisabled GameMatch True: " + gameMatch.getId_gamematch());
					}
				}
			}
		}
	}
}