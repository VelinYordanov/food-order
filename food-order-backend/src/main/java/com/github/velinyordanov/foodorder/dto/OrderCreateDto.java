package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class OrderCreateDto {
    @NotBlank(message = "No restaurant provided")
    private String restaurantId;

    @NotBlank(message = "No customer provided")
    private String customerId;

    @NotBlank(message = "No address provided")
    private String addressId;

    @NotEmpty(message = "No foods provided")
    private Collection<OrderFoodDto> foods;

    public String getRestaurantId() {
	return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
	this.restaurantId = restaurantId;
    }

    public String getCustomerId() {
	return customerId;
    }

    public void setCustomerId(String customerId) {
	this.customerId = customerId;
    }

    public String getAddressId() {
	return addressId;
    }

    public void setAddressId(String addressId) {
	this.addressId = addressId;
    }

    public Collection<OrderFoodDto> getFoods() {
	return foods;
    }

    public void setFoods(Collection<OrderFoodDto> foods) {
	this.foods = foods;
    }
}
