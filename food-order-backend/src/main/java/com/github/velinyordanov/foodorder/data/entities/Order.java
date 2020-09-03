package com.github.velinyordanov.foodorder.data.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Orders")
public class Order extends BaseEntity {
    @Column(name = "Status", nullable = false)
    private Status status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "AddressId")
    private Address address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RestaurantId")
    private Restaurant restaurant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CustomerId")
    private Customer customer;

    @OneToMany(mappedBy = "order")
    private Set<OrderFood> foods;

    public Order() {
	super();
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

    @Override
    public String toString() {
	return "Order [getStatus()=" + getStatus()
		+ ", getRestaurant()="
		+ getRestaurant()
		+ ", getUser()="
		+ getCustomer()
		+ ", getFoods()="
		+ getFoods()
		+ "]";
    }
}
