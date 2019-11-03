package com.github.velinyordanov.foodorder.entities;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    @ManyToMany(mappedBy = "foods")
    private Collection<Category> categories;

    @ManyToMany(mappedBy = "foods")
    private Collection<Order> orders;

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

    public Collection<Category> getCategories() {
	return categories;
    }

    public void setCategories(Collection<Category> categories) {
	this.categories = categories;
    }

    @Override
    public String toString() {
	return "Food [getPrice()=" + getPrice() + ", getName()=" + getName() + ", getDescription()=" + getDescription()
		+ ", getCategories()=" + getCategories() + "]";
    }
}
