package com.github.velinyordanov.foodorder.services.impl;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.services.CustomersUserDetailsService;

@Service
@Transactional
public class CustomersUserDetailsServiceImpl implements CustomersUserDetailsService {
	private final CustomersRepository customersRepository;

	public CustomersUserDetailsServiceImpl(CustomersRepository customersRepository) {
		this.customersRepository = customersRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.customersRepository
				.findByEmail(email)
				.orElseThrow(
						() -> new UsernameNotFoundException("Could not find user with this username and password"));
	}
}
