package com.github.velinyordanov.foodorder.services.customers.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;
import com.github.velinyordanov.foodorder.services.customers.CustomersAddressesService;

@Service
@Transactional
public class CustomersAddressesServiceImpl implements CustomersAddressesService {
	private final Mapper mapper;
	private final FoodOrderData foodOrderData;

	public CustomersAddressesServiceImpl(Mapper mapper, FoodOrderData foodOrderData,
			SimpMessagingTemplate messagingTemplate, DiscountCodesService discountCodesService) {
		this.mapper = mapper;
		this.foodOrderData = foodOrderData;
	}

	@Override
	public AddressDto addAddressToCustomer(String customerId, AddressCreateDto address) {
		return this.foodOrderData.customers()
				.findById(customerId)
				.map(customer -> {
					Address addressToAdd = this.mapper.map(address, Address.class);
					customer.addAddress(addressToAdd);
					return this.mapper.map(this.foodOrderData.addresses().save(addressToAdd), AddressDto.class);
				})
				.orElseThrow(() -> new NotFoundException(
						MessageFormat.format("Customer with id {0} not found", customerId)));
	}

	@Override
	public AddressDto editAddress(String customerId, String addressId, AddressCreateDto address) {
		return this.foodOrderData.addresses()
				.findByIdAndCustomerId(addressId, customerId)
				.map(foundAddress -> {
					this.mapper.map(address, foundAddress);
					foundAddress.setId(addressId);

					return this.mapper.map(this.foodOrderData.addresses().save(foundAddress), AddressDto.class);
				})
				.orElseThrow(
						() -> new NotFoundException(MessageFormat.format("Address with id {0} not found", addressId)));
	}

	@Override
	public Collection<AddressDto> getAddressesForCustomer(String customerId) {
		if (!this.foodOrderData.customers().existsById(customerId)) {
			throw new NotFoundException(MessageFormat.format("Customer with id {0} not found.", customerId));
		}

		return this.foodOrderData.addresses()
				.findByCustomerId(customerId)
				.stream()
				.map(address -> this.mapper.map(address, AddressDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public AddressDto deleteCustomerAddress(String customerId, String addressId) {
		return this.foodOrderData.addresses()
				.findByIdAndCustomerId(addressId, customerId)
				.map(address -> {
					address.setIsDeleted(true);
					return this.mapper.map(this.foodOrderData.addresses().save(address), AddressDto.class);
				})
				.orElseThrow(
						() -> new NotFoundException(MessageFormat.format("No address with id {0} found", addressId)));
	}

	@Override
	public AddressDto getCustomerAddress(String customerId, String addressId) {
		return this.foodOrderData.addresses()
				.findByIdAndCustomerId(addressId, customerId)
				.map(address -> this.mapper.map(address, AddressDto.class))
				.orElseThrow(
						() -> new NotFoundException(MessageFormat.format("No address with id {0} found", addressId)));
	}
}
