package com.github.velinyordanov.foodorder.data.entities;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_ADDRESS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_CUSTOMER;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_FOODS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_RESTAURANT;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_STATUS;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Orders")
public class Order extends BaseEntity {
	@NotNull(message = EMPTY_STATUS)
	@Column(name = "Status", nullable = false)
	private Status status;

	@NotNull(message = EMPTY_ORDER_ADDRESS)
	@ManyToOne(optional = false)
	@JoinColumn(name = "AddressId")
	private Address address;

	@NotNull(message = EMPTY_ORDER_RESTAURANT)
	@ManyToOne(optional = false)
	@JoinColumn(name = "RestaurantId")
	private Restaurant restaurant;

	@NotNull(message = EMPTY_ORDER_CUSTOMER)
	@ManyToOne(optional = false)
	@JoinColumn(name = "CustomerId")
	private Customer customer;

	@NotEmpty(message = EMPTY_ORDER_FOODS)
	@OneToMany(mappedBy = "order", cascade = { CascadeType.ALL })
	private Set<OrderFood> foods;

	@ManyToOne()
	@JoinColumn(name = "DiscountCodeId")
	private DiscountCode discountCode;

	@Column(name = "Comment", columnDefinition = "nvarchar(max)")
	private String comment;

	public Order() {
		super();
		this.setStatus(Status.Pending);
		this.setFoods(new HashSet<>());
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Set<OrderFood> getFoods() {
		return foods;
	}

	public void setFoods(Set<OrderFood> foods) {
		this.foods = foods;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public void setUser(Customer user) {
		this.customer = user;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DiscountCode getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(DiscountCode discountCode) {
		this.discountCode = discountCode;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Order [getStatus()=" + getStatus() + ", getRestaurant()=" + getRestaurant() + ", getUser()="
				+ getCustomer() + ", getFoods()=" + getFoods() + "]";
	}
}
