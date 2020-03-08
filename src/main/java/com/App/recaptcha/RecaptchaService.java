package com.App.recaptcha;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RecaptchaService {

	public boolean checkRecaptcha(String recap)
			throws JsonProcessingException, ParseException, ClientProtocolException, IOException {

		String secret = "secret";

		HttpClient client = HttpClients.custom().build();
		HttpUriRequest request = RequestBuilder.post().setUri("https://www.google.com/recaptcha/api/siteverify")
				.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").addParameter("secret", secret)
				.addParameter("response", recap).build();

		JsonNode jsonResponse = new ObjectMapper()
				.readTree(EntityUtils.toString(client.execute(request).getEntity(), "UTF-8"));

		return Boolean.valueOf(jsonResponse.get("success").asText());

	}

	public boolean callRecaptcha(String recap) {
		boolean isValid = false;

		try {
			isValid = checkRecaptcha(recap);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		return isValid;
	}

}