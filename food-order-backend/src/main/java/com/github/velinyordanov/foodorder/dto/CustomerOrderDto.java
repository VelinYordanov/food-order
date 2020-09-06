package com.github.velinyordanov.foodorder.dto;

public class CustomerOrderDto {
    private String id;
    private String phoneNumber;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }
}
