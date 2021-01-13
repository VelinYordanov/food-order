package com.github.velinyordanov.foodorder.data.entities;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
	name = "DiscountCodes",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = { "Code", "RestaurantId" })
	})
public class DiscountCode extends BaseEntity {
    @Column(name = "Code", nullable = false)
    private String code;

    @OneToMany(mappedBy = "discountCode")
    private Set<Order> orders;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RestaurantId")
    private Restaurant restaurant;

    @Column(name = "DiscountPercentage", nullable = false)
    private int discountPercentage;

    @Column(name = "ValidFrom", columnDefinition = "DATE")
    private LocalDate validFrom;

    @Column(name = "ValidTo", columnDefinition = "DATE")
    private LocalDate validTo;

    @Column(name = "IsSingleUse")
    private boolean isSingleUse;

    @Column(name = "IsOncePerUser")
    private boolean isOncePerUser;

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public Set<Order> getOrders() {
	return orders;
    }

    public void setOrders(Set<Order> orders) {
	this.orders = orders;
    }

    public Restaurant getRestaurant() {
	return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
	this.restaurant = restaurant;
    }

    public int getDiscountPercentage() {
	return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
	this.discountPercentage = discountPercentage;
    }

    public LocalDate getValidFrom() {
	return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
	this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
	return validTo;
    }

    public void setValidTo(LocalDate validTo) {
	this.validTo = validTo;
    }

    public boolean getIsSingleUse() {
	return isSingleUse;
    }

    public void setIsSingleUse(boolean isSingleUse) {
	this.isSingleUse = isSingleUse;
    }

    public boolean getIsOncePerUser() {
	return isOncePerUser;
    }

    public void setIsOncePerUser(boolean isOncePerUser) {
	this.isOncePerUser = isOncePerUser;
    }
}
