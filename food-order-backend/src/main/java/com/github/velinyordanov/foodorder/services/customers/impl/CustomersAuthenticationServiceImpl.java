package com.github.velinyordanov.foodorder.services.customers.impl;

import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.CustomerRegisterDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.customers.CustomersAuthenticationService;

@Service
@Transactional
public class CustomersAuthenticationServiceImpl implements CustomersAuthenticationService {
	private final Mapper mapper;
	private final PasswordEncoder encoder;
	private final FoodOrderData foodOrderData;
	private final JwtTokenService jwtTokenService;
	private final AuthenticationManager authenticationManager;

	public CustomersAuthenticationServiceImpl(Mapper mapper, PasswordEncoder encoder, FoodOrderData foodOrderData,
			JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
		this.mapper = mapper;
		this.encoder = encoder;
		this.foodOrderData = foodOrderData;
		this.jwtTokenService = jwtTokenService;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public String registerCustomer(@Valid CustomerRegisterDto user) {
		if (this.foodOrderData.customers()
				.existsByEmailOrPhoneNumber(user.getEmail(), user.getPhoneNumber())) {
			throw new DuplicateUserException("Customer with this email or phone number already exists");
		}

		Customer customer = this.mapper.map(user, Customer.class);
		customer.setPassword(this.encoder.encode(user.getPassword()));
		Authority authority = this.foodOrderData.authorities()
				.findFirstByAuthority("ROLE_CUSTOMER")
				.orElseGet(() -> {
					Authority roleCustomer = new Authority();
					roleCustomer.setAuthority("ROLE_CUSTOMER");
					return roleCustomer;
				});

		customer.addAuthority(authority);
		return this.jwtTokenService.generateToken(this.foodOrderData.customers().save(customer));
	}

	@Override
	public JwtTokenDto login(UserLoginDto user) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(),
				user.getPassword());
		token.setDetails(UserType.Customer);

		Authentication authentication = this.authenticationManager.authenticate(token);
		return new JwtTokenDto(jwtTokenService.generateToken((Customer) authentication.getPrincipal()));
	}

	@Override
	public Optional<Customer> findById(String id) {
		return this.foodOrderData.customers().findById(id);
	}
}
