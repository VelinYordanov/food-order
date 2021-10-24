package com.github.velinyordanov.foodorder.dto;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.github.velinyordanov.foodorder.validation.annotations.CompareDates;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

@CompareDates(before = "validFrom", after = "validTo", message = VALID_FROM_AFTER_VALID_TO)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + discountPercentage;
		result = prime * result + (isOncePerUser ? 1231 : 1237);
		result = prime * result + (isSingleUse ? 1231 : 1237);
		result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
		result = prime * result + ((validTo == null) ? 0 : validTo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscountCodeCreateDto other = (DiscountCodeCreateDto) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (discountPercentage != other.discountPercentage)
			return false;
		if (isOncePerUser != other.isOncePerUser)
			return false;
		if (isSingleUse != other.isSingleUse)
			return false;
		if (validFrom == null) {
			if (other.validFrom != null)
				return false;
		} else if (!validFrom.equals(other.validFrom))
			return false;
		if (validTo == null) {
			if (other.validTo != null)
				return false;
		} else if (!validTo.equals(other.validTo))
			return false;
		return true;
	}
}
