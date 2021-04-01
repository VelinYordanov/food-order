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
}
