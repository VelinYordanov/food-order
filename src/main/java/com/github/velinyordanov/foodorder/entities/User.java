package com.github.velinyordanov.foodorder.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Users")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore()
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Collection<Order> orders;

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public Collection<Order> getOrders() {
	return orders;
    }

    public void setOrders(Collection<Order> orders) {
	this.orders = orders;
    }
}
