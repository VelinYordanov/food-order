package com.github.velinyordanov.foodorder.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class User extends BaseEntity {
    private String username;

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
