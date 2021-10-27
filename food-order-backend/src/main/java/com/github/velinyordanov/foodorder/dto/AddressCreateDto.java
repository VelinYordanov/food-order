package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class AddressCreateDto {
	@NotNull(message = EMPTY_CITY)
	private City city;

	@NotNull(message = EMPTY_ADDRESS_TYPE)
	private AddressType addressType;

	@Size(min = MIN_LENGTH_NEIGHBORHOOD, max = MAX_LENGTH_NEIGHBORHOOD, message = NEIGHBORHOOD_OUT_OF_BOUNDS)
	private String neighborhood;

	@Size(min = MIN_LENGTH_STREET, max = MAX_LENGTH_STREET, message = STREET_OUT_OF_BOUNDS)
	private String street;

	@Size(min = MIN_LENGTH_STREET_NUMBER, max = MAX_LENGTH_STREET_NUMBER, message = STREET_NUMBER_OUT_OF_BOUNDS)
	private String streetNumber;

	@Size(min = MIN_LENGTH_APARTMENT_BUILDING_NUMBER, max = MAX_LENGTH_APARTMENT_BUILDING_NUMBER, message = APARTMENT_BUILDING_NUMBER_OUT_OF_BOUNDS)
	private String apartmentBuildingNumber;

	@Size(min = MIN_LENGTH_ENTRANCE, max = MAX_LENGTH_ENTRANCE, message = ENTRANCE_OUT_OF_BOUNDS)
	private String entrance;

	@Min(value = 0, message = FLOOR_NEGATIVE)
	private Byte floor;

	@Size(min = MIN_LENGTH_APARTMENT_NUMBER, max = MAX_LENGTH_APARTMENT_NUMBER, message = APARTMENT_NUMBER_OUT_OF_BOUNDS)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addressType == null) ? 0 : addressType.hashCode());
		result = prime * result + ((apartmentBuildingNumber == null) ? 0 : apartmentBuildingNumber.hashCode());
		result = prime * result + ((apartmentNumber == null) ? 0 : apartmentNumber.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((entrance == null) ? 0 : entrance.hashCode());
		result = prime * result + ((floor == null) ? 0 : floor.hashCode());
		result = prime * result + ((neighborhood == null) ? 0 : neighborhood.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((streetNumber == null) ? 0 : streetNumber.hashCode());
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
		AddressCreateDto other = (AddressCreateDto) obj;
		if (addressType != other.addressType)
			return false;
		if (apartmentBuildingNumber == null) {
			if (other.apartmentBuildingNumber != null)
				return false;
		} else if (!apartmentBuildingNumber.equals(other.apartmentBuildingNumber))
			return false;
		if (apartmentNumber == null) {
			if (other.apartmentNumber != null)
				return false;
		} else if (!apartmentNumber.equals(other.apartmentNumber))
			return false;
		if (city != other.city)
			return false;
		if (entrance == null) {
			if (other.entrance != null)
				return false;
		} else if (!entrance.equals(other.entrance))
			return false;
		if (floor == null) {
			if (other.floor != null)
				return false;
		} else if (!floor.equals(other.floor))
			return false;
		if (neighborhood == null) {
			if (other.neighborhood != null)
				return false;
		} else if (!neighborhood.equals(other.neighborhood))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		if (streetNumber == null) {
			if (other.streetNumber != null)
				return false;
		} else if (!streetNumber.equals(other.streetNumber))
			return false;
		return true;
	}
}
