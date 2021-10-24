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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.NotValidRestaurantArgumentsProvider;
import com.github.velinyordanov.foodorder.controllers.test.ValidRestaurantProvider;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsCategoriesService;

@WebMvcTest(RestaurantsCategoriesController.class)
public class RestaurantsCategoriesControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestaurantsCategoriesService restaurantsCategoriesService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;
	
	ValidRestaurantProvider validRestaurantProvider = new ValidRestaurantProvider();

	@Test
	public void getCategoriesShould_returnEmptyCollection_whenNoCategoriesAreFound() throws Exception {
		given(this.restaurantsCategoriesService.getCategoriesForRestaurant("restaurantId")).willReturn(List.of());
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		this.mockMvc.perform(get("/restaurants/restaurantId/categories")
				.with(user(restaurant)))
				.andExpect(status().isOk());
	}

	@Test
	public void getCategoriesShould_returnCorrectData_whenCategoriesAreFound() throws Exception {
		CategoryDto firstCategoryDto = new CategoryDto();
		firstCategoryDto.setId("firstCategoryDtoId");
		firstCategoryDto.setName("firstCategoryDto");

		CategoryDto secondCategoryDto = new CategoryDto();
		secondCategoryDto.setId("secondCategoryDtoId");
		secondCategoryDto.setName("secondCategoryDto");

		CategoryDto thirdCategoryDto = new CategoryDto();
		thirdCategoryDto.setId("thirdCategoryDtoId");
		thirdCategoryDto.setName("thirdCategoryDto");

		CategoryDto fourthCategoryDto = new CategoryDto();
		fourthCategoryDto.setId("fourthCategoryDtoId");
		fourthCategoryDto.setName("fourthCategoryDto");

		CategoryDto fifthCategoryDto = new CategoryDto();
		fifthCategoryDto.setId("fifthCategoryDtoId");
		fifthCategoryDto.setName("fifthCategoryDto");

		Collection<CategoryDto> returnedCategories = List.of(firstCategoryDto, secondCategoryDto, thirdCategoryDto,
				fourthCategoryDto,
				fifthCategoryDto);

		given(this.restaurantsCategoriesService.getCategoriesForRestaurant("restaurantId"))
				.willReturn(returnedCategories);
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		MvcResult result = this.mockMvc.perform(get("/restaurants/restaurantId/categories")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andReturn();

		assertEquals(this.objectMapper.writeValueAsString(returnedCategories),
				result.getResponse().getContentAsString());
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void getCategoriesShould_returnUnauthorized_whenRestaurantIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		this.mockMvc.perform(get("/restaurants/restaurantId/categories")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@Test
	public void deleteCategoryFromRestaurantShould_deleteTheCategory_whenAuthorized() throws Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();
		this.mockMvc.perform(delete("/restaurants/restaurantId/categories/categoryId")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResponse().getContentAsString().isEmpty()));

		then(this.restaurantsCategoriesService).should(times(1)).deleteCategory("restaurantId", "categoryId");
		then(this.restaurantsCategoriesService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void deleteCategoryFromRestaurantShould_returnUnauthorized_whenRestaurantIsNotAuthorized(
			Restaurant restaurant) throws Exception {
		this.mockMvc.perform(delete("/restaurants/restaurantId/categories/categoryId")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsCategoriesService).shouldHaveNoInteractions();
	}

	@Test
	public void addCategoryToRestaurantShould_addCategory_whenRestaurantIsAuthorizedAndCategoryNameIsValid()
			throws Exception {
		CategoryCreateDto categoryCreateDto = new CategoryCreateDto();
		categoryCreateDto.setName("categoryName");

		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName(categoryCreateDto.getName());
		given(this.restaurantsCategoriesService.addCategoryForRestaurant("restaurantId", categoryCreateDto))
				.willReturn(categoryDto);

		this.mockMvc.perform(post("/restaurants/restaurantId/categories")
				.content(this.objectMapper.writeValueAsString(categoryCreateDto))
				.contentType("application/json")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResponse().getContentAsString()
						.equals(this.objectMapper.writeValueAsString(categoryDto))));

		then(this.restaurantsCategoriesService).should(times(1)).addCategoryForRestaurant("restaurantId",
				categoryCreateDto);
		then(this.restaurantsCategoriesService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void addCategoryToRestaurantShould_returnUnauthorized_whenRestaurantIsNotAuthorized(
			Restaurant restaurant) throws Exception {
		CategoryCreateDto categoryCreateDto = new CategoryCreateDto();
		categoryCreateDto.setName("categoryName");

		this.mockMvc.perform(post("/restaurants/restaurantId/categories")
				.content(this.objectMapper.writeValueAsString(categoryCreateDto))
				.contentType("application/json")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.restaurantsCategoriesService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource
	public void addCategoryToRestaurantShould_returnBadRequest_whenCategoryNameIsNotValid(String name,
			String expectedError) throws Exception {
		CategoryCreateDto categoryCreateDto = new CategoryCreateDto();
		categoryCreateDto.setName(name);

		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		this.mockMvc.perform(post("/restaurants/restaurantId/categories")
				.content(this.objectMapper.writeValueAsString(categoryCreateDto))
				.contentType("application/json")
				.with(user(restaurant)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)))
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

		then(this.restaurantsCategoriesService).shouldHaveNoInteractions();
	}

	private static Stream<Arguments> addCategoryToRestaurantShould_returnBadRequest_whenCategoryNameIsNotValid() {
		return Stream.of(
				Arguments.of(null, EMPTY_CATEGORY_NAME),
				Arguments.of("", EMPTY_CATEGORY_NAME),
				Arguments.of("      ", EMPTY_CATEGORY_NAME),
				Arguments.of("a", CATEGORY_NAME_OUT_OF_BOUNDS),
				Arguments.of(
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						CATEGORY_NAME_OUT_OF_BOUNDS));
	}
}
