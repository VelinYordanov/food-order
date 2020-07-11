package com.github.velinyordanov.foodorder.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.services.CustomersService;
import com.github.velinyordanov.foodorder.services.impl.JwtTokenServiceImpl;

@RestController
@RequestMapping("customers")
public class CustomersController {
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
}
