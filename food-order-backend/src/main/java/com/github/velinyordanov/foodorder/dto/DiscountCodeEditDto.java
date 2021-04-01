package com.github.velinyordanov.foodorder.dto;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

public class DiscountCodeEditDto {
	@Range(min = 1, max = 100, message = "Discount percentage must be between 1 and 100")
	private int discountPercentage;

	@NotNull(message = "Valid from is required")
	private LocalDate validFrom;

	@NotNull(message = "Valid to is required")
	@FutureOrPresent(message = "Valid to date must be in the present or future")
	private LocalDate validTo;

	private boolean isSingleUse;

	private boolean isOncePerUser;

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
