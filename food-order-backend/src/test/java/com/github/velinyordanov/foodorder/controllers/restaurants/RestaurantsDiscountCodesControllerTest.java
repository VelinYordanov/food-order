package com.github.velinyordanov.foodorder.controllers.restaurants;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.NotValidRestaurantArgumentsProvider;
import com.github.velinyordanov.foodorder.controllers.test.ValidUserProvider;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeEditDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeListDto;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsDiscountCodesService;

@WebMvcTest(RestaurantsDiscountCodesController.class)
public class RestaurantsDiscountCodesControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestaurantsDiscountCodesService restaurantsDiscountCodesService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;

	ValidUserProvider validUserProvider = new ValidUserProvider();

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void addDiscountCodeToRestaurantShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws JsonProcessingException, Exception {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setCode("X5ASD3");
		discountCodeCreateDto.setDiscountPercentage(15);
		discountCodeCreateDto.setValidFrom(LocalDate.of(999999999, Month.JANUARY, 15));
		discountCodeCreateDto.setValidTo(LocalDate.of(999999999, Month.DECEMBER, 15));

		this.mockMvc.perform(post("/restaurants/restaurantId/discount-codes")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeCreateDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource
	public void addDiscountCodeToRestaurantShould_returnBadRequest_whenDiscountCodeCreateDtoIsNotValid(String fieldName,
			Object value, String expectedError) throws JsonProcessingException, Exception {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setCode("X5ASD3");
		discountCodeCreateDto.setDiscountPercentage(15);
		discountCodeCreateDto.setValidFrom(LocalDate.of(999999999, Month.JANUARY, 15));
		discountCodeCreateDto.setValidTo(LocalDate.of(999999999, Month.DECEMBER, 15));

		Field field = discountCodeCreateDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(discountCodeCreateDto, value);

		Restaurant restaurant = this.validUserProvider.getValidRestaurant();
		this.mockMvc.perform(post("/restaurants/restaurantId/discount-codes")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}
	
	@Test
	public void addDiscountCodeToRestaurantShould_returnBadRequest_whenValidFromIsAfterValidTo() throws JsonProcessingException, Exception {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setCode("X5ASD3");
		discountCodeCreateDto.setDiscountPercentage(15);
		discountCodeCreateDto.setValidFrom(LocalDate.of(999999999, Month.DECEMBER, 15));
		discountCodeCreateDto.setValidTo(LocalDate.of(999999999, Month.JANUARY, 15));

		Restaurant restaurant = this.validUserProvider.getValidRestaurant();
		this.mockMvc.perform(post("/restaurants/restaurantId/discount-codes")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(VALID_FROM_AFTER_VALID_TO)));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}

	@Test
	public void addDiscountCodeToRestaurantShould_returnCorrectDataAndAddDiscountCode_whenDiscountCodeCreateDtoIsValid()
			throws JsonProcessingException, Exception {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setCode("X5ASD3");
		discountCodeCreateDto.setDiscountPercentage(15);
		discountCodeCreateDto.setValidFrom(LocalDate.of(999999999, Month.JANUARY, 15));
		discountCodeCreateDto.setValidTo(LocalDate.of(999999999, Month.DECEMBER, 15));

		Restaurant restaurant = this.validUserProvider.getValidRestaurant();

		DiscountCodeDto discountCodeDto = new DiscountCodeDto();
		discountCodeDto.setId("discountCodeId");
		discountCodeDto.setCode("X5ASD3");
		discountCodeDto.setDiscountPercentage(15);
		given(this.restaurantsDiscountCodesService.addDiscountCodeToRestaurant("restaurantId", discountCodeCreateDto))
				.willReturn(discountCodeDto);
		String expectedResponse = this.objectMapper.writeValueAsString(discountCodeDto);

		this.mockMvc.perform(post("/restaurants/restaurantId/discount-codes")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeCreateDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.restaurantsDiscountCodesService).should(times(1)).addDiscountCodeToRestaurant("restaurantId",
				discountCodeCreateDto);
		then(this.restaurantsDiscountCodesService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void getDiscountCodesForRestaurantShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		this.mockMvc.perform(get("/restaurants/restaurantId/discount-codes")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}

	@Test
	public void getDiscountCodesForRestaurantShould_returnUnauthorized_whenUserIsNotAuthorized()
			throws Exception {
		Restaurant restaurant = this.validUserProvider.getValidRestaurant();

		DiscountCodeListDto discountCodeListDto = new DiscountCodeListDto();
		discountCodeListDto.setId("discountCodeId");
		discountCodeListDto.setCode("discountCode");
		discountCodeListDto.setDiscountPercentage(15);
		discountCodeListDto.setTimesUsed(3);
		discountCodeListDto.setValidFrom(LocalDate.of(2021, Month.JULY, 15));
		discountCodeListDto.setValidTo(LocalDate.of(2021, Month.SEPTEMBER, 15));

		Collection<DiscountCodeListDto> expecteDiscountCodeListDtos = List.of(discountCodeListDto);
		String expectedResponse = this.objectMapper.writeValueAsString(expecteDiscountCodeListDtos);

		given(this.restaurantsDiscountCodesService.getDiscountCodesForRestaurant("restaurantId"))
				.willReturn(expecteDiscountCodeListDtos);
		this.mockMvc.perform(get("/restaurants/restaurantId/discount-codes")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	@Test
	public void getDiscountCodeShould_returnUnauthorized_whenUserIsNotACustomer() throws Exception {
		this.mockMvc.perform(get("/restaurants/restaurantId/discount-codes/code"))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsDiscountCodesService).shouldHaveNoMoreInteractions();
	}

	@Test
	public void getDiscountCodeShould_returnTheDiscountCode_whenDiscountCodeIsFound() throws Exception {
		DiscountCodeDto discountCodeDto = new DiscountCodeDto();
		discountCodeDto.setId("discountCodeId");
		discountCodeDto.setCode("X5ASD3");
		discountCodeDto.setDiscountPercentage(15);

		given(this.restaurantsDiscountCodesService.getDiscountByCode("restaurantId", "code", "customerId"))
				.willReturn(discountCodeDto);
		String expectedResponse = this.objectMapper.writeValueAsString(discountCodeDto);

		Customer customer = this.validUserProvider.getValidCustomer();
		this.mockMvc.perform(get("/restaurants/restaurantId/discount-codes/code")
				.with(user(customer)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.restaurantsDiscountCodesService).should(times(1)).getDiscountByCode("restaurantId", "code",
				"customerId");
		then(this.restaurantsDiscountCodesService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void deleteDiscountCodeShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		this.mockMvc.perform(delete("/restaurants/restaurantId/discount-codes/discountCodeId")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}

	@Test
	public void deleteDiscountCodeShould_returnOkAndDeleteTheDiscountCode_whenUserIsAuthorized() throws Exception {
		Restaurant restaurant = this.validUserProvider.getValidRestaurant();

		this.mockMvc.perform(delete("/restaurants/restaurantId/discount-codes/discountCodeId")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResponse().getContentAsString().isEmpty()));

		then(this.restaurantsDiscountCodesService).should(times(1)).deleteDiscountCode("restaurantId",
				"discountCodeId");
		then(this.restaurantsDiscountCodesService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void editDiscountCodeShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(15);
		discountCodeEditDto.setValidFrom(LocalDate.of(999999999, Month.JANUARY, 15));
		discountCodeEditDto.setValidTo(LocalDate.of(999999999, Month.DECEMBER, 15));

		this.mockMvc.perform(put("/restaurants/restaurantId/discount-codes/discountCodeId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeEditDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource
	public void editDiscountCodeShould_returnBadRequest_whenDiscountCodeEditDtoIsNotValid(String fieldName,
			Object value, String expectedError)
			throws Exception {
		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(15);
		discountCodeEditDto.setValidFrom(LocalDate.of(999999999, Month.JANUARY, 15));
		discountCodeEditDto.setValidTo(LocalDate.of(999999999, Month.DECEMBER, 15));

		Field field = discountCodeEditDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(discountCodeEditDto, value);

		Restaurant restaurant = this.validUserProvider.getValidRestaurant();

		this.mockMvc.perform(put("/restaurants/restaurantId/discount-codes/discountCodeId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeEditDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}
	
	@Test
	public void editDiscountCodeShould_returnBadRequest_whenValidFromIsAfterValidTo()
			throws Exception {
		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(15);
		discountCodeEditDto.setValidFrom(LocalDate.of(999999999, Month.DECEMBER, 15));
		discountCodeEditDto.setValidTo(LocalDate.of(999999999, Month.JANUARY, 15));

		Restaurant restaurant = this.validUserProvider.getValidRestaurant();

		this.mockMvc.perform(put("/restaurants/restaurantId/discount-codes/discountCodeId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeEditDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(VALID_FROM_AFTER_VALID_TO)));

		then(this.restaurantsDiscountCodesService).shouldHaveNoInteractions();
	}
	
	@Test
	public void editDiscountCodeShould_returnOkAndCorrectData_whenDiscountCodeEditDtoIsValid()
			throws Exception {
		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(15);
		discountCodeEditDto.setValidFrom(LocalDate.of(999999999, Month.JANUARY, 15));
		discountCodeEditDto.setValidTo(LocalDate.of(999999999, Month.DECEMBER, 15));
		
		DiscountCodeListDto discountCodeListDto = new DiscountCodeListDto();
		discountCodeListDto.setId("discountCodeId");
		discountCodeListDto.setCode("discountCode");
		discountCodeListDto.setDiscountPercentage(15);
		discountCodeListDto.setTimesUsed(3);
		discountCodeListDto.setValidFrom(LocalDate.of(2021, Month.JULY, 15));
		discountCodeListDto.setValidTo(LocalDate.of(2021, Month.SEPTEMBER, 15));
		
		String expectedResponse = this.objectMapper.writeValueAsString(discountCodeListDto);
		
		given(this.restaurantsDiscountCodesService.editDiscountCode("restaurantId", "discountCodeId", discountCodeEditDto)).willReturn(discountCodeListDto);
		
		Restaurant restaurant = this.validUserProvider.getValidRestaurant();

		this.mockMvc.perform(put("/restaurants/restaurantId/discount-codes/discountCodeId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(discountCodeEditDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.restaurantsDiscountCodesService).should(times(1)).editDiscountCode("restaurantId", "discountCodeId", discountCodeEditDto);
		then(this.restaurantsDiscountCodesService).shouldHaveNoMoreInteractions();
	}

	private static Stream<Arguments> addDiscountCodeToRestaurantShould_returnBadRequest_whenDiscountCodeCreateDtoIsNotValid() {
		Stream<Arguments> addDiscountCodeArguments = Stream.of(
				// code
				Arguments.of("code", null, EMPTY_DISCOUNT_CODE),
				Arguments.of("code", "", EMPTY_DISCOUNT_CODE),
				Arguments.of("code", "     ", EMPTY_DISCOUNT_CODE),
				Arguments.of("code", "asd", DISCOUNT_CODE_OUT_OF_BOUNDS),
				Arguments.of("code", "asdasdasdasdasdasd", DISCOUNT_CODE_OUT_OF_BOUNDS),

				// valid from
				Arguments.of("validFrom", LocalDate.of(2000, Month.JANUARY, 15), PAST_VALID_FROM));

		return Stream.concat(
				editDiscountCodeShould_returnBadRequest_whenDiscountCodeEditDtoIsNotValid(),
				addDiscountCodeArguments);
	}

	private static Stream<Arguments> editDiscountCodeShould_returnBadRequest_whenDiscountCodeEditDtoIsNotValid() {
		return Stream.of(
				// discount percentage
				Arguments.of("discountPercentage", -5, DISCOUNT_PERCENTAGE_OUT_OF_BOUNDS),
				Arguments.of("discountPercentage", 0, DISCOUNT_PERCENTAGE_OUT_OF_BOUNDS),
				Arguments.of("discountPercentage", 101, DISCOUNT_PERCENTAGE_OUT_OF_BOUNDS),

				// valid from
				Arguments.of("validFrom", null, EMPTY_VALID_FROM),

				// valid to
				Arguments.of("validTo", null, EMPTY_VALID_TO),
				Arguments.of("validTo", LocalDate.of(2000, Month.JANUARY, 15), PAST_VALID_TO));
	}
}
