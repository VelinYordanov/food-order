package com.github.velinyordanov.foodorder.services.customers.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;
import com.github.velinyordanov.foodorder.services.customers.CustomersAddressesService;

@Service
public class CustomersAddressesServiceImpl implements CustomersAddressesService {
    private final Mapper mapper;
    private final FoodOrderData foodOrderData;

    public CustomersAddressesServiceImpl(
	    Mapper mapper,
	    FoodOrderData foodOrderData,
	    SimpMessagingTemplate messagingTemplate,
	    DiscountCodesService discountCodesService) {
	this.mapper = mapper;
	this.foodOrderData = foodOrderData;
    }

    @Override
    public AddressDto addAddressToCustomer(String customerId, AddressCreateDto address) {
	Optional<Customer> customerOptional = this.foodOrderData.customers().findById(customerId);
	if (customerOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Customer with id {0} not found", customerId));
	}

	Address addressToAdd = this.mapper.map(address, Address.class);
	Customer customer = customerOptional.get();
	customer.addAddress(addressToAdd);
	return this.mapper.map(this.foodOrderData.addresses().save(addressToAdd), AddressDto.class);
    }

    @Override
    public AddressDto editAddress(String customerId, String addressId, AddressDto address) {
	Optional<Address> addressOptional = this.foodOrderData.addresses().findById(addressId);

	if (addressOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Address with id {0} not found", addressId));
	}

	Address addressToUpdate = addressOptional.get();
	if (!customerId.equals(addressToUpdate.getCustomer().getId())) {
	    throw new NotFoundException(
		    MessageFormat.format("No such address found for customer with id {0}", customerId));
	}

	this.mapper.map(address, addressToUpdate);
	addressToUpdate.setId(addressId);

	return this.mapper.map(this.foodOrderData.addresses().save(addressToUpdate), AddressDto.class);
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
	Optional<Address> addressOptional = this.foodOrderData.addresses().findById(addressId);
	if (addressOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("No address with id {0} found", addressId));
	}

	Address addressToDelete = addressOptional.get();
	if (!customerId.equals(addressToDelete.getCustomer().getId())) {
	    throw new NotFoundException(
		    MessageFormat.format("No such address found for customer with id {0}", customerId));
	}

	addressToDelete.setIsDeleted(true);
	return this.mapper.map(this.foodOrderData.addresses().save(addressToDelete), AddressDto.class);
    }

    @Override
    public AddressDto getCustomerAddress(String customerId, String addressId) {
	Optional<Address> addressOptional = this.foodOrderData.addresses().findById(addressId);
	if (addressOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("No address with id {0} found", addressId));
	}

	Address address = addressOptional.get();
	if (!customerId.equals(address.getCustomer().getId())) {
	    throw new NotFoundException(
		    MessageFormat.format("No such address found for customer with id {0}", customerId));
	}

	return this.mapper.map(address, AddressDto.class);
    }
}
