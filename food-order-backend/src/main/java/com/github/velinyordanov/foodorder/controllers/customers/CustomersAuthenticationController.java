package com.github.velinyordanov.foodorder.controllers.customers;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.CustomerRegisterDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.services.customers.CustomersAuthenticationService;

@RestController
@RequestMapping("customers")
public class CustomersAuthenticationController {
	private final CustomersAuthenticationService customersAuthenticationService;

	public CustomersAuthenticationController(CustomersAuthenticationService customersAuthenticationService) {
		this.customersAuthenticationService = customersAuthenticationService;
	}

	@PostMapping("")
	public JwtTokenDto registerUser(@Valid @RequestBody CustomerRegisterDto data) {
		return new JwtTokenDto(this.customersAuthenticationService.registerCustomer(data));
	}

	@PostMapping("tokens")
	public JwtTokenDto loginUser(@Valid @RequestBody UserLoginDto data) {
		return this.customersAuthenticationService.login(data);
	}
}
