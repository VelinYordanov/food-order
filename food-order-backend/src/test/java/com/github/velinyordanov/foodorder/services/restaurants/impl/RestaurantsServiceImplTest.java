package com.github.velinyordanov.foodorder.services.restaurants.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.FoodsRepository;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantsServiceImplTest {
	@Mock
	private FoodsRepository foodsRepository;

	@Mock
	private RestaurantsRepository restaurantsRepository;

	@Mock
	private FoodOrderData foodOrderData;

	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());

	@InjectMocks
	private RestaurantsServiceImpl restaurantsServiceImpl;

	@Test
	public void getAllShould_ReturnTheRestaurantData() {
		RestaurantDto restaurantDto = mock(RestaurantDto.class);
		RestaurantDto restaurantDto2 = mock(RestaurantDto.class);
		RestaurantDto restaurantDto3 = mock(RestaurantDto.class);
		given(this.restaurantsRepository.getRestaurantsList())
				.willReturn(List.of(restaurantDto, restaurantDto2, restaurantDto3));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Collection<RestaurantDto> result = this.restaurantsServiceImpl.getAll();

		assertThat(result).usingRecursiveComparison().isEqualTo(List.of(restaurantDto, restaurantDto2, restaurantDto3));
	}

	@Test
	public void getRestaurantDataShould_throwNotFoundException_whenRestaurantIsNotFound() {
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.empty());
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsServiceImpl.getRestaurantData("restaurantId"));
		assertEquals("Restaurant with id restaurantId not found", exc.getMessage());
	}

	@Test
	public void getRestaurantDataShould_returnCorrectData_whenRestaurantIsPresent() {
		Category category = new Category();
		category.setId("categoryId");
		category.setName("categoryName");

		Category category2 = new Category();
		category.setId("category2Id");
		category.setName("category2Name");

		Category category3 = new Category();
		category.setId("category3Id");
		category.setName("category3Name");

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");
		restaurant.setCategories(Set.of(category, category2, category3));

		Food food = new Food();
		food.setId("foodId");
		food.setName("foodName");
		food.setDescription("foodDescription");
		food.setPrice(BigDecimal.valueOf(15));
		food.setCategories(Set.of(category, category3));

		Food food2 = new Food();
		food2.setId("food2Id");
		food2.setName("food2Name");
		food2.setDescription("food2Description");
		food2.setPrice(BigDecimal.valueOf(30));
		food2.setCategories(Set.of(category, category2));

		Food food3 = new Food();
		food3.setId("food3Id");
		food3.setName("food3Name");
		food3.setDescription("food3Description");
		food3.setPrice(BigDecimal.valueOf(50));
		food3.setCategories(Set.of(category, category2, category3));
		category.setFoods(Set.of(food, food2, food3));
		category2.setFoods(Set.of(food2, food3));
		category3.setFoods(Set.of(food, food3));

		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food, food2, food3));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);

		RestaurantDataDto result = this.restaurantsServiceImpl.getRestaurantData("restaurantId");
		assertThat(result).usingRecursiveComparison().ignoringFields("foods").isEqualTo(restaurant);

		List<Food> expectedFoods = List.of(food, food2, food3);
		assertThat(result.getFoods()).usingRecursiveComparison().ignoringFields("price").isEqualTo(expectedFoods);
	}

	@Test
	public void editRestaurantShould_throwNotFoundException_whenRestaurantIsNotFound() {
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.empty());
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsServiceImpl.editRestaurant("restaurantId", new RestaurantEditDto()));

		assertEquals("Restaurant with id restaurantId not found!", exc.getMessage());
	}

	@Test
	public void editRestaurantShould_saveTheRestaurantWithCorrectData_whenRestaurantIsFound() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");
		restaurant.setCategories(Set.of());

		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.restaurantsRepository.save(any())).willAnswer(answer -> answer.getArgument(0));
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(Set.of());
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);

		RestaurantEditDto restaurantEditDto = new RestaurantEditDto();
		restaurantEditDto.setName("restaurantEdittedName");
		restaurantEditDto.setDescription("restaurantEdittedDescription");

		this.restaurantsServiceImpl.editRestaurant("restaurantId", restaurantEditDto);

		then(this.restaurantsRepository).should(times(1)).save(restaurant);
		assertEquals(restaurant.getName(), restaurantEditDto.getName());
		assertEquals(restaurant.getDescription(), restaurantEditDto.getDescription());
	}

	@Test
	public void editRestaurantShould_returnCorrectData_whenRestaurantIsFound() {
		Category category = new Category();
		category.setId("categoryId");
		category.setName("categoryName");

		Category category2 = new Category();
		category.setId("category2Id");
		category.setName("category2Name");

		Category category3 = new Category();
		category.setId("category3Id");
		category.setName("category3Name");

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");
		restaurant.setCategories(Set.of(category, category2, category3));

		Food food = new Food();
		food.setId("foodId");
		food.setName("foodName");
		food.setDescription("foodDescription");
		food.setPrice(BigDecimal.valueOf(15));
		food.setCategories(Set.of(category, category3));

		Food food2 = new Food();
		food2.setId("food2Id");
		food2.setName("food2Name");
		food2.setDescription("food2Description");
		food2.setPrice(BigDecimal.valueOf(30));
		food2.setCategories(Set.of(category, category2));

		Food food3 = new Food();
		food3.setId("food3Id");
		food3.setName("food3Name");
		food3.setDescription("food3Description");
		food3.setPrice(BigDecimal.valueOf(50));
		food3.setCategories(Set.of(category, category2, category3));
		category.setFoods(Set.of(food, food2, food3));
		category2.setFoods(Set.of(food2, food3));
		category3.setFoods(Set.of(food, food3));

		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.restaurantsRepository.save(any())).willAnswer(answer -> answer.getArgument(0));
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(Set.of(food, food2, food3));
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);

		RestaurantEditDto restaurantEditDto = new RestaurantEditDto();
		restaurantEditDto.setName("restaurantEdittedName");
		restaurantEditDto.setDescription("restaurantEdittedDescription");

		RestaurantDataDto restaurantDataDto = this.restaurantsServiceImpl.editRestaurant("restaurantId",
				restaurantEditDto);

		assertThat(restaurantDataDto)
				.usingRecursiveComparison()
				.ignoringFields("foods")
				.isEqualTo(restaurant);

		assertThat(restaurantDataDto.getFoods())
				.usingRecursiveComparison()
				.ignoringFields("price")
				.isEqualTo(Set.of(food, food2, food3));
	}
}
