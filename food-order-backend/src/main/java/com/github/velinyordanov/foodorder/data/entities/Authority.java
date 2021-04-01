package com.github.velinyordanov.foodorder.data.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;

import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@Entity
@Table(name = "Authorities")
public class Authority extends BaseEntity implements GrantedAuthority {
	private static final long serialVersionUID = -3851383621543243995L;

	@NotBlank(message = ValidationConstraints.EMPTY_AUTHORITY)
	@Column(name = "Authority", unique = true, nullable = false)
	private String authority;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinTable(name = "Customers_Authorities", joinColumns = @JoinColumn(name = "AuthorityId"), inverseJoinColumns = @JoinColumn(name = "CustomerId"))
	private Set<Customer> customers;

	@ManyToMany()
	@JoinTable(name = "Restaurants_Authorities", joinColumns = @JoinColumn(name = "AuthorityId"), inverseJoinColumns = @JoinColumn(name = "RestaurantId"))
	private Set<Restaurant> restaurants;

	public Authority() {
		super();
		this.setCustomers(new HashSet<>());
		this.setRestaurants(new HashSet<>());
	}

	public Set<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(Set<Customer> customers) {
		this.customers = customers;
	}

	public Set<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(Set<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return this.authority;
	}
}
