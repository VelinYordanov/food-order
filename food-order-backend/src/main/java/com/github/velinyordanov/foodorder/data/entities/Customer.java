package com.github.velinyordanov.foodorder.data.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@Entity
@Table(name = "Customers")
public class Customer extends BaseUser {
	private static final long serialVersionUID = 4746831092067539667L;
	
	@NotBlank(message = ValidationConstraints.EMPTY_NAME)
	@Pattern(regexp = ValidationConstraints.NAME_PATTERN, message = ValidationConstraints.NAME_DOES_NOT_MATCH_PATTERN)
	@Size(min = ValidationConstraints.MIN_LENGTH_NAME, max = ValidationConstraints.MAX_LENGTH_NAME, message = ValidationConstraints.NAME_OUT_OF_BOUNDS)
	@Column(name = "Name", unique = false, nullable = false, columnDefinition = "nvarchar(100)")
	private String name;

	@NotBlank(message = ValidationConstraints.EMPTY_NAME)
	@Column(name = "PhoneNumber", nullable = false, unique = true)
	private String phoneNumber;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
	private Set<Order> orders;

	@ManyToMany(mappedBy = "customers", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH })
	private Set<Authority> authorities;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
	private Set<Address> addresses;

	public Customer() {
		super();
		this.setAuthorities(new HashSet<>());
		this.setOrders(new HashSet<>());
		this.setAddresses(new HashSet<>());
	}
	
	public void addAuthority(Authority authority) {
		this.authorities.add(authority);
		authority.getCustomers().add(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addAddress(Address address) {
		this.getAddresses().add(address);
		address.setCustomer(this);
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
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
}
