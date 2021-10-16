package com.github.velinyordanov.foodorder.controllers.customers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.services.customers.CustomersAddressesService;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@RestController
@RequestMapping("customers/{customerId}/addresses")
@PreAuthorize(ValidationConstraints.ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION)
public class CustomersAddressesController {
	private final CustomersAddressesService customersAddressesService;

	public CustomersAddressesController(CustomersAddressesService customersAddressesService) {
		this.customersAddressesService = customersAddressesService;
	}

	@PostMapping()
	public AddressDto addAddressToCustomer(@PathVariable String customerId, @RequestBody @Valid AddressCreateDto address) {
		return this.customersAddressesService.addAddressToCustomer(customerId, address);
	}

	@PutMapping("{addressId}")
	public AddressDto updateAddress(@PathVariable String customerId, @PathVariable String addressId,
			@RequestBody @Valid AddressCreateDto address) {
		return this.customersAddressesService.editAddress(customerId, addressId, address);
	}

	@GetMapping()
	public Collection<AddressDto> getAddressesForCustomer(@PathVariable String customerId) {
		return this.customersAddressesService.getAddressesForCustomer(customerId);
	}

	@DeleteMapping("{addressId}")
	public AddressDto deleteCustomerAddress(@PathVariable String customerId, @PathVariable String addressId) {
		return this.customersAddressesService.deleteCustomerAddress(customerId, addressId);
	}

	@GetMapping("{addressId}")
	public AddressDto getCustomerAddress(@PathVariable String customerId, @PathVariable String addressId) {
		return this.customersAddressesService.getCustomerAddress(customerId, addressId);
	}
}
