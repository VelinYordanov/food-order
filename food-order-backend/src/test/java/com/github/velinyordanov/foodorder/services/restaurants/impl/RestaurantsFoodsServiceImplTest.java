package com.github.velinyordanov.foodorder.services.restaurants.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.argThat;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.github.velinyordanov.foodorder.data.CategoriesRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.FoodsRepository;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.ForeignCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.exceptions.UnrecognizedCategoriesException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantsFoodsServiceImplTest {
	@Mock
	private FoodOrderData foodOrderData;

	@Mock
	private RestaurantsRepository restaurantsRepository;

	@Mock
	private CategoriesRepository categoriesRepository;

	@Mock
	private FoodsRepository foodsRepository;

	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());

	@InjectMocks
	private RestaurantsFoodsServiceImpl restaurantsFoodsServiceImpl;

	@Test
	public void deleteFoodShould_throwNotFoundException_whenRestaurantIsNotFound() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.empty());

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsFoodsServiceImpl.deleteFood("restaurantId", "foodId"));
		assertEquals("Restaurant with id restaurantId not found", exc.getMessage());
	}

	@Test
	public void deleteFoodShould_throwNotFoundException_whenFoodIsNotFound() {
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodsRepository.findById("foodId")).willReturn(Optional.empty());

		Restaurant restaurant = new Restaurant();
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsFoodsServiceImpl.deleteFood("restaurantId", "foodId"));
		assertEquals("Could not find food with id foodId", exc.getMessage());
	}

	@Test
	public void deleteFoodShould_throwNotFoundException_whenFoodDoesNotBelongToRestaurant() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Restaurant restaurant2 = new Restaurant();
		restaurant2.setId("notRestaurantId");
		Category category = new Category();
		category.setRestaurant(restaurant2);
		Food food = new Food();
		food.addCategory(category);

		given(this.foodsRepository.findById("foodId")).willReturn(Optional.of(food));
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsFoodsServiceImpl.deleteFood("restaurantId", "foodId"));
		assertEquals("Could not find food for restaurant", exc.getMessage());
	}

	@Test
	public void deleteFoodShould_deleteTheEntity_whenValidationsPass() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Category category = new Category();
		category.setRestaurant(restaurant);
		Food food = new Food();
		food.addCategory(category);

		given(this.foodsRepository.findById("foodId")).willReturn(Optional.of(food));
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);

		this.restaurantsFoodsServiceImpl.deleteFood("restaurantId", "foodId");

		then(this.foodsRepository).should(times(1)).delete(food);
		then(this.foodsRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	public void addFoodToRestaurantShould_throwNotFoundException_whenRestaurantIsNotFound() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.empty());

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsFoodsServiceImpl.addFoodToRestaurant("restaurantId", new FoodCreateDto()));

		assertEquals("Restaurant with id restaurantId not found!", exc.getMessage());
	}

	@Test
	public void addFoodToRestaurantShould_throwUnrecognizedCategoriesException_whenThereAreNewCategories() {
		Restaurant restaurant = new Restaurant();
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Category category = new Category();
		category.setId("firstCategoryId");
		category.setName("firstCategory");
		given(this.categoriesRepository.findAllById(any())).willReturn(List.of(category));
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		CategoryDto firstCategory = new CategoryDto();
		firstCategory.setId("firstCategoryId");
		firstCategory.setName("firstCategory");

		CategoryDto secondCategory = new CategoryDto();
		secondCategory.setId("secondCategoryId");
		secondCategory.setName("secondCategoryName");

		CategoryDto thirdCategory = new CategoryDto();
		thirdCategory.setId("thirdCategoryId");
		thirdCategory.setName("thirdCategoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setCategories(Set.of(firstCategory, secondCategory, thirdCategory));

		UnrecognizedCategoriesException exc = assertThrows(UnrecognizedCategoriesException.class,
				() -> this.restaurantsFoodsServiceImpl.addFoodToRestaurant("restaurantId", foodCreateDto));
		assertEquals(
				"Unrecognized categories detected. You need to create them first. thirdCategoryName, secondCategoryName",
				exc.getMessage());
	}

	@Test
	public void addFoodToRestaurantShould_throwForeignCategoryException_whenCategoryBelongsToAnotherRestaurant() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Category category = new Category();
		category.setId("firstCategoryId");
		category.setName("firstCategory");
		category.setRestaurant(restaurant);

		Category category2 = new Category();
		category2.setId("secondCategoryId");
		category2.setName("secondCategoryName");
		category2.setRestaurant(restaurant);

		Restaurant anotherRestaurant = new Restaurant();
		anotherRestaurant.setId("anotherRestaurantId");

		Category category3 = new Category();
		category3.setId("thirdCategoryId");
		category3.setName("thirdCategoryName");
		category3.setRestaurant(anotherRestaurant);
		given(this.categoriesRepository.findAllById(any())).willReturn(List.of(category, category2, category3));
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		CategoryDto firstCategory = new CategoryDto();
		firstCategory.setId("firstCategoryId");
		firstCategory.setName("firstCategory");

		CategoryDto secondCategory = new CategoryDto();
		secondCategory.setId("secondCategoryId");
		secondCategory.setName("secondCategoryName");

		CategoryDto thirdCategory = new CategoryDto();
		thirdCategory.setId("thirdCategoryId");
		thirdCategory.setName("thirdCategoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setCategories(Set.of(firstCategory, secondCategory, thirdCategory));

		ForeignCategoryException exc = assertThrows(ForeignCategoryException.class,
				() -> this.restaurantsFoodsServiceImpl.addFoodToRestaurant("restaurantId", foodCreateDto));
		assertEquals("Category belongs to another restaurant", exc.getMessage());
	}

	@Test
	public void addFoodToRestaurantShould_saveTheFoodWithCorrectData_whenValidationsPass() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Category category = new Category();
		category.setId("firstCategoryId");
		category.setName("firstCategory");
		category.setRestaurant(restaurant);

		Category category2 = new Category();
		category2.setId("secondCategoryId");
		category2.setName("secondCategoryName");
		category2.setRestaurant(restaurant);

		Category category3 = new Category();
		category3.setId("thirdCategoryId");
		category3.setName("thirdCategoryName");
		category3.setRestaurant(restaurant);

		Collection<Category> categories = List.of(category, category2, category3);
		given(this.categoriesRepository.findAllById(any())).willReturn(categories);
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodsRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		CategoryDto firstCategory = new CategoryDto();
		firstCategory.setId("firstCategoryId");
		firstCategory.setName("firstCategory");

		CategoryDto secondCategory = new CategoryDto();
		secondCategory.setId("secondCategoryId");
		secondCategory.setName("secondCategoryName");

		CategoryDto thirdCategory = new CategoryDto();
		thirdCategory.setId("thirdCategoryId");
		thirdCategory.setName("thirdCategoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("foodName");
		foodCreateDto.setPrice(BigDecimal.valueOf(30));
		foodCreateDto.setDescription("Some long description");

		foodCreateDto.setCategories(Set.of(firstCategory, secondCategory, thirdCategory));

		this.restaurantsFoodsServiceImpl.addFoodToRestaurant("restaurantId", foodCreateDto);

		ArgumentCaptor<Food> argumentCaptor = ArgumentCaptor.forClass(Food.class);
		then(this.foodsRepository).should(times(1)).save(argumentCaptor.capture());
		then(this.foodsRepository).shouldHaveNoMoreInteractions();

		Food result = argumentCaptor.getValue();
		assertEquals(result.getName(), foodCreateDto.getName());
		assertEquals(result.getDescription(), foodCreateDto.getDescription());
		assertEquals(result.getPrice(), foodCreateDto.getPrice());
		assertThat(result.getCategories()).containsAll(categories);
	}

	@Test
	public void editFood_throwNotFoundException_whenFoodIsNotFound() {
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodsRepository.findById("foodId")).willReturn(Optional.empty());

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsFoodsServiceImpl.editFood("restaurantId", "foodId", new FoodCreateDto()));
		assertEquals("Food with id foodId not found", exc.getMessage());
	}

	@Test
	public void editFood_throwBadRequestException_whenFoodHasNoValidCategories() {
		Food food = new Food();
		food.setCategories(Set.of());
		given(this.foodsRepository.findById("foodId")).willReturn(Optional.of(food));
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.categoriesRepository.findByRestaurantId("restaurantId")).willReturn(List.of());
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		BadRequestException exc = assertThrows(BadRequestException.class,
				() -> this.restaurantsFoodsServiceImpl.editFood("restaurantId", "foodId", new FoodCreateDto()));
		assertEquals("At least one valid category is required.", exc.getMessage());
	}

	@ParameterizedTest
	@MethodSource("categoriesMethodSource")
	public void editFood_saveTheFoodWithAddedAndRemovedCategory_whenValidationsPass(
			Set<Category> initialCategories,
			Set<Category> returnedCategories,
			Set<CategoryDto> edittedCategories,
			Set<Category> resultCategories) {
		Food food = new Food();
		food.setName("name");
		food.setDescription("description");
		food.setPrice(BigDecimal.valueOf(15));

		food.setCategories(new HashSet<>(initialCategories));
		given(this.foodsRepository.findById("foodId")).willReturn(Optional.of(food));
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodsRepository.save(any())).willAnswer(answer -> answer.getArgument(0));
		given(this.categoriesRepository.findByRestaurantId("restaurantId"))
				.willReturn(returnedCategories);
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("editName");
		foodCreateDto.setDescription("editDescription");
		foodCreateDto.setPrice(BigDecimal.valueOf(30));

		foodCreateDto.setCategories(new HashSet<>(edittedCategories));

		ArgumentCaptor<Food> argumentCaptor = ArgumentCaptor.forClass(Food.class);

		this.restaurantsFoodsServiceImpl.editFood("restaurantId", "foodId", foodCreateDto);

		then(this.foodsRepository).should(times(1)).save(argumentCaptor.capture());
		Food result = argumentCaptor.getValue();

		assertEquals(foodCreateDto.getName(), result.getName());
		assertEquals(foodCreateDto.getDescription(), result.getDescription());
		assertEquals(food.getPrice(), result.getPrice());
		assertThat(result.getCategories()).containsAll(resultCategories);
	}

	@Test
	public void editFood_returnCorrectData_whenValidationsPass() {
		Food food = new Food();
		food.setId("foodId");
		food.setName("name");
		food.setDescription("description");
		food.setPrice(BigDecimal.valueOf(15));

		Category category = new Category();
		category.setId("firstCategoryId");
		category.setName("firstCategory");

		Category category2 = new Category();
		category2.setId("secondCategoryId");
		category2.setName("secondCategoryName");

		Category category3 = new Category();
		category3.setId("thirdCategoryId");
		category3.setName("thirdCategoryName");

		food.setCategories(new HashSet<>(Set.of(category)));
		given(this.foodsRepository.findById("foodId")).willReturn(Optional.of(food));
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodsRepository.save(any())).willAnswer(answer -> answer.getArgument(0));
		given(this.categoriesRepository.findByRestaurantId("restaurantId"))
				.willReturn(List.of(category, category2, category3));
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("editName");
		foodCreateDto.setDescription("editDescription");
		foodCreateDto.setPrice(BigDecimal.valueOf(30));

		CategoryDto firstCategory = new CategoryDto();
		firstCategory.setId("firstCategoryId");
		firstCategory.setName("firstCategory");

		CategoryDto secondCategory = new CategoryDto();
		secondCategory.setId("secondCategoryId");
		secondCategory.setName("secondCategoryName");

		CategoryDto thirdCategory = new CategoryDto();
		thirdCategory.setId("thirdCategoryId");
		thirdCategory.setName("thirdCategoryName");

		foodCreateDto.setCategories(new HashSet<>(Set.of(secondCategory)));

		FoodDto result = this.restaurantsFoodsServiceImpl.editFood("restaurantId", "foodId", foodCreateDto);

		assertEquals(result.getId(), food.getId());
		assertEquals(result.getName(), food.getName());
		assertEquals(result.getDescription(), food.getDescription());
		assertEquals(result.getPrice(), food.getPrice().doubleValue());
		assertTrue(food.getCategories().stream()
				.allMatch(c -> result.getCategories().stream().anyMatch(rc -> rc.getId().equals(c.getId()))));
	}

	private static Stream<Arguments> categoriesMethodSource() {
		Category category = new Category();
		category.setId("firstCategoryId");
		category.setName("firstCategory");

		Category category2 = new Category();
		category2.setId("secondCategoryId");
		category2.setName("secondCategoryName");

		Category category3 = new Category();
		category3.setId("thirdCategoryId");
		category3.setName("thirdCategoryName");

		FoodCreateDto foodCreateDto = new FoodCreateDto();
		foodCreateDto.setName("editName");
		foodCreateDto.setDescription("editDescription");
		foodCreateDto.setPrice(BigDecimal.valueOf(30));

		CategoryDto firstCategory = new CategoryDto();
		firstCategory.setId("firstCategoryId");
		firstCategory.setName("firstCategory");

		CategoryDto secondCategory = new CategoryDto();
		secondCategory.setId("secondCategoryId");
		secondCategory.setName("secondCategoryName");

		CategoryDto thirdCategory = new CategoryDto();
		thirdCategory.setId("thirdCategoryId");
		thirdCategory.setName("thirdCategoryName");

		final Set<Category> firstCaseInitialCategories = Set.of(category, category2);
		final Set<Category> secondCaseInitialCategories = Set.of(category, category2, category3);
		final Set<Category> thirdCaseInitialCategories = Set.of(category);
		final Set<Category> fourthCaseInitialCategories = Set.of(category, category3);
		final Set<Category> fifthCaseInitialCategories = Set.of(category2);

		final Set<Category> returnedFromRepositoryCategories = Set.of(category, category2, category3);

		final Set<CategoryDto> firstCaseEdittedCategories = Set.of(firstCategory, secondCategory, thirdCategory);
		final Set<CategoryDto> secondCaseEdittedCategories = Set.of(firstCategory, secondCategory);
		final Set<CategoryDto> thirdCaseEdittedCategories = Set.of(secondCategory);
		final Set<CategoryDto> fourthCaseEdittedCategories = Set.of(secondCategory, firstCategory);
		final Set<CategoryDto> fifthCaseEdittedCategories = Set.of(secondCategory, firstCategory, thirdCategory);

		final Set<Category> firstCaseResultCategories = Set.of(category, category2, category3);
		final Set<Category> secondCaseResultCategories = Set.of(category, category2);
		final Set<Category> thirdCaseResultCategories = Set.of(category2);
		final Set<Category> fourthCaseResultCategories = Set.of(category2, category);
		final Set<Category> fifthCaseResultCategories = Set.of(category2, category, category3);

		return Stream.of(
				Arguments.of(firstCaseInitialCategories, returnedFromRepositoryCategories, firstCaseEdittedCategories,
						firstCaseResultCategories),
				Arguments.of(secondCaseInitialCategories, returnedFromRepositoryCategories,
						secondCaseEdittedCategories, secondCaseResultCategories),
				Arguments.of(thirdCaseInitialCategories, returnedFromRepositoryCategories, thirdCaseEdittedCategories,
						thirdCaseResultCategories),
				Arguments.of(fourthCaseInitialCategories, returnedFromRepositoryCategories, fourthCaseEdittedCategories,
						fourthCaseResultCategories),
				Arguments.of(fifthCaseInitialCategories, returnedFromRepositoryCategories, fifthCaseEdittedCategories,
						fifthCaseResultCategories));
	}
}
