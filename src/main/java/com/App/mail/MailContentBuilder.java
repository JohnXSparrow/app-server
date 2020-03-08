package com.App.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(MailConstrutorBean mailBean) {
        Context context = new Context();
        
        if (mailBean.getFirstName() != null) {
        	context.setVariable("firstName", mailBean.getFirstName());
        }
        
        if (mailBean.getMessage() != null) {
        	context.setVariable("message", mailBean.getMessage());
        }
        
        if (mailBean.getLink() != null) {
        	context.setVariable("link", mailBean.getLink());
        }
        
        if (mailBean.getFirstName() != null) {
        	context.setVariable("totalMoney", mailBean.getTotalMoney());
        }
        
        if (mailBean.getTax() != null) {
        	context.setVariable("tax", mailBean.getTax());
        }
        
        if (mailBean.getValueToReceive() != null) {
        	context.setVariable("valueToReceive", mailBean.getValueToReceive());
        }
        
        if (mailBean.getFirstName() != null) {
        	context.setVariable("buttonMessage", mailBean.getButtonTitle());
        }   
        
        return templateEngine.process(mailBean.getTemplateEngine(), context);
    }

}