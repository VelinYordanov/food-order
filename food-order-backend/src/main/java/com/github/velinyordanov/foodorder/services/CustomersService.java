package com.github.velinyordanov.foodorder.services;

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
}
