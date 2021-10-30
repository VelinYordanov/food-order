package com.github.velinyordanov.foodorder.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.services.RestaurantsUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class RestaurantAuthenticationProviderTest {
	@Mock
	private RestaurantsUserDetailsService restaurantsUserDetailsService;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;
	
	@Test
	public void authenticateShould_returnNull_whenAuthenticationDetailsIsNull() {
		Authentication authentication = mock(Authentication.class);
		given(authentication.getDetails()).willReturn(null);
		
		assertNull(this.restaurantAuthenticationProvider.authenticate(authentication));
	}
	
	@Test
	public void authenticateShould_returnNull_whenAuthenticationDetailsIsUserTypeCustomer() {
		Authentication authentication = mock(Authentication.class);
		given(authentication.getDetails()).willReturn(UserType.Customer);
		
		assertNull(this.restaurantAuthenticationProvider.authenticate(authentication));
	}
	
	@Test
	public void authenticateShould_returnCorrectAuthentication_whenAuthenticationDetailsAreUserTypeRestaurant() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setEmail("restaurantName");
		restaurant.setPassword("restaurantPassword");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");
		restaurant.setCategories(Set.of());

		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_RESTAURANT");
		authority.setRestaurants(Set.of(restaurant));
		restaurant.setAuthorities(Set.of(authority));
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(restaurant, restaurant.getPassword());
		usernamePasswordAuthenticationToken.setDetails(UserType.Restaurant);
		
		given(this.restaurantsUserDetailsService.loadUserByUsername("restaurantName")).willReturn(restaurant);
		given(this.passwordEncoder.matches(anyString(), anyString())).willAnswer(answer -> answer.getArgument(0).equals(answer.getArgument(1)));
		
		assertNotNull(this.restaurantAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
	}
}
