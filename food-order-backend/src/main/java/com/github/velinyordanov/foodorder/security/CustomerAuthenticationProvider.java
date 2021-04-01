package com.github.velinyordanov.foodorder.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.services.CustomersUserDetailsService;

@Component
public class CustomerAuthenticationProvider extends DaoAuthenticationProvider {
	public CustomerAuthenticationProvider(CustomersUserDetailsService customersUserDetailsService,
			PasswordEncoder passwordEncoder) {
		this.setUserDetailsService(customersUserDetailsService);
		this.setPasswordEncoder(passwordEncoder);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object details = authentication.getDetails();
		if (details instanceof UserType) {
			UserType userType = (UserType) details;
			if (UserType.Customer.equals(userType)) {
				return super.authenticate(authentication);
			}
		}

		return null;
	}
}
