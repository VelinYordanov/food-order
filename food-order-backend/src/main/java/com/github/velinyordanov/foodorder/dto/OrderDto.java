package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;
import java.util.Date;

import com.github.velinyordanov.foodorder.data.entities.Status;

public class OrderDto {
	private String id;
	private Status status;
	private Date createdOn;
	private RestaurantOrderDto restaurant;
	private CustomerOrderDto customer;
	private AddressDto address;
	private DiscountCodeDto discountCode;
	private Collection<FoodOrderDto> foods;
	private String comment;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
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

	public DiscountCodeDto getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(DiscountCodeDto discountCode) {
		this.discountCode = discountCode;
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
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((discountCode == null) ? 0 : discountCode.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((restaurant == null) ? 0 : restaurant.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		OrderDto other = (OrderDto) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (discountCode == null) {
			if (other.discountCode != null)
				return false;
		} else if (!discountCode.equals(other.discountCode))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (restaurant == null) {
			if (other.restaurant != null)
				return false;
		} else if (!restaurant.equals(other.restaurant))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
}
