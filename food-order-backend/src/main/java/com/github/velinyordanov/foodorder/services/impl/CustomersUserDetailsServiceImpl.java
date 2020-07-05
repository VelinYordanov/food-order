package com.github.velinyordanov.foodorder.services.impl;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.services.CustomersUserDetailsService;

@Service
public class CustomersUserDetailsServiceImpl implements CustomersUserDetailsService {
    private final CustomersRepository customersRepository;

    public CustomersUserDetailsServiceImpl(CustomersRepository customersRepository) {
	this.customersRepository = customersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	Optional<Customer> user = this.customersRepository.findByUsername(username);
	return user.orElseThrow(
		() -> new UsernameNotFoundException("Could not find user with this username and password"));
    }
}
