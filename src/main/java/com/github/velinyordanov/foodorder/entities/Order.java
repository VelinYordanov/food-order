package com.github.velinyordanov.foodorder.entities;

import java.util.Collection;

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
    @Column(nullable = false)
    private Status status;

    @ManyToOne(optional = false)
    private Restaurant restaurant;

    @ManyToOne(optional = false)
    private User user;

    @ManyToMany()
    @JoinTable(name = "Order_Food", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "food_id"))
    private Collection<Food> foods;

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

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public Collection<Food> getFoods() {
	return foods;
    }

    public void setFoods(Collection<Food> foods) {
	this.foods = foods;
    }

    @Override
    public String toString() {
	return "Order [getStatus()=" + getStatus() + ", getRestaurant()=" + getRestaurant() + ", getUser()=" + getUser()
		+ ", getFoods()=" + getFoods() + "]";
    }
}
