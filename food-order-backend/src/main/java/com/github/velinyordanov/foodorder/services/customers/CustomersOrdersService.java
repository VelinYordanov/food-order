package com.github.velinyordanov.foodorder.services.customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;

public interface CustomersOrdersService {
	OrderDto addOrderToCustomer(String customerId, OrderCreateDto order);

	Page<OrderDto> getCustomerOrders(String customerId, Pageable pageable);

	OrderDto getCustomerOrder(String customerId, String orderId);
}
