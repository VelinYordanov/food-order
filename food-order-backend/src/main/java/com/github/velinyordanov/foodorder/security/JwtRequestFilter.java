package com.github.velinyordanov.foodorder.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.velinyordanov.foodorder.services.AuthenticationService;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;

    public JwtRequestFilter(
	    AuthenticationService authenticationService) {
	this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	    throws ServletException,
	    IOException {

	final String requestTokenHeader = request.getHeader("Authorization");

	if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
	    String jwtToken = requestTokenHeader.substring(7);
	    this.authenticationService.getAuthenticationToken(jwtToken)
		    .ifPresent(SecurityContextHolder.getContext()::setAuthentication);
	} else {
	    logger.warn("Missing bearer token");
	}

	chain.doFilter(request, response);
    }
}
