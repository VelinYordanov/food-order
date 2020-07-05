package com.github.velinyordanov.foodorder.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.services.RestaurantsUserDetailsService;

@Component
public class RestaurantAuthenticationProvider extends DaoAuthenticationProvider {
    public RestaurantAuthenticationProvider(
	    RestaurantsUserDetailsService restaurantsUserDetailsService,
	    PasswordEncoder passwordEncoder) {
	this.setUserDetailsService(restaurantsUserDetailsService);
	this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	Object details = authentication.getDetails();
	if (details instanceof UserType) {
	    UserType userType = (UserType) details;
	    if (UserType.Restaurant.equals(userType)) {
		return super.authenticate(authentication);
	    }
	}

	return null;
    }
}
