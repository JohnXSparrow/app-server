package com.App.paypal;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PayPalConection {
	private static final Logger LOG = LoggerFactory.getLogger(PayPalConection.class);

	// live or sandbox
	private final String mode = "sandbox";

	// sandbox
	private final String clientId = "id";
	private final String clientSecret = "secret";
	private final String urlBaseSuccess = "http://localhost:4200/paypalPayment/success?tb=";
	private final String urlBaseCancel = "http://localhost:4200/paypalPayment/canceled?tb=";


	public PaypalRequestBean createPayment(String nameProduct, String totalToPay, String firstName, String lk) {
		PaypalRequestBean request = new PaypalRequestBean();

		Amount amount = new Amount();
		amount.setCurrency("BRL");
		amount.setTotal(totalToPay);

		Transaction transaction = new Transaction();
		transaction.setAmount(amount);

		Item item = new Item();
		item.setQuantity("1");
		item.setCurrency("BRL");
		item.setName(nameProduct);
		item.setPrice(totalToPay);

		ItemList itemList = new ItemList();
		List<Item> items = new ArrayList<Item>();
		items.add(item);
		itemList.setItems(items);
		transaction.setItemList(itemList);

		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		PayerInfo payerInfo = new PayerInfo();
		payerInfo.setFirstName(firstName);

		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
		payer.setPayerInfo(payerInfo);

		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(urlBaseCancel);
		redirectUrls.setReturnUrl(urlBaseSuccess + lk);
		payment.setRedirectUrls(redirectUrls);

		Payment createdPayment;
		try {
			String redirectUrl = "";
			APIContext context = new APIContext(clientId, clientSecret, mode);
			createdPayment = payment.create(context);
			if (createdPayment != null) {
				List<Links> links = createdPayment.getLinks();
				for (Links link : links) {
					if (link.getRel().equals("approval_url")) {
						redirectUrl = link.getHref();
						break;
					}
				}
				request.setStatus("success");
				request.setRedirect_url(redirectUrl);
				request.setId_pay(createdPayment.getId());
			}
		} catch (PayPalRESTException e) {
			LOG.error("Error happened during payment creation! ");
			return null;
		}
		return request;
	}

	public PaypalPaymentDetails completePayment(PaypalResponseBean paypalResponse) {
		PaypalPaymentDetails paypalStatus = new PaypalPaymentDetails();

		Payment payment = new Payment();
		payment.setId(paypalResponse.getPaymentId());

		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(paypalResponse.getPayerID());

		try {
			APIContext context = new APIContext(clientId, clientSecret, mode);
			Payment createdPayment = payment.execute(context, paymentExecution);

			if (createdPayment != null) {
				paypalStatus.setStatus(createdPayment.getState());				
				paypalStatus.setId_pay(createdPayment.getId());				
				paypalStatus.setValuePayed(createdPayment.getTransactions().get(0).getAmount().getTotal());
			}
			
		} catch (PayPalRESTException e) {
			LOG.error(e.getDetails().toString());
			return null;
		}
		return paypalStatus;
	}

	public PaypalPaymentDetails verifyPayment(String id_pay) {
		PaypalPaymentDetails paypalStatus = new PaypalPaymentDetails();
		
		try {
			APIContext apiContext = new APIContext(clientId, clientSecret, mode);
			Payment payment = Payment.get(apiContext, id_pay);
			
			if (payment != null) {
				paypalStatus.setId_pay(payment.getId());
				paypalStatus.setStatus(payment.getState());			
				paypalStatus.setValuePayed(payment.getTransactions().get(0).getAmount().getTotal());	
			}

		} catch (PayPalRESTException e) {
			LOG.error(e.getDetails().toString());
			return null;
		}
		return paypalStatus;
	}

}