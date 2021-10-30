package com.github.velinyordanov.foodorder.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.config.FoodOrderConfigurationProperties;
import com.github.velinyordanov.foodorder.data.entities.BaseUser;
import com.github.velinyordanov.foodorder.dto.JwtUserDto;
import com.github.velinyordanov.foodorder.services.JwtTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
	private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	private final FoodOrderConfigurationProperties configurationProperties;

	public JwtTokenServiceImpl(FoodOrderConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	@Override
	public String generateToken(BaseUser user) {
		Collection<String> authorities = user.getAuthorities()
				.stream()
				.map(authority -> authority.getAuthority())
				.collect(Collectors.toList());

		Map<String, Object> claims = new HashMap<>();
		claims.put("email", user.getEmail());
		claims.put("name", user.getName());
		claims.put("authorities", authorities);
		claims.put("id", user.getId());

		return this.generateToken(claims, user.getId());
	}

	@Override
	public JwtUserDto parseToken(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(this.configurationProperties.getSecret())
				.parseClaimsJws(token)
				.getBody();

		JwtUserDto result = new JwtUserDto();
		result.setAuthorities(claims.get("authorities", ArrayList.class));
		result.setId(claims.get("id", String.class));
		result.setEmail(claims.get("email", String.class));
		result.setName(claims.get("name", String.class));

		return result;
	}

	private String generateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, this.configurationProperties.getSecret())
				.compact();
	}
}
