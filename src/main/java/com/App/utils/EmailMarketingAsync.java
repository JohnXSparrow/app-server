package com.App.utils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.App.enumeration.UserStatusEnum;
import com.App.mail.MailClient;
import com.App.mail.MailConstrutorBean;
import com.App.model.UserLogin;
import com.App.model.UserMarketing;
import com.App.repository.UserLoginRepository;
import com.App.repository.UserMarketingRespository;
import com.App.repository.UserRepository;

@Service
public class EmailMarketingAsync {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserMarketingRespository userMarketingRespository;

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	MailClient mailClient;

	@Async
	public void sendEmailMarketingApp() {
		Collection<UserLogin> usersLogin = userLoginRepository.findUsersByStatus(UserStatusEnum.ACTIVE);

		int limitToSendByDay = 500;
		int limitToSleep = 10;
		int ltsd = 1;
		int lts = 1;

		for (UserLogin user : usersLogin) {
			if (ltsd <= limitToSendByDay) {
				if (lts <= limitToSleep) {

					if (user.getUser().getUserProfile().getUserNotifications() != null) {
						if (user.getUser().getUserProfile().getUserNotifications().isNews()) {
							MailConstrutorBean mailBean = new MailConstrutorBean();
							mailBean.setFirstName(user.getUser().getFirstName());
							mailBean.setRecipient(user.getUser().getEmail());
							mailBean.setSubject(user.getUser().getFirstName() + ", conheça o aplicativo");
							mailBean.setTemplateEngine("app");
							mailClient.prepareAndSendMail(mailBean);
							lts++;
							ltsd++;
						}
					} else {
						MailConstrutorBean mailBean = new MailConstrutorBean();
						mailBean.setFirstName(user.getUser().getFirstName());
						mailBean.setRecipient(user.getUser().getEmail());
						mailBean.setSubject(user.getUser().getFirstName() + ", conheça o aplicativo");
						mailBean.setTemplateEngine("app");
						mailClient.prepareAndSendMail(mailBean);
						lts++;
						ltsd++;
					}

				} else {
					try {
						TimeUnit.SECONDS.sleep(3);
						lts = 1;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					TimeUnit.MINUTES.sleep(1);
					lts = 1;
					ltsd = 1;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName("Adm");
		mailBean.setRecipient("email");
		mailBean.setSubject("App E-mail Marketing");
		mailBean.setMessage("Foram enviados: " + (ltsd - 1) + " e-mails com sucesso.");
		mailBean.setLink("url");
		mailBean.setButtonTitle("ADM");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);
	}

	@Async
	public void sendEmailMarketingAppNotUser() {
		Collection<UserMarketing> users = userMarketingRespository.findAllNotSentAndIsUnsubscribe("F", "F", "@icloud.com");

		int limitToSendByDay = 500;
		int limitToSleep = 10;
		int ltsd = 1;
		int lts = 1;

		for (UserMarketing user : users) {
			if (ltsd <= limitToSendByDay) {
				if (lts <= limitToSleep) {
					MailConstrutorBean mailBean = new MailConstrutorBean();
					mailBean.setFirstName(user.getFirstName());
					mailBean.setRecipient(user.getEmail().trim());
					mailBean.setSubject(user.getFirstName() + ", conheça o aplicativo");
					mailBean.setLink("url/unsubscribe?tk=" + user.getTokenToUnsubscribe() + "&auth=" + user.getId_usermarketing());
					mailBean.setTemplateEngine("app");
					mailClient.prepareAndSendMail(mailBean);
					lts++;
					ltsd++;

					user.setWasSent(true);
					userMarketingRespository.saveAndFlush(user);
				} else {
					try {
						TimeUnit.SECONDS.sleep(5);
						lts = 1;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				break;
			}
		}

		MailConstrutorBean mailBean = new MailConstrutorBean();
		mailBean.setFirstName("Adm");
		mailBean.setRecipient("email");
		mailBean.setSubject("App E-mail Marketing");
		mailBean.setMessage("Foram enviados: " + (ltsd - 1) + " e-mails com sucesso.");
		mailBean.setLink("url");
		mailBean.setButtonTitle("ADM");
		mailBean.setTemplateEngine("defaultMail");
		mailClient.prepareAndSendMail(mailBean);
	}

}
