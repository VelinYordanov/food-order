package com.github.velinyordanov.foodorder.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.velinyordanov.foodorder.config.FoodOrderConfigurationProperties;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.JwtUserDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceImplTest {
	@Mock
	private FoodOrderConfigurationProperties foodOrderConfigurationProperties;

	@InjectMocks
	private JwtTokenServiceImpl jwtTokenServiceImpl;

	@Test
	public void generateTokenShould_generateTokenWithCorrectClaims_whenUserIsACustomer() {
		given(this.foodOrderConfigurationProperties.getSecret()).willReturn("secret");

		Customer user = new Customer();
		user.setId("customerId");
		user.setEmail("usernameEmail");
		user.setName("customerName");

		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_CUSTOMER");
		authority.setCustomers(Set.of(user));
		user.setAuthorities(Set.of(authority));

		String token = this.jwtTokenServiceImpl.generateToken(user);
		Claims claims = Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
		
		assertEquals(List.of("ROLE_CUSTOMER"), claims.get("authorities", ArrayList.class));
		assertEquals(user.getId(), claims.get("id", String.class));
		assertEquals(user.getEmail(), claims.get("email", String.class));
		assertEquals(user.getName(), claims.get("name", String.class));
	}

	@Test
	public void generateTokenShould_generateTokenWithCorrectClaims_whenUserIsARestaurant() {
		given(this.foodOrderConfigurationProperties.getSecret()).willReturn("secret");

		Restaurant user = new Restaurant();
		user.setId("customerId");
		user.setEmail("usernameEmail");
		user.setName("customerName");

		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_RESTAURANT");
		authority.setRestaurants(Set.of(user));
		user.setAuthorities(Set.of(authority));

		String token = this.jwtTokenServiceImpl.generateToken(user);
		Claims claims = Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
		
		assertEquals(List.of("ROLE_RESTAURANT"), claims.get("authorities", ArrayList.class));
		assertEquals(user.getId(), claims.get("id", String.class));
		assertEquals(user.getEmail(), claims.get("email", String.class));
		assertEquals(user.getName(), claims.get("name", String.class));
	}

	@Test
	public void parseTokenShould_returnAUJwtUSerDtoWithCorrectData_whenUserIsACustomer() {
		given(this.foodOrderConfigurationProperties.getSecret()).willReturn("secret");

		Map<String, Object> claims = new HashMap<>();
		claims.put("email", "customerEmail");
		claims.put("name", "customerName");
		claims.put("authorities", List.of("ROLE_CUSTOMER"));
		claims.put("id", "customerId");

		String token = Jwts.builder()
				.setClaims(claims)
				.setSubject("customerId")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 10000))
				.signWith(SignatureAlgorithm.HS512, "secret")
				.compact();

		JwtUserDto user = this.jwtTokenServiceImpl.parseToken(token);

		assertEquals("customerId", user.getId());
		assertEquals("customerEmail", user.getEmail());
		assertEquals("customerName", user.getName());
		assertEquals(List.of("ROLE_CUSTOMER"), user.getAuthorities());
	}
	
	@Test
	public void parseTokenShould_returnAUJwtUSerDtoWithCorrectData_whenUserIsARestaurant() {
		given(this.foodOrderConfigurationProperties.getSecret()).willReturn("secret");

		Map<String, Object> claims = new HashMap<>();
		claims.put("email", "restaurantEmail");
		claims.put("name", "restaurantName");
		claims.put("authorities", List.of("ROLE_RESTAURANT"));
		claims.put("id", "restaurantId");

		String token = Jwts.builder()
				.setClaims(claims)
				.setSubject("restaurantId")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000))
				.signWith(SignatureAlgorithm.HS512, "secret")
				.compact();

		JwtUserDto user = this.jwtTokenServiceImpl.parseToken(token);

		assertEquals("restaurantId", user.getId());
		assertEquals("restaurantEmail", user.getEmail());
		assertEquals("restaurantName", user.getName());
		assertEquals(List.of("ROLE_RESTAURANT"), user.getAuthorities());
	}
}
