package com.github.velinyordanov.foodorder.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.services.CustomersUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class CustomerAuthenticationProviderTest {
	@Mock
	private CustomersUserDetailsService customersUserDetailsService;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private CustomerAuthenticationProvider customerAuthenticationProvider;
	
	@Test
	public void authenticateShould_returnNull_whenAuthenticationDetailsIsNull() {
		Authentication authentication = mock(Authentication.class);
		given(authentication.getDetails()).willReturn(null);
		
		assertNull(this.customerAuthenticationProvider.authenticate(authentication));
	}
	
	@Test
	public void authenticateShould_returnNull_whenAuthenticationDetailsIsUserTypeRestaurant() {
		Authentication authentication = mock(Authentication.class);
		given(authentication.getDetails()).willReturn(UserType.Restaurant);
		
		assertNull(this.customerAuthenticationProvider.authenticate(authentication));
	}
	
	@Test
	public void authenticateShould_returnCorrectAuthentication_whenAuthenticationDetailsAreUserTypeCustomer() {
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
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customer, customer.getPassword());
		usernamePasswordAuthenticationToken.setDetails(UserType.Customer);
		
		given(this.customersUserDetailsService.loadUserByUsername("customerEmail")).willReturn(customer);
		given(this.passwordEncoder.matches(anyString(), anyString())).willAnswer(answer -> answer.getArgument(0).equals(answer.getArgument(1)));
		
		assertNotNull(this.customerAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken));
	}
}
