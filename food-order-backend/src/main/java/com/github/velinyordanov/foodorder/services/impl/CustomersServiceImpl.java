package com.github.velinyordanov.foodorder.services.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderListDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.CustomersService;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;
import com.github.velinyordanov.foodorder.services.JwtTokenService;

@Service
public class CustomersServiceImpl implements CustomersService {
    private final Mapper mapper;
    private final FoodOrderData foodOrderData;
    private final SimpMessagingTemplate messagingTemplate;
    private final DiscountCodesService discountCodesService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder encoder;

    public CustomersServiceImpl(
	    Mapper mapper,
	    PasswordEncoder encoder,
	    SimpMessagingTemplate messagingTemplate,
	    FoodOrderData foodOrderData,
	    DiscountCodesService discountCodesService,
	    JwtTokenService jwtTokenService,
	    AuthenticationManager authenticationManager) {
	this.mapper = mapper;
	this.jwtTokenService = jwtTokenService;
	this.messagingTemplate = messagingTemplate;
	this.foodOrderData = foodOrderData;
	this.discountCodesService = discountCodesService;
	this.authenticationManager = authenticationManager;
	this.encoder = encoder;
    }

    @Override
    public Optional<Customer> findById(String id) {
	return this.foodOrderData.customers().findById(id);
    }

    @Override
    @Transactional
    public String registerCustomer(UserDto user) {
	if (this.foodOrderData.customers().existsByUsername(user.getUsername())) {
	    throw new DuplicateUserException("Customer with this username already exists");
	}

	Customer customer = this.mapper.map(user, Customer.class);
	customer.setPassword(this.encoder.encode(user.getPassword()));
	Optional<Authority> authorityOptional = this.foodOrderData.authorities().findFirstByAuthority("ROLE_CUSTOMER");
	Authority authority = null;
	if (authorityOptional.isPresent()) {
	    authority = authorityOptional.get();
	} else {
	    authority = new Authority();
	    authority.setAuthority("ROLE_CUSTOMER");
	}

	Set<Authority> authorities = new HashSet<>();
	authorities.add(authority);
	authority.getCustomers().add(customer);
	customer.setAuthorities(authorities);

	Customer savedRestaurant = this.foodOrderData.customers().save(customer);
	return this.jwtTokenService.generateToken(savedRestaurant);
    }

    @Override
    public String login(UserDto user) {
	UsernamePasswordAuthenticationToken token =
		new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
	token.setDetails(UserType.Customer);

	Authentication authentication = this.authenticationManager.authenticate(token);
	return this.jwtTokenService.generateToken((Customer) authentication.getPrincipal());
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

    @Override
    public OrderDto addOrderToCustomer(String customerId, OrderCreateDto order) {
	Address address = this.foodOrderData
		.addresses()
		.findById(order.getAddressId())
		.orElseThrow(() -> new NotFoundException("No such address found"));

	if (!order.getCustomerId().equals(address.getCustomer().getId())) {
	    throw new NotFoundException("No such address found");
	}

	this.foodOrderData
		.restaurants()
		.findById(order.getRestaurantId())
		.orElseThrow(() -> new NotFoundException("No such restaurant found"));

	Collection<Food> restaurantFoods = this.foodOrderData.foods()
		.findByRestaurantId(order.getRestaurantId());

	if (!order.getFoods()
		.stream()
		.allMatch(food -> restaurantFoods.stream().anyMatch(f -> f.getId().equals(food.getId())))) {
	    throw new NotFoundException("No such food found");
	}

	Order orderToAdd = this.mapper.map(order, Order.class);

	if (order.getDiscountCodeId() != null) {
	    DiscountCode discountCode = this.foodOrderData.discountCodes()
		    .findById(order.getDiscountCodeId())
		    .orElseThrow(() -> new NotFoundException("Discount code not found"));

	    this.discountCodesService.validateDiscountCode(discountCode, customerId);

	    orderToAdd.setDiscountCode(discountCode);
	}

	OrderDto result = this.mapper.map(this.foodOrderData.orders().save(orderToAdd), OrderDto.class);
	this.messagingTemplate.convertAndSend(
		MessageFormat.format("/notifications/restaurants/{0}/orders", result.getRestaurant().getId()),
		result);
	return result;
    }

    @Override
    public Collection<OrderListDto> getCustomerOrders(String customerId) {
	return this.foodOrderData.orders()
		.findByCustomerId(customerId)
		.stream()
		.map(order -> this.mapper.map(order, OrderListDto.class))
		.collect(Collectors.toList());
    }

    @Override
    public OrderDto getCustomerOrder(String customerId, String orderId) {
	Order order = this.foodOrderData.orders()
		.findById(orderId)
		.orElseThrow(() -> new NotFoundException("Order not found"));

	if (!customerId.equals(order.getCustomer().getId())) {
	    throw new NotFoundException("Order not found for customer");
	}

	return this.mapper.map(order, OrderDto.class);
    }
}
