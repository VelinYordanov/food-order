package com.github.velinyordanov.foodorder.controllers.test;

import java.util.Set;

import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;

public class ValidUserProvider {
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
	
	public Customer getValidCustomer() {
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

		return customer;
	}
}
