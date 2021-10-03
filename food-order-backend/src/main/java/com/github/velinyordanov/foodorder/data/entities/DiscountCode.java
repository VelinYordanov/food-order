package com.github.velinyordanov.foodorder.data.entities;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "DiscountCodes", uniqueConstraints = { @UniqueConstraint(columnNames = { "Code", "RestaurantId" }) })
public class DiscountCode extends BaseEntity {
	@NotBlank(message = EMPTY_DISCOUNT_CODE)
	@Size(min = MIN_LENGTH_DISCOUNT_CODE, max = MAX_LENGTH_DISCOUNT_CODE, message = DISCOUNT_CODE_OUT_OF_BOUNDS)
	@Column(name = "Code", nullable = false)
	private String code;

	@OneToMany(mappedBy = "discountCode")
	private Set<Order> orders;

	@ManyToOne(optional = false)
	@JoinColumn(name = "RestaurantId")
	private Restaurant restaurant;

	@Range(min = MIN_DISCOUNT_PERCENTAGE, max = MAX_DISCOUNT_PERCENTAGE, message = DISCOUNT_CODE_OUT_OF_BOUNDS)
	@Column(name = "DiscountPercentage", nullable = false)
	private int discountPercentage;

	@NotNull(message = EMPTY_VALID_FROM)
	@Column(name = "ValidFrom", columnDefinition = "DATE", nullable = false)
	private LocalDate validFrom;

	@NotNull(message = EMPTY_VALID_TO)
	@Column(name = "ValidTo", columnDefinition = "DATE", nullable = false)
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
