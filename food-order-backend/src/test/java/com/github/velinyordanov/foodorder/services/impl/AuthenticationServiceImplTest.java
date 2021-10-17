package com.github.velinyordanov.foodorder.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.JwtUserDto;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.customers.CustomersAuthenticationService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsAuthenticationService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
	@Mock
	private CustomersAuthenticationService customersAuthenticationService;

	@Mock
	private RestaurantsAuthenticationService restaurantsAuthenticationService;

	@Mock
	private JwtTokenService jwtTokenUtil;

	@InjectMocks
	private AuthenticationServiceImpl authenticationServiceImpl;

	@ParameterizedTest
	@ValueSource(classes = {
			UnsupportedJwtException.class,
			MalformedJwtException.class,
			SignatureException.class,
			ExpiredJwtException.class,
			IllegalStateException.class,
	})
	public void getAuthenticationTokenShould_returnAnEmptyOptional_whenTokenIsNotValid(
			Class<Exception> exceptionClass) {
		given(this.jwtTokenUtil.parseToken("token")).willThrow(exceptionClass);

		assertEquals(Optional.empty(), this.authenticationServiceImpl.getAuthenticationToken("token"));
	}

	@Test
	public void getAuthenticationTokenShould_returnAnEmptyOptional_whenUserHasRoleRestaurantButCannotBeFoundById() {
		JwtUserDto jwtUserDto = new JwtUserDto();
		jwtUserDto.setId("notUserId");
		jwtUserDto.setAuthorities(List.of("ROLE_RESTAURANT"));
		given(this.jwtTokenUtil.parseToken("token")).willReturn(jwtUserDto);

		assertEquals(Optional.empty(), this.authenticationServiceImpl.getAuthenticationToken("token"));
	}

	@Test
	public void getAuthenticationTokenShould_returnAuthenticationTokenWithCorrectData_whenUserHasRoleRestaurantAndIsFound() {
		JwtUserDto jwtUserDto = new JwtUserDto();
		jwtUserDto.setId("restaurantId");
		jwtUserDto.setAuthorities(List.of("ROLE_RESTAURANT"));
		given(this.jwtTokenUtil.parseToken("token")).willReturn(jwtUserDto);

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setEmail("restaurantEmail");
		restaurant.setName("restaurantName");
		restaurant.setPassword("restaurantPassword");
		restaurant.setDescription("restaurantDescription");

		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_RESTAURANT");
		authority.setRestaurants(Set.of(restaurant));
		restaurant.setAuthorities(Set.of(authority));

		given(this.restaurantsAuthenticationService.findById("restaurantId")).willReturn(Optional.of(restaurant));

		UsernamePasswordAuthenticationToken authenticationToken = this.authenticationServiceImpl
				.getAuthenticationToken("token").get();

		assertEquals(restaurant, authenticationToken.getPrincipal());
		assertEquals(restaurant.getPassword(), authenticationToken.getCredentials());
		assertThat(authenticationToken.getAuthorities())
				.usingRecursiveComparison()
				.isEqualTo(restaurant.getAuthorities());
	}

	@Test
	public void getAuthenticationTokenShould_returnAnEmptyOptional_whenUserHasRoleCustomerButCannotBeFoundById() {
		JwtUserDto jwtUserDto = new JwtUserDto();
		jwtUserDto.setId("notUserId");
		jwtUserDto.setAuthorities(List.of("ROLE_CUSTOMER"));
		given(this.jwtTokenUtil.parseToken("token")).willReturn(jwtUserDto);

		assertEquals(Optional.empty(), this.authenticationServiceImpl.getAuthenticationToken("token"));
	}

	@Test
	public void getAuthenticationTokenShould_returnAuthenticationTokenWithCorrectData_whenUserHasRoleCustomerAndIsFound() {
		JwtUserDto jwtUserDto = new JwtUserDto();
		jwtUserDto.setId("customerId");
		jwtUserDto.setAuthorities(List.of("ROLE_CUSTOMER"));
		given(this.jwtTokenUtil.parseToken("token")).willReturn(jwtUserDto);

		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setEmail("customerEmail");
		customer.setPassword("customerPassword");
		customer.setName("customerName");
		customer.setPhoneNumber("customerPhone");

		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_CUSTOMER");
		authority.setCustomers(Set.of(customer));
		customer.setAuthorities(Set.of(authority));

		given(this.customersAuthenticationService.findById("customerId")).willReturn(Optional.of(customer));

		UsernamePasswordAuthenticationToken authenticationToken = this.authenticationServiceImpl
				.getAuthenticationToken("token").get();

		assertEquals(customer, authenticationToken.getPrincipal());
		assertEquals(customer.getPassword(), authenticationToken.getCredentials());
		assertThat(authenticationToken.getAuthorities())
				.usingRecursiveComparison()
				.isEqualTo(customer.getAuthorities());
	}
	
	@Test
	public void getAuthenticationTokenShould_returnAnEmptyOptional_whenUserHasUndefinedRole() {
		JwtUserDto jwtUserDto = new JwtUserDto();
		jwtUserDto.setId("notUserId");
		jwtUserDto.setAuthorities(List.of("ROLE_UNDEFINED"));
		given(this.jwtTokenUtil.parseToken("token")).willReturn(jwtUserDto);

		assertEquals(Optional.empty(), this.authenticationServiceImpl.getAuthenticationToken("token"));
	}
	
	@Test
	public void getAuthenticationTokenShould_returnAnEmptyOptional_whenUserHasNoRole() {
		JwtUserDto jwtUserDto = new JwtUserDto();
		jwtUserDto.setId("notUserId");
		jwtUserDto.setAuthorities(List.of());
		given(this.jwtTokenUtil.parseToken("token")).willReturn(jwtUserDto);

		assertEquals(Optional.empty(), this.authenticationServiceImpl.getAuthenticationToken("token"));
	}
}
