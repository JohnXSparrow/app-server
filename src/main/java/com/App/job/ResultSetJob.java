package com.App.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.GameMatch;
import com.App.repository.GameMatchRepository;
import com.App.repository.LockJobRepository;
import com.App.repository.ResultRepository;

public class ResultSetJob implements Job {
	private static final Logger LOG = LoggerFactory.getLogger(ResultSetJob.class.getName());

	@Autowired
	GameMatchRepository gameMatchRepository;

	@Autowired
	ResultRepository resultRepository;

	@Autowired
	LockJobRepository lockJobRepository;

	@Autowired
	MailClient mailClient;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {

		LockJob lockJob = lockJobRepository.findById(3L).orElse(null);
		if (!lockJob.isLock()) {
			lockJob.setLock(true);
			lockJobRepository.saveAndFlush(lockJob);

			Collection<GameMatch> gameMatchs = gameMatchRepository.findIsKickDisabled(true, false);
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

			if (gameMatchs != null) {
				if (gameMatchs.size() > 0) {
					for (GameMatch gameMatch : gameMatchs) {
						Calendar cal = Calendar.getInstance();

						if (Long.parseLong(dateFormat.format(gameMatch.getEndTime().getTime().getTime())) <= Long
								.parseLong(dateFormat.format(cal.getTime().getTime()))) {
							
							gameMatch.setIsSetResult(true);
							gameMatchRepository.save(gameMatch);

							MailConstrutorBean mailBean = new MailConstrutorBean();
							mailBean.setFirstName("Adm");
							mailBean.setRecipient("email");
							mailBean.setSubject(gameMatch.getTeamA().getNameTeam() + " X " + gameMatch.getTeamB().getNameTeam());
							mailBean.setMessage("O jogo entre " + gameMatch.getTeamA().getNameTeam() 
									+ " X " + gameMatch.getTeamB().getNameTeam() + " de número: " 
									+ gameMatch.getId_gamematch() + " foi finalizado e está pronto para ser inserido um resultado.");
							mailBean.setLink("url");
							mailBean.setButtonTitle("Inserir Resultado");
							mailBean.setTemplateEngine("defaultMail");
							mailClient.prepareAndSendMail(mailBean);

							LOG.info("GameMatch isResultSet True: " + gameMatch.getId_gamematch());
						}
					}
				}
			}
			
			lockJob.setLock(false);
			lockJobRepository.saveAndFlush(lockJob);
		}
	}
}