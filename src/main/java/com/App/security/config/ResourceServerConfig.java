package com.App.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private ResourceServerTokenServices tokenServices;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("rs").tokenServices(tokenServices);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers().and().authorizeRequests()
		.antMatchers("/register").permitAll()
		.antMatchers("/confirmAccount").permitAll()
		.antMatchers("/resendEmail").permitAll()
		.antMatchers("/login").permitAll()
		.antMatchers("/forgotPassword").permitAll()
		.antMatchers("/resetForgottenPassword").permitAll()
		.antMatchers("/support/visitor").permitAll()
		.antMatchers("/unsubscribe").permitAll()
		.antMatchers("/gamematch/list/public").permitAll()
		.antMatchers("/dateTimeServer").permitAll()
		.antMatchers("/user/buyCoin/status").permitAll()
		.antMatchers("/user/buyPlan/status").permitAll()
		.antMatchers("/user/ranking/public").permitAll()
		.anyRequest().authenticated();
	}
}
