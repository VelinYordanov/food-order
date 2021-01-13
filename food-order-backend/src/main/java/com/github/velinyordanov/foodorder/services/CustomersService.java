package com.github.velinyordanov.foodorder.services;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.dto.CustomerRegisterDto;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;

public interface CustomersService {
    String registerCustomer(CustomerRegisterDto data);

    Optional<Customer> findById(String id);

    String login(UserLoginDto userDto);

    AddressDto addAddressToCustomer(String customerId, AddressCreateDto address);

    AddressDto editAddress(String customerId, String addressId, AddressDto address);

    Collection<AddressDto> getAddressesForCustomer(String customerId);

    AddressDto deleteCustomerAddress(String customerId, String addressId);

    AddressDto getCustomerAddress(String customerId, String addressId);

    OrderDto addOrderToCustomer(String customerId, OrderCreateDto order);

    Page<OrderDto> getCustomerOrders(String customerId, Pageable pageable);

    OrderDto getCustomerOrder(String customerId, String orderId);
}
