package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;

public class OrderDto {
    private String id;
    private RestaurantOrderDto restaurant;
    private CustomerOrderDto customer;
    private AddressDto address;
    private Collection<FoodOrderDto> foods;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public RestaurantOrderDto getRestaurant() {
	return restaurant;
    }

    public void setRestaurant(RestaurantOrderDto restaurant) {
	this.restaurant = restaurant;
    }

    public CustomerOrderDto getCustomer() {
	return customer;
    }

    public void setCustomer(CustomerOrderDto customer) {
	this.customer = customer;
    }

    public AddressDto getAddress() {
	return address;
    }

    public void setAddress(AddressDto address) {
	this.address = address;
    }

    public Collection<FoodOrderDto> getFoods() {
	return foods;
    }

    public void setFoods(Collection<FoodOrderDto> foods) {
	this.foods = foods;
    }
}
