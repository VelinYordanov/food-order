package com.github.velinyordanov.foodorder.services.customers;

import java.util.Optional;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.CustomerRegisterDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;

public interface CustomersAuthenticationService {
    String registerCustomer(CustomerRegisterDto data);

    JwtTokenDto login(UserLoginDto userDto);

    Optional<Customer> findById(String id);
}
