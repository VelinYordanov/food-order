package com.github.velinyordanov.foodorder.controllers.customers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.ValidUserProvider;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.CustomerRegisterDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.JwtUserDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.services.JwtTokenService;

import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomersAuthenticationControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CustomersRepository customersRepository;
	
	@Autowired
	private JwtTokenService jwtTokenService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final ValidUserProvider validUserProvider = new ValidUserProvider();

	@Test
	public void registerShould_saveTheNewCustomerToTheDatabaseAndReturnCorrectToken_whenDataIsValid()
			throws JsonProcessingException, Exception {
		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setPassword("validPassword123");
		customerRegisterDto.setName("validName");
		customerRegisterDto.setEmail("validEmail@asd.com");
		customerRegisterDto.setPhoneNumber("phoneNumber");

		MvcResult result = mockMvc.perform(post("/customers")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(customerRegisterDto)))
				.andExpect(status().isOk())
				.andReturn();

		Optional<Customer> customerOptional = this.customersRepository.findByEmail("validEmail@asd.com");
		assertTrue(customerOptional.isPresent());
		
		Customer customer = customerOptional.get();
		assertThat(customer.getAuthorities()).anyMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority()));
		
		JwtTokenDto response = this.objectMapper.readValue(result.getResponse().getContentAsString(), JwtTokenDto.class);
		JwtUserDto tokenCustomer = this.jwtTokenService.parseToken(response.getToken());
		assertEquals(customer.getId(), tokenCustomer.getId());
		assertEquals(customer.getEmail(), tokenCustomer.getEmail());
		assertEquals(customer.getName(), tokenCustomer.getName());
		assertThat(tokenCustomer.getAuthorities()).anyMatch(a -> "ROLE_CUSTOMER".equals(a));
	}
	
	@Test
	public void loginShould_returnCorrectTokenWithFoundCustomer_whenDataIsValid()
			throws JsonProcessingException, Exception {
		Customer customer = this.validUserProvider.getValidCustomer();
		customer.setEmail("validEmail@asd.asd");
		customer.setPassword(this.passwordEncoder.encode("Password_123"));
		this.customersRepository.save(customer);
		
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail("validEmail@asd.asd");
		userLoginDto.setPassword("Password_123");

		MvcResult result = mockMvc.perform(post("/customers/tokens")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(userLoginDto)))
				.andExpect(status().isOk())
				.andReturn();

		JwtTokenDto response = this.objectMapper.readValue(result.getResponse().getContentAsString(), JwtTokenDto.class);
		JwtUserDto tokenCustomer = this.jwtTokenService.parseToken(response.getToken());
		assertEquals(customer.getId(), tokenCustomer.getId());
		assertEquals(customer.getEmail(), tokenCustomer.getEmail());
		assertEquals(customer.getName(), tokenCustomer.getName());
		assertThat(tokenCustomer.getAuthorities()).anyMatch(a -> "ROLE_CUSTOMER".equals(a));
	}
}
