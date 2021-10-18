package com.github.velinyordanov.foodorder.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;

@ExtendWith(MockitoExtension.class)
public class CustomersUserDetailsServiceImplTest {
	@Mock
	private CustomersRepository customersRepository;
	
	@InjectMocks
	private CustomersUserDetailsServiceImpl customersUserDetailsServiceImpl;
	
	@Test
	public void loadUserByUsernameShould_throwUsernameNotFoundException_whenUserIsNotFound() {
		given(this.customersRepository.findByEmail("customerEmail")).willReturn(Optional.empty());
		
		UsernameNotFoundException exc = assertThrows(UsernameNotFoundException.class, () -> this.customersUserDetailsServiceImpl.loadUserByUsername("customerEmail"));
		
		assertEquals("Could not find user with this username and password", exc.getMessage());
	}
	
	@Test
	public void loadUserByUsernameShould_returnCorrectData_whenUserIsFound() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setEmail("customerEmail");
		customer.setPassword("customerPassword");
		customer.setName("customerName");
		customer.setPhoneNumber("customerPhone");

		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_CUSTOMER");
		authority.setCustomers(Set.of(customer));
		customer.setAuthorities(Set.of(authority));
		
		given(this.customersRepository.findByEmail("customerEmail")).willReturn(Optional.of(customer));
		
		UserDetails result = this.customersUserDetailsServiceImpl.loadUserByUsername("customerEmail");
		
		assertEquals(customer, result);
	}
}
