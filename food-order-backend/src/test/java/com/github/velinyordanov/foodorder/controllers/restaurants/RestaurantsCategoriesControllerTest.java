package com.github.velinyordanov.foodorder.controllers.restaurants;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.NotValidRestaurantArgumentsProvider;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
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

	@Test
	public void getCategoriesShould_returnEmptyCollection_whenNoCategoriesAreFound() throws Exception {
		given(this.restaurantsCategoriesService.getCategoriesForRestaurant("restaurantId")).willReturn(List.of());
		Restaurant restaurant = this.getValidRestaurant();

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
		
		given(this.restaurantsCategoriesService.getCategoriesForRestaurant("restaurantId")).willReturn(returnedCategories);
		Restaurant restaurant = this.getValidRestaurant();

		MvcResult result = this.mockMvc.perform(get("/restaurants/restaurantId/categories")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andReturn();
		
		assertEquals(this.objectMapper.writeValueAsString(returnedCategories), result.getResponse().getContentAsString());
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

	private Restaurant getValidRestaurant() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setEmail("restaurantEmail");
		restaurant.setName("restaurantName");
		restaurant.setPassword("restaurantPassword");
		restaurant.setDescription("restaurantDescription");

		Authority authority = new Authority();
		authority.setId("authorityId");
		authority.setAuthority("ROLE_RESTAURANT");
		authority.setRestaurants(Set.of(restaurant));
		restaurant.setAuthorities(Set.of(authority));

		return restaurant;
	}
}
