package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsAuthenticationService;

@Service
@Transactional
public class RestaurantsAuthenticationServiceImpl implements RestaurantsAuthenticationService {
	private final FoodOrderData foodOrderData;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final Mapper mapper;
	private final JwtTokenService jwtTokenService;

	public RestaurantsAuthenticationServiceImpl(FoodOrderData foodOrderData,
			AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, Mapper mapper,
			JwtTokenService jwtTokenService) {
		this.foodOrderData = foodOrderData;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.mapper = mapper;
		this.jwtTokenService = jwtTokenService;
	}

	@Override
	public Optional<Restaurant> findById(String id) {
		return this.foodOrderData.restaurants().findById(id);
	}

	@Override
	public String register(@Valid RestaurantRegisterDto user) {
		if (this.foodOrderData.restaurants().existsByEmailOrName(user.getEmail(), user.getName())) {
			throw new DuplicateUserException("Email or restaurant name already exists!");
		}

		Restaurant restaurant = this.mapper.map(user, Restaurant.class);
		restaurant.setPassword(this.passwordEncoder.encode(user.getPassword()));
		Optional<Authority> authorityOptional = this.foodOrderData.authorities()
				.findFirstByAuthority("ROLE_RESTAURANT");
		Authority authority = null;
		if (authorityOptional.isPresent()) {
			authority = authorityOptional.get();
		} else {
			authority = new Authority();
			authority.setAuthority("ROLE_RESTAURANT");
		}

		Set<Authority> authorities = new HashSet<>();
		authorities.add(authority);
		restaurant.setAuthorities(authorities);
		authority.getRestaurants().add(restaurant);

		Restaurant savedRestaurant = this.foodOrderData.restaurants().save(restaurant);
		return this.jwtTokenService.generateToken(savedRestaurant);
	}

	@Override
	public String login(UserLoginDto user) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(),
				user.getPassword());
		token.setDetails(UserType.Restaurant);

		Authentication authentication = this.authenticationManager.authenticate(token);
		return this.jwtTokenService.generateToken((Restaurant) authentication.getPrincipal());
	}
}
