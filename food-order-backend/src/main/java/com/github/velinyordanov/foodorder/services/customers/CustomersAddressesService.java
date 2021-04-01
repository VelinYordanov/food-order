package com.github.velinyordanov.foodorder.services.customers;

import java.util.Collection;

import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;

public interface CustomersAddressesService {
	AddressDto addAddressToCustomer(String customerId, AddressCreateDto address);

	AddressDto editAddress(String customerId, String addressId, AddressDto address);

	Collection<AddressDto> getAddressesForCustomer(String customerId);

	AddressDto deleteCustomerAddress(String customerId, String addressId);

	AddressDto getCustomerAddress(String customerId, String addressId);
}
