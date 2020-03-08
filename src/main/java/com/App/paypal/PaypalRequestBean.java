package com.App.paypal;

public class PaypalRequestBean {

	private String redirect_url;

	private String id_pay;
	
	private String status;

	public String getId_pay() {
		return id_pay;
	}

	public void setId_pay(String id_pay) {
		this.id_pay = id_pay;
	}

	public String getRedirect_url() {
		return redirect_url;
	}

	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}