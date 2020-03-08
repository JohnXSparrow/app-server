package com.App.enumeration;

public enum MessageRedeemMoneyEnum {

	  INVALID_CPF("CPF inválido pela receita federal. Entre em contato conosco para validar seu CPF."),
	  USE_TERMS("Quebra dos Termos de Uso, sua conta está em avaliação.");

	  private final String description;

	  private MessageRedeemMoneyEnum(String description) {
	    this.description = description;
	  }

	  public String getDescription() {
	     return description;
	  }

	  @Override
	  public String toString() {
	    return description;
	  }

}
