package com.github.velinyordanov.foodorder.controllers.restaurants;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_NAME;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsService;

@WebMvcTest(RestaurantsController.class)
public class RestaurantsControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestaurantsService restaurantsService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;
	
	ValidUserProvider validRestaurantProvider = new ValidUserProvider();

	@Test
	public void getAllShould_returnTheRestaurants_whenRestaurantsAreFound() throws Exception {
		RestaurantDtoImpl restaurantDto = new RestaurantDtoImpl();
		restaurantDto.setId("restaurantId");
		restaurantDto.setName("restaurantName");
		restaurantDto.setDescription("restaurantDescription");

		RestaurantDtoImpl restaurantDto2 = new RestaurantDtoImpl();
		restaurantDto2.setId("restaurantI2");
		restaurantDto2.setName("restaurantName2");
		restaurantDto2.setDescription("restaurantDescription2");

		RestaurantDtoImpl restaurantDto3 = new RestaurantDtoImpl();
		restaurantDto3.setId("restaurantId3");
		restaurantDto3.setName("restaurantName3");
		restaurantDto3.setDescription("restaurantDescription3");

		Collection<RestaurantDto> returnedRestaurants = List.of(restaurantDto, restaurantDto2, restaurantDto3);
		given(this.restaurantsService.getAll()).willReturn(returnedRestaurants);
		String expectedResponse = this.objectMapper.writeValueAsString(returnedRestaurants);

		this.mockMvc.perform(get("/restaurants")
				.with(anonymous()))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	@Test
	public void getAllShould_returnAnEmptyCollection_whenNoRestaurantsAreFound() throws Exception {
		Collection<RestaurantDto> returnedRestaurants = List.of();
		given(this.restaurantsService.getAll()).willReturn(returnedRestaurants);

		this.mockMvc.perform(get("/restaurants")
				.with(anonymous()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(empty())));
	}

	@Test
	public void getRestaurantDataShould_returnCorrectData_whenRestaurantIsFound() throws Exception {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		CategoryDto categoryDto2 = new CategoryDto();
		categoryDto2.setId("categoryId2");
		categoryDto2.setName("categoryName2");

		FoodDto foodDto = new FoodDto();
		foodDto.setId("foodId");
		foodDto.setName("foodName");
		foodDto.setPrice(30d);
		foodDto.setDescription("foodDescription");
		foodDto.setCategories(List.of(categoryDto));

		FoodDto foodDto2 = new FoodDto();
		foodDto2.setId("foodId2");
		foodDto2.setName("foodName2");
		foodDto2.setPrice(50d);
		foodDto2.setDescription("foodDescription2");
		foodDto2.setCategories(List.of(categoryDto, categoryDto2));

		RestaurantDataDto restaurantDataDto = new RestaurantDataDto();
		restaurantDataDto.setId("restaurantId");
		restaurantDataDto.setName("restaurantName");
		restaurantDataDto.setDescription("restaurantDescription");
		restaurantDataDto.setCategories(List.of(categoryDto, categoryDto2));
		restaurantDataDto.setFoods(List.of(foodDto, foodDto2));

		given(this.restaurantsService.getRestaurantData("restaurantId")).willReturn(restaurantDataDto);
		String expectedResponse = this.objectMapper.writeValueAsString(restaurantDataDto);

		this.mockMvc.perform(get("/restaurants/restaurantId")
				.with(anonymous()))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void editRestaurantShould_returnUnauthorized_whenUserIsNotAuthenticated(Restaurant restaurant)
			throws JsonProcessingException, Exception {
		RestaurantEditDto restaurantEditDto = new RestaurantEditDto();
		restaurantEditDto.setName("restaurantName");
		restaurantEditDto.setDescription("restaurantDescription");

		this.mockMvc.perform(put("/restaurants/restaurantId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(restaurantEditDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
		
		then(this.restaurantsService).shouldHaveNoInteractions();
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "     ")
	public void editRestaurantShould_returnBadRequest_whenRestaurantEditDtoIsNotValid(String name)
			throws JsonProcessingException, Exception {
		RestaurantEditDto restaurantEditDto = new RestaurantEditDto();
		restaurantEditDto.setName(name);
		restaurantEditDto.setDescription("restaurantDescription");

		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();
		this.mockMvc.perform(put("/restaurants/restaurantId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(restaurantEditDto)))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(EMPTY_NAME)));
		
		then(this.restaurantsService).shouldHaveNoInteractions();
	}
	

	@Test
	public void editRestaurantShould_returnOkAndEditRestaurant_whenRestaurantEditDtoIsValid()
			throws JsonProcessingException, Exception {
		RestaurantEditDto restaurantEditDto = new RestaurantEditDto();
		restaurantEditDto.setName("restaurantName");
		restaurantEditDto.setDescription("restaurantDescription");

		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId("categoryId");
		categoryDto.setName("categoryName");

		CategoryDto categoryDto2 = new CategoryDto();
		categoryDto2.setId("categoryId2");
		categoryDto2.setName("categoryName2");

		FoodDto foodDto = new FoodDto();
		foodDto.setId("foodId");
		foodDto.setName("foodName");
		foodDto.setPrice(30d);
		foodDto.setDescription("foodDescription");
		foodDto.setCategories(List.of(categoryDto));

		FoodDto foodDto2 = new FoodDto();
		foodDto2.setId("foodId2");
		foodDto2.setName("foodName2");
		foodDto2.setPrice(50d);
		foodDto2.setDescription("foodDescription2");
		foodDto2.setCategories(List.of(categoryDto, categoryDto2));

		RestaurantDataDto restaurantDataDto = new RestaurantDataDto();
		restaurantDataDto.setId("restaurantId");
		restaurantDataDto.setName("restaurantName");
		restaurantDataDto.setDescription("restaurantDescription");
		restaurantDataDto.setCategories(List.of(categoryDto, categoryDto2));
		restaurantDataDto.setFoods(List.of(foodDto, foodDto2));
		given(this.restaurantsService.editRestaurant("restaurantId", restaurantEditDto)).willReturn(restaurantDataDto);
		String expectedResponse = this.objectMapper.writeValueAsString(restaurantDataDto);
		
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();
		this.mockMvc.perform(put("/restaurants/restaurantId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(restaurantEditDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
		
		then(this.restaurantsService).should(times(1)).editRestaurant("restaurantId", restaurantEditDto);
		then(this.restaurantsService).shouldHaveNoMoreInteractions();
	}

	private class RestaurantDtoImpl implements RestaurantDto {
		private String id;
		private String name;
		private String description;

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getDescription() {
			return this.description;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
}
