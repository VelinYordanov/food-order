package com.github.velinyordanov.foodorder.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.velinyordanov.foodorder.enums.AddressType;

@Entity
@Table(name = "Addresses")
public class Address extends BaseEntity {
    @Column(name = "AddressType")
    private AddressType addressType;

    @Column(name = "Neighborhood")
    private String neighborhood;

    @Column(name = "Street")
    private String street;

    @Column(name = "ApartmentBuildingNumber")
    private String apartmentBuildingNumber;

    @Column(name = "Entrance")
    private String entrance;

    @Column(name = "Floor")
    private Byte floor;

    @Column(name = "ApartmentNumber")
    private String apartmentNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CustomerId")
    private Customer customer;

    public Customer getCustomer() {
	return customer;
    }

    public void setCustomer(Customer customer) {
	this.customer = customer;
    }

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
