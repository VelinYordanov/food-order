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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addressId == null) ? 0 : addressId.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((discountCodeId == null) ? 0 : discountCodeId.hashCode());
		result = prime * result + ((restaurantId == null) ? 0 : restaurantId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderCreateDto other = (OrderCreateDto) obj;
		if (addressId == null) {
			if (other.addressId != null)
				return false;
		} else if (!addressId.equals(other.addressId))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (discountCodeId == null) {
			if (other.discountCodeId != null)
				return false;
		} else if (!discountCodeId.equals(other.discountCodeId))
			return false;
		if (restaurantId == null) {
			if (other.restaurantId != null)
				return false;
		} else if (!restaurantId.equals(other.restaurantId))
			return false;
		return true;
	}
}
