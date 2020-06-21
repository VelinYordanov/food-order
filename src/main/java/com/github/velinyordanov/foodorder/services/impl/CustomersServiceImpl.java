package com.github.velinyordanov.foodorder.services.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.AuthoritiesRepository;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.CustomersService;
import com.github.velinyordanov.foodorder.services.JwtTokenService;

@Service
public class CustomersServiceImpl implements CustomersService {
    private final Mapper mapper;
    private final CustomersRepository customersRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder encoder;

    public CustomersServiceImpl(
	    Mapper mapper,
	    CustomersRepository customersRepository,
	    PasswordEncoder encoder,
	    AuthoritiesRepository authoritiesRepository,
	    JwtTokenService jwtTokenService,
	    AuthenticationManager authenticationManager) {
	this.mapper = mapper;
	this.customersRepository = customersRepository;
	this.authoritiesRepository = authoritiesRepository;
	this.jwtTokenService = jwtTokenService;
	this.authenticationManager = authenticationManager;
	this.encoder = encoder;
    }

    @Override
    public Optional<Customer> findById(String id) {
	return this.customersRepository.findById(id);
    }

    @Override
    @Transactional
    public String registerCustomer(UserDto user) {
	if (this.customersRepository.existsByUsername(user.getUsername())) {
	    throw new DuplicateUserException("Customer with this username already exists");
	}

	Customer customer = this.mapper.map(user, Customer.class);
	customer.setPassword(this.encoder.encode(user.getPassword()));
	Optional<Authority> authorityOptional = this.authoritiesRepository.findFirstByAuthority("ROLE_CUSTOMER");
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

	Customer savedRestaurant = this.customersRepository.save(customer);
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
}
