package com.github.velinyordanov.foodorder.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

@Entity
@Table(name = "Addresses")
public class Address extends BaseEntity {
	@NotNull(message = EMPTY_CITY)
	@Column(name = "City", nullable = false)
	private City city;

	@NotNull(message = EMPTY_ADDRESS_TYPE)
	@Column(name = "AddressType", nullable = false)
	private AddressType addressType;

	@Size(min = MIN_LENGTH_NEIGHBORHOOD, max = MAX_LENGTH_NEIGHBORHOOD, message = NEIGHBORHOOD_OUT_OF_BOUNDS)
	@Column(name = "Neighborhood", columnDefinition = "nvarchar(100)")
	private String neighborhood;

	@Size(min = MIN_LENGTH_STREET, max = MAX_LENGTH_STREET, message = STREET_OUT_OF_BOUNDS)
	@Column(name = "Street", columnDefinition = "nvarchar(100)")
	private String street;

	@Size(min = MIN_LENGTH_STREET_NUMBER, max = MAX_LENGTH_STREET_NUMBER, message = STREET_NUMBER_OUT_OF_BOUNDS)
	@Column(name = "StreetNumber", columnDefinition = "nvarchar(10)")
	private String streetNumber;

	@Size(min = MIN_LENGTH_APARTMENT_BUILDING_NUMBER, max = MAX_LENGTH_APARTMENT_BUILDING_NUMBER, message = APARTMENT_BUILDING_NUMBER_OUT_OF_BOUNDS)
	@Column(name = "ApartmentBuildingNumber", columnDefinition = "nvarchar(10)")
	private String apartmentBuildingNumber;

	@Size(min = MIN_LENGTH_ENTRANCE, max = MAX_LENGTH_ENTRANCE, message = ENTRANCE_OUT_OF_BOUNDS)
	@Column(name = "Entrance", columnDefinition = "nvarchar(10)")
	private String entrance;

	@Min(value = 0, message = FLOOR_NEGATIVE)
	@Column(name = "Floor")
	private Byte floor;

	@Size(min = MIN_LENGTH_APARTMENT_NUMBER, max = MAX_LENGTH_APARTMENT_NUMBER, message = APARTMENT_NUMBER_OUT_OF_BOUNDS)
	@Column(name = "ApartmentNumber", columnDefinition = "nvarchar(10)")
	private String apartmentNumber;

	@NotNull(message = EMPTY_ORDER_CUSTOMER)
	@ManyToOne(optional = false)
	@JoinColumn(name = "CustomerId")
	private Customer customer;

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
