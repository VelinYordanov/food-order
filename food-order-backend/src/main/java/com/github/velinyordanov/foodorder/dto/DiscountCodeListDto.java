package com.github.velinyordanov.foodorder.dto;

import java.time.LocalDate;

public class DiscountCodeListDto {
    private String code;

    private int discountPercentage;

    private LocalDate validFrom;

    private LocalDate validTo;

    private boolean isSingleUse;

    private boolean isOncePerUser;

    private int timesUsed;

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

    public int getTimesUsed() {
	return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
	this.timesUsed = timesUsed;
    }
}
