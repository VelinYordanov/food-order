package com.github.velinyordanov.foodorder.data.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Foods")
public class Food extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToMany(mappedBy = "foods",
	    fetch = FetchType.EAGER,
	    cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private Set<Category> categories;

    @ManyToMany(mappedBy = "foods")
    private Set<Order> orders;

    public Food() {
	super();
	this.setCategories(new HashSet<>());
	this.setOrders(new HashSet<>());
    }

    public BigDecimal getPrice() {
	return price;
    }

    public void setPrice(BigDecimal price) {
	this.price = price;
    }

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

    public Set<Category> getCategories() {
	return categories;
    }

    public void setCategories(Set<Category> categories) {
	this.categories = categories;
    }

    public Set<Order> getOrders() {
	return orders;
    }

    public void setOrders(Set<Order> orders) {
	this.orders = orders;
    }

    @Override
    public String toString() {
	return "Food [getPrice()=" + getPrice()
		+ ", getName()="
		+ getName()
		+ ", getDescription()="
		+ getDescription()
		+ ", getCategories()="
		+ getCategories()
		+ "]";
    }
}
