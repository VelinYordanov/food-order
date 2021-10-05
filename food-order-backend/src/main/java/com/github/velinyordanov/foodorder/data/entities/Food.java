package com.github.velinyordanov.foodorder.data.entities;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_CATEGORIES;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_DESCRIPTION;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_NAME;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_PRICE;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.ZERO_OR_NEGATIVE_FOOD_PRICE;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "Foods")
public class Food extends BaseEntity {
	@NotBlank(message = EMPTY_FOOD_NAME)
	@Column(nullable = false, columnDefinition = "nvarchar(100)")
	private String name;

	@NotBlank(message = EMPTY_FOOD_DESCRIPTION)
	@Column(nullable = false, columnDefinition = "nvarchar(max)")
	private String description;

	@NotNull(message = EMPTY_FOOD_PRICE)
	@Positive(message = ZERO_OR_NEGATIVE_FOOD_PRICE)
	@Column(nullable = false)
	private BigDecimal price;

	@NotEmpty(message = EMPTY_FOOD_CATEGORIES)
	@ManyToMany(mappedBy = "foods", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
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
		return this.categories.stream().filter(category -> !category.getIsDeleted()).collect(Collectors.toSet());
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
		return "Food [getPrice()=" + getPrice() + ", getName()=" + getName() + ", getDescription()=" + getDescription()
				+ "]";
	}
}
