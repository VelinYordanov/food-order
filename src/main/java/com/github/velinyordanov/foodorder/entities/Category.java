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
@Table(name = "Categories")
public class Category extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @ManyToMany()
    @JoinTable(name = "Category_Food", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "food_id"))
    private Collection<Food> foods;

    @ManyToOne
    private Restaurant restaurant;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "Category [getName()=" + getName() + ", getFoods()=" + getFoods() + ", getRestaurant()="
		+ getRestaurant() + "]";
    }

    public Collection<Food> getFoods() {
	return foods;
    }

    public void setFoods(Collection<Food> foods) {
	this.foods = foods;
    }

    public Restaurant getRestaurant() {
	return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
	this.restaurant = restaurant;
    }
}
