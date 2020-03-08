package com.App.job;

import java.util.Collection;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.UserCoin;
import com.App.repository.LockJobRepository;
import com.App.repository.UserCoinRepository;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DistributeCoinsJob extends QuartzJobBean {

	@Autowired
	UserCoinRepository usercoinRepository;

	@Autowired
	LockJobRepository lockJobRepository;

	@Value("${distribute.silver.coin.amount}")
	long distributeCoinAmount = 0;

	@Autowired
	MailClient mailClient;

	protected void executeInternal(JobExecutionContext jobExecutionContext) {

		LockJob lockJob = lockJobRepository.findById(1L).orElse(null);
		if (!lockJob.isLock()) {
			lockJob.setLock(true);
			lockJobRepository.saveAndFlush(lockJob);

			Collection<UserCoin> usersCoin = usercoinRepository.findUsersCoin(distributeCoinAmount);
			
			if (usersCoin != null) {
				if (usersCoin.size() > 0) {
					int x = 1;
					for (UserCoin userCoin : usersCoin) {
						userCoin.setSilverCoin(distributeCoinAmount);
						usercoinRepository.saveAndFlush(userCoin);
						x++;
					}

					MailConstrutorBean mailBean = new MailConstrutorBean();
					mailBean.setFirstName("Adm");
					mailBean.setRecipient("email");
					mailBean.setSubject("Distribuição de Moedas");
					mailBean.setMessage("Foram distribuidas " + distributeCoinAmount + " Moedas Prata para " + x + " usuários");
					mailBean.setLink("url");
					mailBean.setButtonTitle("Adm");
					mailBean.setTemplateEngine("defaultMail");
					mailClient.prepareAndSendMail(mailBean);
				}
			}
			
			lockJob.setLock(false);
			lockJobRepository.saveAndFlush(lockJob);
		}
	}
}