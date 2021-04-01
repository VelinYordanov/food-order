package com.github.velinyordanov.foodorder.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.entities.BaseUser;
import com.github.velinyordanov.foodorder.dto.JwtUserDto;
import com.github.velinyordanov.foodorder.services.JwtTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
	private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	@Value("${jwt.secret}")
	private String secret;

	@Override
	public String generateToken(BaseUser customer) {
		Collection<String> authorities = customer.getAuthorities().stream().map(authority -> authority.getAuthority())
				.collect(Collectors.toList());

		Map<String, Object> claims = new HashMap<>();
		claims.put("username", customer.getUsername());
		claims.put("name", customer.getName());
		claims.put("authorities", authorities);
		claims.put("id", customer.getId());

		return this.generateToken(claims, customer.getId());
	}

	@Override
	public JwtUserDto parseToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		JwtUserDto result = new JwtUserDto();
		result.setAuthorities(claims.get("authorities", ArrayList.class));
		result.setId(claims.get("id", String.class));
		result.setEmail(claims.get("email", String.class));
		result.setName(claims.get("name", String.class));

		return result;
	}

	private String generateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
}
