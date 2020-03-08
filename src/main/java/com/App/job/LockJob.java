package com.App.job;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
public class LockJob {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_lockjob;
	
	@Column
	@Type(type = "true_false")
	private boolean isLock;
	
	@Column(length = 30)
	private String jobName;

	public long getId_lockjob() {
		return id_lockjob;
	}

	public void setId_lockjob(long id_lockjob) {
		this.id_lockjob = id_lockjob;
	}

	public boolean isLock() {
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
