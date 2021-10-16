package com.github.velinyordanov.foodorder.services.customers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.velinyordanov.foodorder.data.AuthoritiesRepository;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.CustomerRegisterDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;
import com.github.velinyordanov.foodorder.services.JwtTokenService;

@ExtendWith(MockitoExtension.class)
public class CustomersAuthenticationServiceImplTest {
	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());

	@Mock
	private PasswordEncoder encoder;

	@Mock
	private CustomersRepository customersRepository;

	@Mock
	private AuthoritiesRepository authoritiesRepository;

	@Mock
	private FoodOrderData foodOrderData;

	@Mock
	private JwtTokenService jwtTokenService;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private CustomersAuthenticationServiceImpl customersAuthenticationServiceImpl;

	@Test
	public void registerCustomerShould_throwDuplicateUserException_whenCustomerAlreadyExists() {
		given(this.customersRepository.existsByEmailOrPhoneNumber("customerEmail", "customerPhone")).willReturn(true);
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);

		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setEmail("customerEmail");
		customerRegisterDto.setPhoneNumber("customerPhone");

		DuplicateUserException exc = assertThrows(DuplicateUserException.class,
				() -> customersAuthenticationServiceImpl.registerCustomer(customerRegisterDto));

		assertEquals("Customer with this email or phone number already exists", exc.getMessage());
	}

	@Test
	public void registerCustomerShould_saveTheCustomerWithCorrectData_whenCustomerDoesNotExistAndCustomerAuthorityExists() {
		given(this.customersRepository.existsByEmailOrPhoneNumber("customerEmail", "customerPhone")).willReturn(false);

		Authority authority = new Authority();
		authority.setCustomers(new HashSet<>());
		given(this.authoritiesRepository.findFirstByAuthority("ROLE_CUSTOMER")).willReturn(Optional.of(authority));
		given(this.customersRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.authorities()).willReturn(this.authoritiesRepository);

		given(this.encoder.encode("customerPassword")).willReturn("customerEncodedPassword");

		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setEmail("customerEmail");
		customerRegisterDto.setPassword("customerPassword");
		customerRegisterDto.setName("customerName");
		customerRegisterDto.setPhoneNumber("customerPhone");

		customersAuthenticationServiceImpl.registerCustomer(customerRegisterDto);

		ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
		then(this.customersRepository).should(times(1)).save(customerCaptor.capture());
		then(this.customersRepository).shouldHaveNoMoreInteractions();

		Customer customer = customerCaptor.getValue();
		assertEquals(customer.getName(), customerRegisterDto.getName());
		assertEquals(customer.getPhoneNumber(), customerRegisterDto.getPhoneNumber());
		assertEquals(customer.getEmail(), customerRegisterDto.getEmail());
		assertEquals(customer.getPassword(), "customerEncodedPassword");
		assertEquals(customer.getAuthorities(), Set.of(authority));
		assertEquals(authority.getCustomers(), Set.of(customer));
	}

	@Test
	public void registerCustomerShould_saveTheCustomerWithCorrectData_whenCustomerDoesNotExistAndCustomerAuthorityDoesNotExist() {
		given(this.customersRepository.existsByEmailOrPhoneNumber("customerEmail", "customerPhone")).willReturn(false);

		given(this.authoritiesRepository.findFirstByAuthority("ROLE_CUSTOMER")).willReturn(Optional.empty());
		given(this.customersRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.authorities()).willReturn(this.authoritiesRepository);

		given(this.encoder.encode("customerPassword")).willReturn("customerEncodedPassword");

		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setEmail("customerEmail");
		customerRegisterDto.setPassword("customerPassword");
		customerRegisterDto.setName("customerName");
		customerRegisterDto.setPhoneNumber("customerPhone");

		customersAuthenticationServiceImpl.registerCustomer(customerRegisterDto);

		ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
		then(this.customersRepository).should(times(1)).save(customerCaptor.capture());
		then(this.customersRepository).shouldHaveNoMoreInteractions();

		Customer customer = customerCaptor.getValue();
		assertEquals(customer.getName(), customerRegisterDto.getName());
		assertEquals(customer.getPhoneNumber(), customerRegisterDto.getPhoneNumber());
		assertEquals(customer.getEmail(), customerRegisterDto.getEmail());
		assertEquals(customer.getPassword(), "customerEncodedPassword");
		assertThat(customer.getAuthorities())
				.hasSize(1)
				.first()
				.matches(authority -> "ROLE_CUSTOMER".equals(authority.getAuthority()), "hasAuthoroityRoleCustomer");
	}

	@Test
	public void registerCustomerShould_generateTokenWithCorrectData_whenCustomerDoesNotExistAndAuthorityExists() {
		given(this.customersRepository.existsByEmailOrPhoneNumber("customerEmail", "customerPhone")).willReturn(false);

		Authority authority = new Authority();
		authority.setCustomers(new HashSet<>());
		given(this.authoritiesRepository.findFirstByAuthority("ROLE_CUSTOMER")).willReturn(Optional.of(authority));
		given(this.customersRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.authorities()).willReturn(this.authoritiesRepository);

		given(this.encoder.encode("customerPassword")).willReturn("customerEncodedPassword");

		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setEmail("customerEmail");
		customerRegisterDto.setPassword("customerPassword");
		customerRegisterDto.setName("customerName");
		customerRegisterDto.setPhoneNumber("customerPhone");

		customersAuthenticationServiceImpl.registerCustomer(customerRegisterDto);

		ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
		then(this.jwtTokenService).should(times(1)).generateToken(customerCaptor.capture());
		then(this.jwtTokenService).shouldHaveNoMoreInteractions();

		Customer customer = customerCaptor.getValue();
		assertEquals(customer.getName(), customerRegisterDto.getName());
		assertEquals(customer.getPhoneNumber(), customerRegisterDto.getPhoneNumber());
		assertEquals(customer.getEmail(), customerRegisterDto.getEmail());
		assertEquals(customer.getPassword(), "customerEncodedPassword");
		assertEquals(customer.getAuthorities(), Set.of(authority));
		assertEquals(authority.getCustomers(), Set.of(customer));
	}

	@Test
	public void registerCustomerShould_generateTokenWithCorrectData_whenCustomerDoesNotExistAndCustomerAuthorityDoesNotExist() {
		given(this.customersRepository.existsByEmailOrPhoneNumber("customerEmail", "customerPhone")).willReturn(false);

		given(this.authoritiesRepository.findFirstByAuthority("ROLE_CUSTOMER")).willReturn(Optional.empty());
		given(this.customersRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.authorities()).willReturn(this.authoritiesRepository);

		given(this.encoder.encode("customerPassword")).willReturn("customerEncodedPassword");

		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setEmail("customerEmail");
		customerRegisterDto.setPassword("customerPassword");
		customerRegisterDto.setName("customerName");
		customerRegisterDto.setPhoneNumber("customerPhone");

		customersAuthenticationServiceImpl.registerCustomer(customerRegisterDto);

		ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
		then(this.jwtTokenService).should(times(1)).generateToken(customerCaptor.capture());
		then(this.jwtTokenService).shouldHaveNoMoreInteractions();

		Customer customer = customerCaptor.getValue();
		assertEquals(customer.getName(), customerRegisterDto.getName());
		assertEquals(customer.getPhoneNumber(), customerRegisterDto.getPhoneNumber());
		assertEquals(customer.getEmail(), customerRegisterDto.getEmail());
		assertEquals(customer.getPassword(), "customerEncodedPassword");
		assertThat(customer.getAuthorities())
				.hasSize(1)
				.first()
				.matches(authority -> "ROLE_CUSTOMER".equals(authority.getAuthority()), "hasAuthoroityRoleCustomer");
	}

	@Test
	public void loginShould_callTheAuthenticationManagerWithCorrectToken() {
		Customer customer = new Customer();
		Authentication authentication = mock(Authentication.class);
		given(authentication.getPrincipal()).willReturn(customer);
		given(this.authenticationManager.authenticate(any())).willReturn(authentication);

		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail("loginEmail");
		userLoginDto.setPassword("loginPassword");

		this.customersAuthenticationServiceImpl.login(userLoginDto);

		ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor = ArgumentCaptor
				.forClass(UsernamePasswordAuthenticationToken.class);
		then(this.authenticationManager).should(times(1)).authenticate(authenticationCaptor.capture());
		then(this.authenticationManager).shouldHaveNoMoreInteractions();

		UsernamePasswordAuthenticationToken token = authenticationCaptor.getValue();
		assertEquals("loginEmail", token.getPrincipal());
		assertEquals("loginPassword", token.getCredentials());
		assertEquals(UserType.Customer, token.getDetails());
	}
	
	@Test
	public void loginShould_generateACorrectToken() {
		Customer customer = new Customer();
		Authentication authentication = mock(Authentication.class);
		given(authentication.getPrincipal()).willReturn(customer);
		given(this.authenticationManager.authenticate(any())).willReturn(authentication);
		given(this.jwtTokenService.generateToken(customer)).willReturn("authenticationToken");

		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail("loginEmail");
		userLoginDto.setPassword("loginPassword");

		JwtTokenDto result = this.customersAuthenticationServiceImpl.login(userLoginDto);

		then(this.jwtTokenService).should(times(1)).generateToken(customer);
		then(this.jwtTokenService).shouldHaveNoMoreInteractions();
		assertEquals("authenticationToken", result.getToken());
	}
	
	@Test
	public void findByIdShould_ReturnCorrectData() {
		Customer customer = new Customer();
		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		
		Customer result = this.customersAuthenticationServiceImpl.findById("customerId").get();
		
		assertEquals(customer, result);
	}
}
