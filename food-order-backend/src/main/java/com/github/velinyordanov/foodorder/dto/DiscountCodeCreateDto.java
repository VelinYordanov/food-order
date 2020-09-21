package com.github.velinyordanov.foodorder.dto;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

public class DiscountCodeCreateDto {
    @NotBlank
    @Size(min = 5, max = 12, message = "Discount code must be between 5 and 12 symbols long.")
    private String code;

    @Range(min = 1, max = 100, message = "Discount percentage must be between 1 and 100")
    private int discountPercentage;

    @NotNull
    @FutureOrPresent(message = "Valid from date must be present or future")
    private LocalDate validFrom;

    @NotNull
    @FutureOrPresent(message = "Valid to date must be in the present or future")
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
