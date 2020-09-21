package com.github.velinyordanov.foodorder.dto;

public class DiscountCodeDto {
    private String id;
    private String code;
    private int discountPercentage;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

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
}
