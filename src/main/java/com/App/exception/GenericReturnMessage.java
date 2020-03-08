package com.App.exception;

public class GenericReturnMessage {

	private String defaultMessage;
	private int code;

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public int getCode() {
		return code;
	}

	public GenericReturnMessage(int code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}
}
