package com.github.velinyordanov.foodorder.dto;

import com.github.velinyordanov.foodorder.enums.AddressType;

public class AddressCreateDto {
    private AddressType addressType;

    private String neighborhood;

    private String street;

    private String apartmentBuildingNumber;

    private String entrance;

    private Byte floor;

    private String apartmentNumber;

    public AddressType getAddressType() {
	return addressType;
    }

    public void setAddressType(AddressType addressType) {
	this.addressType = addressType;
    }

    public String getNeighborhood() {
	return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
	this.neighborhood = neighborhood;
    }

    public String getStreet() {
	return street;
    }

    public void setStreet(String street) {
	this.street = street;
    }

    public String getApartmentBuildingNumber() {
	return apartmentBuildingNumber;
    }

    public void setApartmentBuildingNumber(String apartmentBuildingNumber) {
	this.apartmentBuildingNumber = apartmentBuildingNumber;
    }

    public String getEntrance() {
	return entrance;
    }

    public void setEntrance(String entrance) {
	this.entrance = entrance;
    }

    public Byte getFloor() {
	return floor;
    }

    public void setFloor(Byte floor) {
	this.floor = floor;
    }

    public String getApartmentNumber() {
	return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
	this.apartmentNumber = apartmentNumber;
    }
}
