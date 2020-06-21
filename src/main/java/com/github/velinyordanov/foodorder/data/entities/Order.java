package com.github.velinyordanov.foodorder.data.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Orders")
public class Order extends BaseEntity {
    @Column(name = "Status", nullable = false)
    private Status status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RestaurantId")
    private Restaurant restaurant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CustomerId")
    private Customer customer;

    @ManyToMany()
    @JoinTable(
	    name = "Orders_Foods",
	    joinColumns = @JoinColumn(name = "order_id"),
	    inverseJoinColumns = @JoinColumn(name = "food_id"))
    private Set<Food> foods;

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

    public Set<Food> getFoods() {
	return foods;
    }

    public void setFoods(Set<Food> foods) {
	this.foods = foods;
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
