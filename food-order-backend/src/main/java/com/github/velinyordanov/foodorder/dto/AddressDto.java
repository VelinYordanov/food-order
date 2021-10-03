package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

public class AddressDto {
	private String id;

	@NotNull(message = ValidationConstraints.EMPTY_CITY)
	private City city;

	@NotNull(message = ValidationConstraints.EMPTY_ADDRESS_TYPE)
	private AddressType addressType;

	@Size(min = ValidationConstraints.MIN_LENGTH_NEIGHBORHOOD, max = ValidationConstraints.MAX_LENGTH_NEIGHBORHOOD, message = ValidationConstraints.NEIGHBORHOOD_OUT_OF_BOUNDS)
	private String neighborhood;

	@Size(min = ValidationConstraints.MIN_LENGTH_STREET, max = ValidationConstraints.MAX_LENGTH_STREET, message = ValidationConstraints.STREET_OUT_OF_BOUNDS)
	private String street;

	@Size(min = ValidationConstraints.MIN_LENGTH_STREET_NUMBER, max = ValidationConstraints.MAX_LENGTH_STREET_NUMBER, message = ValidationConstraints.STREET_NUMBER_OUT_OF_BOUNDS)
	private String streetNumber;

	@Size(min = ValidationConstraints.MIN_LENGTH_APARTMENT_BUILDING_NUMBER, max = ValidationConstraints.MAX_LENGTH_APARTMENT_BUILDING_NUMBER, message = ValidationConstraints.APARTMENT_BUILDING_NUMBER_OUT_OF_BOUNDS)
	private String apartmentBuildingNumber;

	@Size(min = ValidationConstraints.MIN_LENGTH_ENTRANCE, max = ValidationConstraints.MAX_LENGTH_ENTRANCE, message = ValidationConstraints.ENTRANCE_OUT_OF_BOUNDS)
	private String entrance;

	@Min(value = 0, message = ValidationConstraints.FLOOR_NEGATIVE)
	private Byte floor;

	@Size(min = ValidationConstraints.MIN_LENGTH_APARTMENT_NUMBER, max = ValidationConstraints.MAX_LENGTH_APARTMENT_NUMBER, message = ValidationConstraints.APARTMENT_NUMBER_OUT_OF_BOUNDS)
	private String apartmentNumber;

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
