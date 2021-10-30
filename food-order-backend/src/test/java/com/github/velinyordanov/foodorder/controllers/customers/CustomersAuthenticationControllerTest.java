package com.github.velinyordanov.foodorder.controllers.customers;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMAIL_OUT_OF_BOUNDS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_EMAIL;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_NAME;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_PASSWORD;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_PHONE;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.NAME_DOES_NOT_MATCH_PATTERN;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.NAME_OUT_OF_BOUNDS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.NOT_EMAIL;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.PASSWORD_OUT_OF_BOUNDS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.PASSWORD_PATTERN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.dto.CustomerRegisterDto;
import com.github.velinyordanov.foodorder.dto.DisposableEmailValidationApiResponse;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.customers.CustomersAuthenticationService;

@WebMvcTest(CustomersAuthenticationController.class)
public class CustomersAuthenticationControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CustomersAuthenticationService customersAuthenticationService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;

	@MockBean
	private RestTemplate restTemplate;

	@ParameterizedTest
	@MethodSource
	public void registerUserShould_returnBadRequestWithCorrectData_whenCustomerRegisterDtoHasValidationErrors(
			String fieldName, String value, String expectedError) throws Exception {
		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setPassword("validPassword123");
		customerRegisterDto.setName("validName");
		customerRegisterDto.setEmail("validEmail@asd.com");
		customerRegisterDto.setPhoneNumber("phoneNumber");

		Field field = customerRegisterDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(customerRegisterDto, value);

		DisposableEmailValidationApiResponse response = new DisposableEmailValidationApiResponse();
		response.setDisposable("false");
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", customerRegisterDto.getEmail()))))
						.willReturn(response);

		mockMvc.perform(post("/customers")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(customerRegisterDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.customersAuthenticationService).shouldHaveNoInteractions();
	}
	
	@Test
	public void registerUserShould_returnBadRequestWithCorrectData_whenCustomerRegisterDtoHasDisposableEmail() throws Exception {
		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setPassword("validPassword123");
		customerRegisterDto.setName("validName");
		customerRegisterDto.setEmail("validEmail@asd.com");
		customerRegisterDto.setPhoneNumber("phoneNumber");

		DisposableEmailValidationApiResponse response = new DisposableEmailValidationApiResponse();
		response.setDisposable("true");
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", customerRegisterDto.getEmail()))))
						.willReturn(response);

		mockMvc.perform(post("/customers")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(customerRegisterDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", is("Disposable emails are not allowed.")));

		then(this.customersAuthenticationService).shouldHaveNoInteractions();
	}
	
	@Test
	public void registerUserShould_returnOkWithCorrectData_whenCustomerRegisterDtoIsValid() throws Exception {
		CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
		customerRegisterDto.setPassword("validPassword123");
		customerRegisterDto.setName("validName");
		customerRegisterDto.setEmail("validEmail@asd.com");
		customerRegisterDto.setPhoneNumber("phoneNumber");

		DisposableEmailValidationApiResponse response = new DisposableEmailValidationApiResponse();
		response.setDisposable("false");
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", customerRegisterDto.getEmail()))))
						.willReturn(response);
		
		given(this.customersAuthenticationService.registerCustomer(customerRegisterDto)).willReturn("authenticationToken");

		mockMvc.perform(post("/customers")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(customerRegisterDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", is("authenticationToken")));

		then(this.customersAuthenticationService).should(times(1)).registerCustomer(customerRegisterDto);
		then(this.customersAuthenticationService).shouldHaveNoMoreInteractions();
	}
	
	@ParameterizedTest
	@MethodSource
	public void loginUserShould_returnBadRequestWithCorrectData_whenUserLoginDtoHasValidationErrors(String fieldName, String value, String expectedError) throws JsonProcessingException, Exception {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setPassword("validPassword123");
		userLoginDto.setEmail("validEmail@asd.com");

		Field field = userLoginDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(userLoginDto, value);

		mockMvc.perform(post("/customers/tokens")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(userLoginDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.customersAuthenticationService).shouldHaveNoInteractions();
	}
	
	@Test
	public void loginUserShould_returnOkWithCorrectData_whenUserLoginDtoIsValid() throws JsonProcessingException, Exception {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setPassword("validPassword123");
		userLoginDto.setEmail("validEmail@asd.com");
		
		JwtTokenDto tokenDto = new JwtTokenDto("authenticationToken");
		given(this.customersAuthenticationService.login(userLoginDto)).willReturn(tokenDto);

		mockMvc.perform(post("/customers/tokens")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(userLoginDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", is("authenticationToken")));

		then(this.customersAuthenticationService).should(times(1)).login(userLoginDto);
		then(this.authenticationService).shouldHaveNoMoreInteractions();
	}

	private static Stream<Arguments> loginUserShould_returnBadRequestWithCorrectData_whenUserLoginDtoHasValidationErrors() {
		return Stream.of(
				// email
				Arguments.of("email", null, EMPTY_EMAIL),
				Arguments.of("email", "", EMPTY_EMAIL),
				Arguments.of("email", "     ", EMPTY_EMAIL),
				Arguments.of("email", "not a valid email", NOT_EMAIL),
				Arguments.of("email", "a", EMAIL_OUT_OF_BOUNDS),
				Arguments.of("email",
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@asd.com",
						EMAIL_OUT_OF_BOUNDS),

				// password
				Arguments.of("password", null, EMPTY_PASSWORD),
				Arguments.of("password", "", EMPTY_PASSWORD),
				Arguments.of("password", "     ", EMPTY_PASSWORD),
				Arguments.of("password", "a", PASSWORD_OUT_OF_BOUNDS),
				Arguments.of("password", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						PASSWORD_OUT_OF_BOUNDS),
				Arguments.of("password", "aaaaaaaaaa", PASSWORD_PATTERN),
				Arguments.of("password", "AAAAAAAAAA", PASSWORD_PATTERN),
				Arguments.of("password", "0123456789", PASSWORD_PATTERN),
				Arguments.of("password", "aaaaaAAAAA", PASSWORD_PATTERN),
				Arguments.of("password", "aaaaa12345", PASSWORD_PATTERN),
				Arguments.of("password", "AAAAA55555", PASSWORD_PATTERN));
	}

	private static Stream<Arguments> registerUserShould_returnBadRequestWithCorrectData_whenCustomerRegisterDtoHasValidationErrors() {
		Stream<Arguments> emailAndPasswordArguments = loginUserShould_returnBadRequestWithCorrectData_whenUserLoginDtoHasValidationErrors();
		Stream<Arguments> nameArguments = Stream.of(
				// name
				Arguments.of("name", null, EMPTY_NAME),
				Arguments.of("name", "", EMPTY_NAME),
				Arguments.of("name", "   ", EMPTY_NAME),
				Arguments.of("name", "asdasd0123", NAME_DOES_NOT_MATCH_PATTERN),
				Arguments.of("name", "asdasd asdasd_", NAME_DOES_NOT_MATCH_PATTERN),
				Arguments.of("name", "asdasd_asdasd", NAME_DOES_NOT_MATCH_PATTERN),
				Arguments.of("name", "as", NAME_OUT_OF_BOUNDS),
				Arguments.of("name",
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						NAME_OUT_OF_BOUNDS),
				
				// phone number
				Arguments.of("phoneNumber", null, EMPTY_PHONE),
				Arguments.of("phoneNumber", "", EMPTY_PHONE),
				Arguments.of("phoneNumber", "     ", EMPTY_PHONE)
				);

		return Stream.concat(emailAndPasswordArguments, nameArguments);
	}
}
