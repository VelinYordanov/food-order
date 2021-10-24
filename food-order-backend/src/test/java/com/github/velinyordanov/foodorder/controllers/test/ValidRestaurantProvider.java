package com.github.velinyordanov.foodorder.controllers.test;

import java.util.Set;

import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;

public class ValidRestaurantProvider {
	public Restaurant getValidRestaurant() {
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

		return restaurant;
	}
}
