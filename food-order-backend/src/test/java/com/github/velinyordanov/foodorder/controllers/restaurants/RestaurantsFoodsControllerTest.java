package com.github.velinyordanov.foodorder.controllers.restaurants;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_CATEGORY_NAME;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_CATEGORIES;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_DESCRIPTION;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_NAME;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_FOOD_PRICE;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.ZERO_OR_NEGATIVE_FOOD_PRICE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Set;
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
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsFoodsService;

@WebMvcTest(RestaurantsFoodsController.class)
public class RestaurantsFoodsControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestaurantsFoodsService restaurantsFoodsService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;

	ValidUserProvider validRestaurantProvider = new ValidUserProvider();

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void addFoodToRestaurantShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws JsonProcessingException, Exception {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("foodName");
		foodCreateDto.setDescription("description");
		foodCreateDto.setPrice(BigDecimal.valueOf(50));
		foodCreateDto.setCategories(Set.of(categoryDto));

		this.mockMvc.perform(post("/restaurants/restaurantId/foods")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(foodCreateDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsFoodsService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource("getFoodCreateDtoArguments")
	public void addFoodToRestaurantShould_returnBadRequest_whenFoodCreateDtoIsNotValid(String fieldName, Object value,
			String expectedError)
			throws JsonProcessingException, Exception {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("foodName");
		foodCreateDto.setDescription("description");
		foodCreateDto.setPrice(BigDecimal.valueOf(50));
		foodCreateDto.setCategories(Set.of(categoryDto));

		Field field = foodCreateDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(foodCreateDto, value);

		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		this.mockMvc.perform(post("/restaurants/restaurantId/foods")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(foodCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.restaurantsFoodsService).shouldHaveNoInteractions();
	}

	@Test
	public void addFoodToRestaurantShould_returnOkAndCorrectData_whenFoodCreateDtoIsValid()
			throws JsonProcessingException, Exception {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("foodName");
		foodCreateDto.setDescription("description");
		foodCreateDto.setPrice(BigDecimal.valueOf(50));
		foodCreateDto.setCategories(Set.of(categoryDto));

		FoodDto foodDto = new FoodDto();
		foodDto.setId("foodId");
		foodDto.setName("foodName");
		foodDto.setDescription("foodDescription");
		foodDto.setPrice(50);
		foodDto.setCategories(Set.of(categoryDto));
		String expectedResponse = this.objectMapper.writeValueAsString(foodDto);

		given(this.restaurantsFoodsService.addFoodToRestaurant("restaurantId", foodCreateDto)).willReturn(foodDto);

		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		this.mockMvc.perform(post("/restaurants/restaurantId/foods")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(foodCreateDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.restaurantsFoodsService).should(times(1)).addFoodToRestaurant("restaurantId", foodCreateDto);
		then(this.restaurantsFoodsService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void editFoodShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws JsonProcessingException, Exception {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("foodName");
		foodCreateDto.setDescription("description");
		foodCreateDto.setPrice(BigDecimal.valueOf(50));
		foodCreateDto.setCategories(Set.of(categoryDto));

		this.mockMvc.perform(put("/restaurants/restaurantId/foods/foodId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(foodCreateDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsFoodsService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource("getFoodCreateDtoArguments")
	public void editFoodShould_returnBadRequest_whenFoodCreateDtoIsNotValid(String fieldName, Object value,
			String expectedError)
			throws JsonProcessingException, Exception {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("foodName");
		foodCreateDto.setDescription("description");
		foodCreateDto.setPrice(BigDecimal.valueOf(50));
		foodCreateDto.setCategories(Set.of(categoryDto));

		Field field = foodCreateDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(foodCreateDto, value);

		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		this.mockMvc.perform(put("/restaurants/restaurantId/foods/foodId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(foodCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.restaurantsFoodsService).shouldHaveNoInteractions();
	}

	@Test
	public void editFoodShould_returnOkAndCorrectData_whenFoodCreateDtoIsValid()
			throws JsonProcessingException, Exception {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("foodName");
		foodCreateDto.setDescription("description");
		foodCreateDto.setPrice(BigDecimal.valueOf(50));
		foodCreateDto.setCategories(Set.of(categoryDto));

		FoodDto foodDto = new FoodDto();
		foodDto.setId("foodId");
		foodDto.setName("foodName");
		foodDto.setDescription("foodDescription");
		foodDto.setPrice(50);
		foodDto.setCategories(Set.of(categoryDto));
		String expectedResponse = this.objectMapper.writeValueAsString(foodDto);

		given(this.restaurantsFoodsService.editFood("restaurantId", "foodId", foodCreateDto)).willReturn(foodDto);

		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		this.mockMvc.perform(put("/restaurants/restaurantId/foods/foodId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(foodCreateDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.restaurantsFoodsService).should(times(1)).editFood("restaurantId", "foodId", foodCreateDto);
		then(this.restaurantsFoodsService).shouldHaveNoMoreInteractions();
	}
	
	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void deleteFoodFromRestaurantShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant) throws Exception {
		this.mockMvc.perform(delete("/restaurants/restaurantId/foods/foodId")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
		
		then(this.restaurantsFoodsService).shouldHaveNoInteractions();
	}

	@Test
	public void deleteFoodFromRestaurantShould_returnOkAndDeleteTheFood_whenUserIsAuthorized() throws Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		this.mockMvc.perform(delete("/restaurants/restaurantId/foods/foodId")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResponse().getContentAsString().isEmpty()));
		
		then(this.restaurantsFoodsService).should(times(1)).deleteFood("restaurantId", "foodId");
		then(this.restaurantsFoodsService).shouldHaveNoMoreInteractions();
	}

	public static Stream<Arguments> getFoodCreateDtoArguments() {
		return Stream.of(
				// name
				Arguments.of("name", null, EMPTY_FOOD_NAME),
				Arguments.of("name", "", EMPTY_FOOD_NAME),
				Arguments.of("name", "  ", EMPTY_FOOD_NAME),

				// description
				Arguments.of("description", null, EMPTY_FOOD_DESCRIPTION),
				Arguments.of("description", "", EMPTY_FOOD_DESCRIPTION),
				Arguments.of("description", "   ", EMPTY_FOOD_DESCRIPTION),

				// price
				Arguments.of("price", null, EMPTY_FOOD_PRICE),
				Arguments.of("price", BigDecimal.ZERO, ZERO_OR_NEGATIVE_FOOD_PRICE),
				Arguments.of("price", BigDecimal.valueOf(-5), ZERO_OR_NEGATIVE_FOOD_PRICE),

				// categories
				Arguments.of("categories", null, EMPTY_FOOD_CATEGORIES),
				Arguments.of("categories", Set.of(), EMPTY_FOOD_CATEGORIES),
				Arguments.of("categories", Set.of(new FoodCreateDto()), EMPTY_CATEGORY_NAME));
	}
}
