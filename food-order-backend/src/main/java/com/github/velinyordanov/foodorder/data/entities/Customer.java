package com.github.velinyordanov.foodorder.data.entities;

import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "Customers")
public class Customer extends BaseUser {
    private static final long serialVersionUID = 4746831092067539667L;

    @OneToMany(
	    fetch = FetchType.LAZY,
	    mappedBy = "customer",
	    cascade = {
		    CascadeType.DETACH,
		    CascadeType.MERGE,
		    CascadeType.PERSIST,
		    CascadeType.REFRESH })
    private Set<Order> orders;

    @ManyToMany(
	    mappedBy = "customers",
	    fetch = FetchType.EAGER,
	    cascade = {
		    CascadeType.DETACH,
		    CascadeType.MERGE,
		    CascadeType.PERSIST,
		    CascadeType.REFRESH })
    private Set<Authority> authorities;

    public Customer() {
	super();
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
