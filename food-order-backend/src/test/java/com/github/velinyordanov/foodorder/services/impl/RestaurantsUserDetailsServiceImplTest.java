package com.github.velinyordanov.foodorder.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;

@ExtendWith(MockitoExtension.class)
public class RestaurantsUserDetailsServiceImplTest {
	@Mock
	private RestaurantsRepository restaurantsRepository;

	@InjectMocks
	private RestaurantsUserDetailsServiceImpl restaurantsUserDetailsServiceImpl;

	@Test
	public void loadUserByUsernameShould_throwUsernameNotFoundException_whenRestaurantIsNotFoundByEmail() {
		given(this.restaurantsRepository.findByEmail("restaurantEmail")).willReturn(Optional.empty());
		
		UsernameNotFoundException usernameNotFoundException = assertThrows(UsernameNotFoundException.class, () -> this.restaurantsUserDetailsServiceImpl.loadUserByUsername("restaurantEmail"));
		
		assertEquals("Could not find restaurant with this username and password", usernameNotFoundException.getMessage());
	}
	
	@Test
	public void loadUserByUsernameShould_returnCorrectData_whenRestaurantIsFoundByEmail() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");
		restaurant.setCategories(Set.of());
		
		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_CUSTOMER");
		authority.setRestaurants(Set.of(restaurant));
		restaurant.setAuthorities(Set.of(authority));
		
		given(this.restaurantsRepository.findByEmail("restaurantEmail")).willReturn(Optional.of(restaurant));
		
		UserDetails result = this.restaurantsUserDetailsServiceImpl.loadUserByUsername("restaurantEmail");
		
		assertThat(result).usingRecursiveComparison().isEqualTo(result);
	}
}
