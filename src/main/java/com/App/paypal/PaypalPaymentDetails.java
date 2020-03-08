package com.App.paypal;

public class PaypalPaymentDetails {

	private String status;
	private String id_pay;
	private String valuePayed;

	public String getValuePayed() {
		return valuePayed;
	}

	public void setValuePayed(String valuePayed) {
		this.valuePayed = valuePayed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId_pay() {
		return id_pay;
	}

	public void setId_pay(String id_pay) {
		this.id_pay = id_pay;
	}

	
}