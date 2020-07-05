package com.github.velinyordanov.foodorder.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.JwtUserDto;
import com.github.velinyordanov.foodorder.services.CustomersService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;
import com.github.velinyordanov.foodorder.services.impl.JwtTokenServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final CustomersService customersService;
    private final RestaurantsService restaurantsService;
    private final JwtTokenServiceImpl jwtTokenUtil;

    public JwtRequestFilter(
	    CustomersService customersService,
	    RestaurantsService restaurantsService,
	    JwtTokenServiceImpl jwtTokenUtil) {
	super();
	this.customersService = customersService;
	this.restaurantsService = restaurantsService;
	this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	    throws ServletException,
	    IOException {

	final String requestTokenHeader = request.getHeader("Authorization");

	JwtUserDto user = null;
	if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
	    String jwtToken = requestTokenHeader.substring(7);
	    try {
		user = jwtTokenUtil.parseToken(jwtToken);
	    } catch (UnsupportedJwtException
		    | MalformedJwtException
		    | SignatureException
		    | ExpiredJwtException
		    | IllegalStateException e) {
		logger.error("Expired or not valid jwt token " + jwtToken);
		chain.doFilter(request, response);
		return;
	    }
	} else {
	    logger.warn("JWT Token does not begin with Bearer String");
	    chain.doFilter(request, response);
	    return;
	}

	if (user.getAuthorities()
		.stream()
		.filter(authority -> "ROLE_RESTAURANT".equals(authority))
		.findAny()
		.isPresent()) {
	    Optional<Restaurant> restaurantOptional = this.restaurantsService.findById(user.getId());

	    if (restaurantOptional.isPresent()) {
		Restaurant restaurant = restaurantOptional.get();
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(
				restaurant,
				restaurant.getPassword(),
				restaurant.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
	    } else {
		logger.error("Authentication failed! Restaurant with id " + user.getId() + " not found!");
	    }
	}

	if (user.getAuthorities()
		.stream()
		.filter(authority -> "ROLE_CUSTOMER".equals(authority))
		.findAny()
		.isPresent()) {
	    Optional<Customer> customerOptional = this.customersService.findById(user.getId());

	    if (customerOptional.isPresent()) {
		Customer customer = customerOptional.get();
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(customer,
				customer.getPassword(),
				customer.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
	    } else {
		logger.error("Authentication failed! Customer with id " + user.getId() + " not found!");
	    }
	}

	chain.doFilter(request, response);
    }
}
