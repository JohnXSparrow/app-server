package com.App.job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class SchedulerConfig {

	private static final Logger LOG = LoggerFactory.getLogger(SchedulerConfig.class);

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryRun(JobFactory jobFactoryRun, Trigger jobTriggerGameMatch,
			Trigger jobTriggerResultSet) throws IOException {
		SchedulerFactoryBean factoryRun = new SchedulerFactoryBean();
		factoryRun.setSchedulerName("QJob");
		factoryRun.setJobFactory(jobFactoryRun);
		factoryRun.setQuartzProperties(quartzProperties());
		// aqui vai os jobs
		factoryRun.setTriggers(
				jobTriggerGameMatch, jobTriggerResultSet,
				cronTriggerFactoryBeanDistributeCoins().getObject(), 
				cronTriggerFactoryBeanIsWeek().getObject(),
				cronTriggerFactoryBeanUpdateRanking().getObject());
		LOG.info("Starting jobs..... Successful!");
		return factoryRun;
	}

	@Bean
	public JobFactory jobFactoryGameMatch(ApplicationContext applicationContext) {
		AutowiringBeanJobFactory jobFactory = new AutowiringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	// << ----------------- Game Match Job Config ----------------------------->>

	@Bean
	public SimpleTriggerFactoryBean jobTriggerGameMatch(@Qualifier("jobDetailGameMatch") JobDetail jobDetailGameMatch,
			@Value("${job.gamematch.frequency}") long frequencyGameMatch) {
		SimpleTriggerFactoryBean factoryBeanGameMatch = new SimpleTriggerFactoryBean();
		factoryBeanGameMatch.setJobDetail(jobDetailGameMatch);
		factoryBeanGameMatch.setStartDelay(0L);
		factoryBeanGameMatch.setRepeatInterval(frequencyGameMatch);
		factoryBeanGameMatch.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		return factoryBeanGameMatch;
	}

	@Bean
	public JobDetailFactoryBean jobDetailGameMatch() {
		JobDetailFactoryBean factoryBeanGameMatch = new JobDetailFactoryBean();
		factoryBeanGameMatch.setJobClass(CloseGameMatchJob.class);
		factoryBeanGameMatch.setDurability(true);
		return factoryBeanGameMatch;
	}

	// << ----------------- Result Set Job Config ----------------------------->>

	@Bean
	public SimpleTriggerFactoryBean jobTriggerResultSet(@Qualifier("jobDetailResultSet") JobDetail jobDetailResultSet,
			@Value("${job.isresultset.frequency}") long frequencyResultSet) {
		SimpleTriggerFactoryBean factoryBeanResultSet = new SimpleTriggerFactoryBean();
		factoryBeanResultSet.setJobDetail(jobDetailResultSet);
		factoryBeanResultSet.setStartDelay(0L);
		factoryBeanResultSet.setRepeatInterval(frequencyResultSet);
		factoryBeanResultSet.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		return factoryBeanResultSet;
	}

	@Bean
	public JobDetailFactoryBean jobDetailResultSet() {
		JobDetailFactoryBean factoryBeanResultSet = new JobDetailFactoryBean();
		factoryBeanResultSet.setJobClass(ResultSetJob.class);
		factoryBeanResultSet.setDurability(true);
		return factoryBeanResultSet;
	}

	// << ----------------- DistributeCoins Job Config------------------------>>

	@Bean
	public JobDetailFactoryBean jobDetailDistributeCoins() {
		JobDetailFactoryBean factoryBeanDistributeCoins = new JobDetailFactoryBean();
		factoryBeanDistributeCoins.setJobClass(DistributeCoinsJob.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("DistributeCoins", "RAM");
		factoryBeanDistributeCoins.setJobDataAsMap(map);
		factoryBeanDistributeCoins.setDurability(true);
		return factoryBeanDistributeCoins;
	}

	@Value("${job.distributecoins.cronexpression}")
	String cronExpression;

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBeanDistributeCoins() {
		CronTriggerFactoryBean factoryDistributeCoins = new CronTriggerFactoryBean();
		factoryDistributeCoins.setJobDetail(jobDetailDistributeCoins().getObject());
		factoryDistributeCoins.setCronExpression(cronExpression);
		return factoryDistributeCoins;
	}

	// << ----------------- IsWeek Job Config------------------------>>

	@Bean
	public JobDetailFactoryBean jobDetailIsWeek() {
		JobDetailFactoryBean factoryBeanIsWeek = new JobDetailFactoryBean();
		factoryBeanIsWeek.setJobClass(IsWeekGameMatchJob.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("IsWeek", "RAM");
		factoryBeanIsWeek.setJobDataAsMap(map);
		factoryBeanIsWeek.setDurability(true);
		return factoryBeanIsWeek;
	}

	@Value("${job.isweek.cronexpression}")
	String cronExpressionIsWeek;

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBeanIsWeek() {
		CronTriggerFactoryBean factoryIsWeek = new CronTriggerFactoryBean();
		factoryIsWeek.setJobDetail(jobDetailIsWeek().getObject());
		factoryIsWeek.setCronExpression(cronExpressionIsWeek);
		return factoryIsWeek;
	}
	
	// << ----------------- UpdateRanking Job Config------------------------>>

	@Bean
	public JobDetailFactoryBean jobDetailUpdateRanking() {
		JobDetailFactoryBean factoryBeanUpdateRanking = new JobDetailFactoryBean();
		factoryBeanUpdateRanking.setJobClass(UpdateRankingJob.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("UpdateRanking", "RAM");
		factoryBeanUpdateRanking.setJobDataAsMap(map);
		factoryBeanUpdateRanking.setDurability(true);
		return factoryBeanUpdateRanking;
	}

	@Value("${job.updateranking.cronexpression}")
	String cronExpressionUpdateRanking;

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBeanUpdateRanking() {
		CronTriggerFactoryBean factoryUpdateRanking = new CronTriggerFactoryBean();
		factoryUpdateRanking.setJobDetail(jobDetailUpdateRanking().getObject());
		factoryUpdateRanking.setCronExpression(cronExpressionUpdateRanking);
		return factoryUpdateRanking;
	}
	

}