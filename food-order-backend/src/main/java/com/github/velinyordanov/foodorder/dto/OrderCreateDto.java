package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class OrderCreateDto {
	@NotBlank(message = EMPTY_ORDER_RESTAURANT)
	private String restaurantId;

	@NotBlank(message = EMPTY_ORDER_CUSTOMER)
	private String customerId;

	@NotBlank(message = EMPTY_ORDER_ADDRESS)
	private String addressId;

	@NotEmpty(message = EMPTY_ORDER_FOODS)
	private Collection<OrderFoodDto> foods;

	private String discountCodeId;

	private String comment;

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

	public String getDiscountCodeId() {
		return discountCodeId;
	}

	public void setDiscountCodeId(String discountCodeId) {
		this.discountCodeId = discountCodeId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
