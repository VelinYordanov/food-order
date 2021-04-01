package com.github.velinyordanov.foodorder.services;

import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthenticationService {
	Optional<UsernamePasswordAuthenticationToken> getAuthenticationToken(String jwtToken);
}
