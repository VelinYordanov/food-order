package com.github.velinyordanov.foodorder.data.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Categories", uniqueConstraints = @UniqueConstraint(columnNames = { "RestaurantId", "name" }))
public class Category extends BaseEntity {
    @Column(nullable = false)
    @Size(min = 3, max = 35, message = "Category name must be between 3 and 35 symbols.")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER,
	    cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(
	    name = "Categories_Foods",
	    joinColumns = @JoinColumn(name = "CategoryId"),
	    inverseJoinColumns = @JoinColumn(name = "FoodId"))
    private Set<Food> foods;

    @ManyToOne(optional = false,
	    cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "RestaurantId")
    private Restaurant restaurant;

    public Category() {
	super();
	this.setFoods(new HashSet<>());
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "Category [getName()=" + getName()
		+ ", getFoods()="
		+ getFoods()
		+ ", getRestaurant()="
		+ getRestaurant()
		+ "]";
    }

    public Collection<Food> getFoods() {
	return foods;
    }

    public void setFoods(Set<Food> foods) {
	this.foods = foods;
    }

    public Restaurant getRestaurant() {
	return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
	this.restaurant = restaurant;
    }
}
