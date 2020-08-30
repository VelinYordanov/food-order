package com.github.velinyordanov.foodorder.controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.services.CustomersService;
import com.github.velinyordanov.foodorder.services.impl.JwtTokenServiceImpl;

@RestController
@RequestMapping("customers")
public class CustomersController {
    private static final String ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION =
	    "hasAuthority('ROLE_CUSTOMER') and principal.id == #customerId";

    private final CustomersService customersService;
    private final AuthenticationManager authenticationManager;
    private JwtTokenServiceImpl jwtTokenUtil;

    @Autowired
    public CustomersController(
	    CustomersService customersService,
	    AuthenticationManager authenticationManager,
	    JwtTokenServiceImpl jwtTokenUtil) {
	this.customersService = customersService;
	this.authenticationManager = authenticationManager;
	this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("")
    public JwtTokenDto registerUser(@Valid @RequestBody UserDto data) {
	return new JwtTokenDto(this.customersService.registerCustomer(data));
    }

    @PostMapping("tokens")
    public JwtTokenDto loginUser(@Valid @RequestBody UserDto data) {
	UsernamePasswordAuthenticationToken token =
		new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword());
	token.setDetails(UserType.Customer);

	Authentication authentication = this.authenticationManager.authenticate(token);
	return new JwtTokenDto(this.jwtTokenUtil.generateToken((Customer) authentication.getPrincipal()));
    }

    @PostMapping("{customerId}/addresses")
    @PreAuthorize(ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION)
    public AddressDto addAddressToCustomer(@PathVariable String customerId, @RequestBody AddressCreateDto address) {
	return this.customersService.addAddressToCustomer(customerId, address);
    }

    @PutMapping("{customerId}/addresses/{addressId}")
    @PreAuthorize(ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION)
    public AddressDto updateAddress(
	    @PathVariable String customerId,
	    @PathVariable String addressId,
	    @RequestBody AddressDto address) {
	return this.customersService.editAddress(customerId, addressId, address);
    }

    @GetMapping("{customerId}/addresses")
    @PreAuthorize(ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION)
    public Collection<AddressDto> getAddressesForCustomer(@PathVariable String customerId) {
	return this.customersService.getAddressesForCustomer(customerId);
    }

    @DeleteMapping("{customerId}/addresses/{addressId}")
    @PreAuthorize(ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION)
    public AddressDto deleteCustomerAddress(@PathVariable String customerId, @PathVariable String addressId) {
	return this.customersService.deleteCustomerAddress(customerId, addressId);
    }

    @GetMapping("{customerId}/addresses/{addressId}")
    @PreAuthorize(ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION)
    public AddressDto getCustomerAddress(@PathVariable String customerId, @PathVariable String addressId) {
	return this.customersService.getCustomerAddress(customerId, addressId);
    }
}
