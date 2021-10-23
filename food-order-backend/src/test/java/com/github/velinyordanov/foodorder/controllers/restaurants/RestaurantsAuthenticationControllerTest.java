package com.github.velinyordanov.foodorder.controllers.restaurants;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMAIL_OUT_OF_BOUNDS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_EMAIL;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_NAME;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_PASSWORD;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.NAME_DOES_NOT_MATCH_PATTERN;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.NAME_OUT_OF_BOUNDS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.NOT_EMAIL;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.PASSWORD_OUT_OF_BOUNDS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.PASSWORD_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.dto.DisposableEmailValidationApiResponse;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsAuthenticationService;

@WebMvcTest(controllers = RestaurantsAuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RestaurantsAuthenticationControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestaurantsAuthenticationService restaurantsAuthenticationService;

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
	public void registerShould_returnBadRequestWithCorrectData_whenRestaurantRegisterDtoHasValidationErrors(
			String fieldName, String value, Collection<String> expectedErrors) throws Exception {
		RestaurantRegisterDto restaurantRegisterDto = new RestaurantRegisterDto();
		restaurantRegisterDto.setPassword("validPassword123");
		restaurantRegisterDto.setName("validName");
		restaurantRegisterDto.setEmail("validEmail@asd.com");

		Field field = restaurantRegisterDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(restaurantRegisterDto, value);

		DisposableEmailValidationApiResponse response = new DisposableEmailValidationApiResponse();
		response.setDisposable("false");
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", restaurantRegisterDto.getEmail()))))
						.willReturn(response);

		MvcResult mvcResult = mockMvc.perform(post("/restaurants")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(restaurantRegisterDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andReturn();

		assertEquals(MethodArgumentNotValidException.class, mvcResult.getResolvedException().getClass());
		String responseAsString = mvcResult.getResponse().getContentAsString();
		assertThat(expectedErrors).allMatch(error -> responseAsString.contains(error));

		then(this.restaurantsAuthenticationService).shouldHaveNoInteractions();
	}

	@Test
	public void registerShould_returnBadRequestWithCorrectData_whenRestaurantRegisterDtoHasDisposableEmail()
			throws Exception {
		RestaurantRegisterDto restaurantRegisterDto = new RestaurantRegisterDto();
		restaurantRegisterDto.setPassword("validPassword123");
		restaurantRegisterDto.setName("validName");
		restaurantRegisterDto.setEmail("disposableEmail@asd.com");

		DisposableEmailValidationApiResponse response = new DisposableEmailValidationApiResponse();
		response.setDisposable("true");
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", restaurantRegisterDto.getEmail()))))
						.willReturn(response);

		MvcResult mvcResult = mockMvc.perform(post("/restaurants")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(restaurantRegisterDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", is("Disposable emails are not allowed.")))
				.andReturn();

		assertEquals(MethodArgumentNotValidException.class, mvcResult.getResolvedException().getClass());

		then(this.restaurantsAuthenticationService).shouldHaveNoInteractions();
	}

	@Test
	public void registerShould_returnOk_whenRestaurantRegisterDtoIsValid() throws JsonProcessingException, Exception {
		RestaurantRegisterDto restaurantRegisterDto = new RestaurantRegisterDto();
		restaurantRegisterDto.setPassword("validPassword123");
		restaurantRegisterDto.setName("validName");
		restaurantRegisterDto.setEmail("validEmail@asd.com");
		restaurantRegisterDto.setDescription("description");

		DisposableEmailValidationApiResponse response = new DisposableEmailValidationApiResponse();
		response.setDisposable("false");
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", restaurantRegisterDto.getEmail()))))
						.willReturn(response);
		
		given(this.restaurantsAuthenticationService.register(restaurantRegisterDto)).willReturn("accessToken");

		mockMvc.perform(post("/restaurants")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(restaurantRegisterDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", is("accessToken")));

		then(this.restaurantsAuthenticationService).should(times(1)).register(restaurantRegisterDto);
		then(this.restaurantsAuthenticationService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@MethodSource
	public void loginShould_returnBadRequestWithCorrectData_whenUserLoginDtoHasValidationErrors(
			String fieldName, String value, Collection<String> expectedErrors) throws Exception {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setPassword("validPassword123");
		userLoginDto.setEmail("validEmail@asd.com");

		Field field = userLoginDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(userLoginDto, value);

		MvcResult mvcResult = mockMvc.perform(post("/restaurants/tokens")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(userLoginDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andReturn();

		assertEquals(MethodArgumentNotValidException.class, mvcResult.getResolvedException().getClass());
		String responseAsString = mvcResult.getResponse().getContentAsString();
		assertThat(expectedErrors).allMatch(error -> responseAsString.contains(error));

		then(this.restaurantsAuthenticationService).shouldHaveNoInteractions();
	}
	
	@Test
	public void loginShould_returnOkLoginRestaurantAndReturnTheAccessToken_whenUserLoginDtoIsValid() throws Exception {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setPassword("validPassword123");
		userLoginDto.setEmail("validEmail@asd.com");
		
		given(this.restaurantsAuthenticationService.login(userLoginDto)).willReturn("accessToken");

		mockMvc.perform(post("/restaurants/tokens")
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(userLoginDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", is("accessToken")));
		
		then(this.restaurantsAuthenticationService).should(times(1)).login(userLoginDto);
		then(this.restaurantsAuthenticationService).shouldHaveNoMoreInteractions();
	}

	private static Stream<Arguments> loginShould_returnBadRequestWithCorrectData_whenUserLoginDtoHasValidationErrors() {
		return Stream.of(
				// email
				Arguments.of("email", null, List.of(EMPTY_EMAIL)),
				Arguments.of("email", "", List.of(EMPTY_EMAIL, EMAIL_OUT_OF_BOUNDS)),
				Arguments.of("email", "     ", List.of(EMPTY_EMAIL)),
				Arguments.of("email", "not a valid email", List.of(NOT_EMAIL)),
				Arguments.of("email", "a", List.of(EMAIL_OUT_OF_BOUNDS, NOT_EMAIL)),
				Arguments.of("email",
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@asd.com",
						List.of(EMAIL_OUT_OF_BOUNDS)),

				// password
				Arguments.of("password", null, List.of(EMPTY_PASSWORD)),
				Arguments.of("password", "", List.of(EMPTY_PASSWORD)),
				Arguments.of("password", "     ", List.of(EMPTY_PASSWORD)),
				Arguments.of("password", "a", List.of(PASSWORD_OUT_OF_BOUNDS)),
				Arguments.of("password", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						List.of(PASSWORD_OUT_OF_BOUNDS)),
				Arguments.of("password", "aaaaaaaaaa", List.of(PASSWORD_PATTERN)),
				Arguments.of("password", "AAAAAAAAAA", List.of(PASSWORD_PATTERN)),
				Arguments.of("password", "0123456789", List.of(PASSWORD_PATTERN)),
				Arguments.of("password", "aaaaaAAAAA", List.of(PASSWORD_PATTERN)),
				Arguments.of("password", "aaaaa12345", List.of(PASSWORD_PATTERN)),
				Arguments.of("password", "AAAAA55555", List.of(PASSWORD_PATTERN)));
	}

	private static Stream<Arguments> registerShould_returnBadRequestWithCorrectData_whenRestaurantRegisterDtoHasValidationErrors() {
		Stream<Arguments> emailAndPasswordArguments = loginShould_returnBadRequestWithCorrectData_whenUserLoginDtoHasValidationErrors();
		Stream<Arguments> nameArguments = Stream.of(
				// name
				Arguments.of("name", null, List.of(EMPTY_NAME)),
				Arguments.of("name", "", List.of(EMPTY_NAME, NAME_OUT_OF_BOUNDS)),
				Arguments.of("name", "   ", List.of(EMPTY_NAME)),
				Arguments.of("name", "asdasd0123", List.of(NAME_DOES_NOT_MATCH_PATTERN)),
				Arguments.of("name", "asdasd asdasd_", List.of(NAME_DOES_NOT_MATCH_PATTERN)),
				Arguments.of("name", "asdasd_asdasd", List.of(NAME_DOES_NOT_MATCH_PATTERN)),
				Arguments.of("name", "as", List.of(NAME_OUT_OF_BOUNDS)),
				Arguments.of("name",
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						List.of(NAME_OUT_OF_BOUNDS)));

		return Stream.concat(emailAndPasswordArguments, nameArguments);
	}
}
