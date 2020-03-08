package com.App.model;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.App.enumeration.PaymentStatusEnum;
import com.App.enumeration.ProfilePlanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class BuyPlanPP {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_buyplanpp;

	@Column(nullable = false)
	private String pay_id;

	@NotNull
	private ProfilePlanEnum ProfilePlan;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatusEnum status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	@Column(nullable = false)
	private Calendar dateBuy;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private UserLogin userLogin;

	public long getId_buyplanpp() {
		return id_buyplanpp;
	}

	public void setId_buyplanpp(long id_buyplanpp) {
		this.id_buyplanpp = id_buyplanpp;
	}

	public String getPay_id() {
		return pay_id;
	}

	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}

	public ProfilePlanEnum getProfilePlan() {
		return ProfilePlan;
	}

	public void setProfilePlan(ProfilePlanEnum profilePlan) {
		ProfilePlan = profilePlan;
	}

	public PaymentStatusEnum getStatus() {
		return status;
	}

	public void setStatus(PaymentStatusEnum status) {
		this.status = status;
	}

	public Calendar getDateBuy() {
		return dateBuy;
	}

	public void setDateBuy(Calendar dateBuy) {
		this.dateBuy = dateBuy;
	}

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

}
