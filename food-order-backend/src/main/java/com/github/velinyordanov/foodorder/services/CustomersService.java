package com.github.velinyordanov.foodorder.services;

import java.util.Collection;
import java.util.Optional;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.dto.UserDto;

public interface CustomersService {
    String registerCustomer(UserDto data);

    Optional<Customer> findById(String id);

    String login(UserDto userDto);

    AddressDto addAddressToCustomer(String customerId, AddressCreateDto address);

    AddressDto editAddress(String customerId, String addressId, AddressDto address);

    Collection<AddressDto> getAddressesForCustomer(String customerId);

    AddressDto deleteCustomerAddress(String customerId, String addressId);

    AddressDto getCustomerAddress(String customerId, String addressId);
}
