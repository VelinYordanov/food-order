package com.github.velinyordanov.foodorder.controllers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.data.entities.Status;
import com.github.velinyordanov.foodorder.dto.EnumDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.EnumsService;

@WebMvcTest(EnumsController.class)
public class EnumsControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private EnumsService enumsService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;

	@ParameterizedTest
	@MethodSource("getEnumEndpointsArguments")
	@WithAnonymousUser
	public <T extends Enum<T>> void enumEndpointsShould_returnUnauthorized_whenUserIsNotAuthorized(String urlPath, Class<T> enumClass)
			throws Exception {
		String url = "/enums/" + urlPath;

		this.mockMvc.perform(get(url))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@ParameterizedTest
	@MethodSource("getEnumEndpointsArguments")
	@WithMockUser(authorities = "ROLE_CUSTOMER")
	public <T extends Enum<T>> void enumEndpointsShould_returnOkWithCorrectData_whenUserIsAuthorizedCustomer(
			String urlPath, Class<T> enumClass)
			throws Exception {
		String url = "/enums/" + urlPath;

		var data = EnumSet.allOf(enumClass)
				.stream()
				.map(x -> new EnumDto(x.ordinal(), x.name()))
				.collect(Collectors.toList());
		String expectedResponse = this.objectMapper.writeValueAsString(data);

		given(this.enumsService.getEnumOptions(enumClass)).willReturn(data);

		this.mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}
	
	@ParameterizedTest
	@MethodSource("getEnumEndpointsArguments")
	@WithMockUser(authorities = "ROLE_RESTAURANT")
	public <T extends Enum<T>> void enumEndpointsShould_returnOkWithCorrectData_whenUserIsAuthorizedRestaurant(
			String urlPath, Class<T> enumClass)
			throws Exception {
		String url = "/enums/" + urlPath;

		var data = EnumSet.allOf(enumClass)
				.stream()
				.map(x -> new EnumDto(x.ordinal(), x.name()))
				.collect(Collectors.toList());
		String expectedResponse = this.objectMapper.writeValueAsString(data);

		given(this.enumsService.getEnumOptions(enumClass)).willReturn(data);

		this.mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	private static Stream<Arguments> getEnumEndpointsArguments() {
		return Stream.of(
				Arguments.of("address-types", AddressType.class),
				Arguments.of("cities", City.class),
				Arguments.of("order-statuses", Status.class));
	}
}
