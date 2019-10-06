package com.github.velinyordanov.foodorder.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Restaurants")
public class Restaurant extends BaseEntity {
    private String name;

    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    private Collection<Category> categories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    private Collection<Order> orders;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Collection<Category> getCategories() {
	return categories;
    }

    public void setCategories(Collection<Category> categories) {
	this.categories = categories;
    }

    public Collection<Order> getOrders() {
	return orders;
    }

    public void setOrders(Collection<Order> orders) {
	this.orders = orders;
    }

    @Override
    public String toString() {
	return "Restaurant [getName()=" + getName() + ", getDescription()=" + getDescription() + ", getCategories()="
		+ getCategories() + ", getOrders()=" + getOrders() + "]";
    }
}
