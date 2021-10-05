package com.github.velinyordanov.foodorder.data.entities;

import java.util.Collection;
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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

@Entity
@Table(name = "Restaurants")
public class Restaurant extends BaseUser {
	private static final long serialVersionUID = 7321303086507184708L;
	
	@NotBlank(message = EMPTY_NAME)
	@Pattern(regexp = NAME_PATTERN, message = NAME_DOES_NOT_MATCH_PATTERN)
	@Size(min = MIN_LENGTH_NAME, max = MAX_LENGTH_NAME, message = NAME_OUT_OF_BOUNDS)
	@Column(name = "Name", unique = true, nullable = false, columnDefinition = "nvarchar(100)")
	private String name;
	
	@Column(name = "Description", columnDefinition = "nvarchar(max)")
	private String description;

	@ManyToMany(mappedBy = "restaurants", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
	private Set<Authority> authorities;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant", cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
	private Set<Category> categories;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant", cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
	private Set<Order> orders;

	@OneToMany(mappedBy = "restaurant")
	private Set<DiscountCode> discountCodes;
	
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
		return categories.stream().filter(category -> !category.getIsDeleted()).collect(Collectors.toSet());
	}

	public Set<Category> getCategoriesWithDeleted() {
		return categories;
	}

	public void addCategory(Category category) {
		this.categories.add(category);
		category.setRestaurant(this);
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

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public Set<DiscountCode> getDiscountCodes() {
		return discountCodes;
	}

	public void setDiscountCodes(Set<DiscountCode> discountCodes) {
		this.discountCodes = discountCodes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String toString() {
		return "Restaurant [getName()=" + getName() + ", getDescription()=" + getDescription() +  "]";
	}
}
