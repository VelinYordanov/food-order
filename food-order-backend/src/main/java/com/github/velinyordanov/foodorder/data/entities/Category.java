package com.github.velinyordanov.foodorder.data.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

@Entity
@Table(name = "Categories", uniqueConstraints = @UniqueConstraint(columnNames = { "RestaurantId", "name" }))
public class Category extends BaseEntity {
	@NotBlank(message = EMPTY_CATEGORY_NAME)
	@Size(min = MIN_LENGTH_CATEGORY_NAME, max = MAX_LENGTH_CATEGORY_NAME, message = CATEGORY_NAME_OUT_OF_BOUNDS)
	@Column(nullable = false, columnDefinition = "nvarchar(100)")
	private String name;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "Categories_Foods", joinColumns = @JoinColumn(name = "CategoryId"), inverseJoinColumns = @JoinColumn(name = "FoodId"))
	private Set<Food> foods;

	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
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
		return "Category [getName()=" + getName() + ", getFoods()=" + getFoods() + ", getRestaurant()="
				+ getRestaurant() + "]";
	}

	public Collection<Food> getFoods() {
		return foods.stream().filter(food -> !food.getIsDeleted()).collect(Collectors.toList());
	}

	public Collection<Food> getFoodsWithDeleted() {
		return foods;
	}

	public void addFood(Food food) {
		if (!this.foods.contains(food)) {
			this.foods.add(food);
			food.addCategory(this);
		}
	}

	public void removeFood(Food food) {
		if (this.foods.contains(food)) {
			this.foods.remove(food);
			food.removeCategory(this);
		}
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
