package com.github.velinyordanov.foodorder.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.services.RestaurantsUserDetailsService;

@Service
public class RestaurantsUserDetailsServiceImpl implements RestaurantsUserDetailsService {
	private final RestaurantsRepository restaurantsRepository;

	public RestaurantsUserDetailsServiceImpl(RestaurantsRepository restaurantsRepository) {
		this.restaurantsRepository = restaurantsRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.restaurantsRepository
				.findByEmail(email)
				.orElseThrow(
						() -> new UsernameNotFoundException(
								"Could not find restaurant with this username and password"));
	}
}
