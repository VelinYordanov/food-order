package com.github.velinyordanov.foodorder.services.impl;

import java.text.MessageFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.dto.JwtUserDto;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.CustomersService;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final CustomersService customersService;
    private final RestaurantsService restaurantsService;
    private final JwtTokenService jwtTokenUtil;

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    public AuthenticationServiceImpl(
	    @Lazy CustomersService customersService,
	    @Lazy RestaurantsService restaurantsService,
	    JwtTokenService jwtTokenUtil) {
	super();
	this.customersService = customersService;
	this.restaurantsService = restaurantsService;
	this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Optional<UsernamePasswordAuthenticationToken> getAuthenticationToken(String jwtToken) {
	JwtUserDto user = null;
	try {
	    user = jwtTokenUtil.parseToken(jwtToken);
	} catch (UnsupportedJwtException
		| MalformedJwtException
		| SignatureException
		| ExpiredJwtException
		| IllegalStateException e) {
	    logger.error("Expired or not valid jwt token " + jwtToken);
	    return Optional.empty();
	}

	if (user.getAuthorities()
		.stream()
		.filter(authority -> "ROLE_RESTAURANT".equals(authority))
		.findAny()
		.isPresent()) {
	    Optional<UsernamePasswordAuthenticationToken> tokenOptional = this.restaurantsService.findById(user.getId())
		    .map(restaurant -> new UsernamePasswordAuthenticationToken(restaurant,
			    restaurant.getPassword(),
			    restaurant.getAuthorities()));
	    ;

	    if (tokenOptional.isEmpty()) {
		logger.error("Authentication failed! Restaurant with id " + user.getId() + " not found!");
	    }

	    return tokenOptional;
	}

	if (user.getAuthorities()
		.stream()
		.filter(authority -> "ROLE_CUSTOMER".equals(authority))
		.findAny()
		.isPresent()) {
	    Optional<UsernamePasswordAuthenticationToken> tokenOptional = this.customersService.findById(user.getId())
		    .map(customer -> new UsernamePasswordAuthenticationToken(customer,
			    customer.getPassword(),
			    customer.getAuthorities()));

	    if (tokenOptional.isEmpty()) {
		logger.error("Authentication failed! Customer with id " + user.getId() + " not found!");
	    }

	    return tokenOptional;
	}

	logger.error(MessageFormat.format("Jwt token with unknown role {0}. Authentication denied.", jwtToken));
	return Optional.empty();
    }
}