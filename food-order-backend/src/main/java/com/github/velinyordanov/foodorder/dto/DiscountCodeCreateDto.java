package com.github.velinyordanov.foodorder.dto;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.github.velinyordanov.foodorder.validation.annotations.CompareDates;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

@CompareDates(before = "validFrom", after = "validTo")
public class DiscountCodeCreateDto {
	@NotBlank(message = EMPTY_DISCOUNT_CODE)
	@Size(min = MIN_LENGTH_DISCOUNT_CODE, max = MAX_LENGTH_DISCOUNT_CODE, message = DISCOUNT_CODE_OUT_OF_BOUNDS)
	private String code;

	@Range(min = MIN_DISCOUNT_PERCENTAGE, max = MAX_DISCOUNT_PERCENTAGE, message = DISCOUNT_PERCENTAGE_OUT_OF_BOUNDS)
	private int discountPercentage;

	@NotNull(message = EMPTY_VALID_FROM)
	@FutureOrPresent(message = PAST_VALID_FROM)
	private LocalDate validFrom;

	@NotNull(message = EMPTY_VALID_TO)
	@FutureOrPresent(message = PAST_VALID_TO)
	private LocalDate validTo;

	private boolean isSingleUse;

	private boolean isOncePerUser;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
