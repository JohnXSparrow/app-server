package com.App.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.App.model.UserLogin;
import com.App.repository.UserLoginRepository;

@Component
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private UserLoginRepository userLoginRepository;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

		UserLogin userLogin = userLoginRepository.findByUsername(s);
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(userLogin.getUserRole().name()));

		return new User(userLogin.getUsername(), userLogin.getPassword(), authorities);
	}
}
