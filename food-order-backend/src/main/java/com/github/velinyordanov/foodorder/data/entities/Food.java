package com.github.velinyordanov.foodorder.data.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Foods")
public class Food extends BaseEntity {
    @Column(nullable = false, columnDefinition = "nvarchar(100)")
    private String name;

    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToMany(mappedBy = "foods",
	    fetch = FetchType.EAGER,
	    cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private Set<Category> categories;

    @OneToMany(mappedBy = "food")
    private Set<OrderFood> orders;

    public Food() {
	super();
	this.setCategories(new HashSet<>());
	this.setOrders(new HashSet<>());
    }

    public Set<OrderFood> getOrders() {
	return orders;
    }

    public void setOrders(Set<OrderFood> orders) {
	this.orders = orders;
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
	return this.categories
		.stream()
		.filter(category -> !category.getIsDeleted())
		.collect(Collectors.toSet());
    }

    public Set<Category> getCategoriesWithDeleted() {
	return categories;
    }

    public void addCategory(Category category) {
	if (!this.categories.contains(category)) {
	    this.categories.add(category);
	    category.addFood(this);
	}
    }

    public void removeCategory(Category category) {
	if (this.categories.contains(category)) {
	    this.categories.remove(category);
	    category.removeFood(this);
	}
    }

    public void setCategories(Set<Category> categories) {
	this.categories = categories;
    }

    @Override
    public String toString() {
	return "Food [getPrice()=" + getPrice()
		+ ", getName()="
		+ getName()
		+ ", getDescription()="
		+ getDescription()
		+ "]";
    }
}
