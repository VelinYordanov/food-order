package com.github.velinyordanov.foodorder.controllers.customers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.services.customers.CustomersOrdersService;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@RestController
@RequestMapping("customers/{customerId}/orders")
@PreAuthorize(ValidationConstraints.ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION)
public class CustomersOrdersController {
	private static final int DEFAULT_PAGE_SIZE = 15;

	private final CustomersOrdersService customersOrdersService;

	public CustomersOrdersController(CustomersOrdersService customersOrdersService) {
		this.customersOrdersService = customersOrdersService;
	}

	@PostMapping()
	public OrderDto addOrderToCustomer(@PathVariable String customerId, @RequestBody @Valid OrderCreateDto order) {
		return this.customersOrdersService.addOrderToCustomer(customerId, order);
	}

	@GetMapping()
	public Page<OrderDto> getCustomerOrders(@PathVariable String customerId,
			@RequestParam("page") Optional<Integer> pageOptional) {
		int page = pageOptional.filter(selectedPage -> selectedPage >= 0).orElse(0);

		Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("createdOn").descending());

		return this.customersOrdersService.getCustomerOrders(customerId, pageable);
	}

	@GetMapping("{orderId}")
	public OrderDto getCustomerOrder(@PathVariable String customerId, @PathVariable String orderId) {
		return this.customersOrdersService.getCustomerOrder(customerId, orderId);
	}
}
