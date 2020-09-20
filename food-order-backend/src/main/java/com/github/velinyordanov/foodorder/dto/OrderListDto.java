package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;

public class OrderListDto {
    private String id;
    private RestaurantOrderDto restaurant;
    private CustomerOrderDto customer;
    private Collection<FoodOrderListDto> foods;

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

    public Collection<FoodOrderListDto> getFoods() {
	return foods;
    }

    public void setFoods(Collection<FoodOrderListDto> foods) {
	this.foods = foods;
    }
}
