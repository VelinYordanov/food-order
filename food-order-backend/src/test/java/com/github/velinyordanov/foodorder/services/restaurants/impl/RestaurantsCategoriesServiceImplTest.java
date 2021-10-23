package com.github.velinyordanov.foodorder.services.restaurants.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.github.velinyordanov.foodorder.data.CategoriesRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NonEmptyCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantsCategoriesServiceImplTest {
	@Mock
	private FoodOrderData foodOrderData;

	@Mock
	private CategoriesRepository categoriesRepository;

	@Mock
	private RestaurantsRepository restaurantsRepository;

	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());

	@InjectMocks
	private RestaurantsCategoriesServiceImpl restaurantCategoriesService;

	@Test
	public void getCategoriesForRestaurantShould_ReturnRestaurantCategories() {
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		Category firstCategory = new Category();
		firstCategory.setId("firstCategoryId");
		firstCategory.setName("firstCategory");

		Category secondCategory = new Category();
		secondCategory.setId("secondCategoryId");
		secondCategory.setName("secondCategory");

		Category thirdCategory = new Category();
		thirdCategory.setId("thirdCategoryId");
		thirdCategory.setName("thirdCategory");

		Collection<Category> categories = List.of(firstCategory, secondCategory, thirdCategory);
		given(this.categoriesRepository.findByRestaurantId("restaurantId")).willReturn(categories);

		Collection<CategoryDto> actual = this.restaurantCategoriesService.getCategoriesForRestaurant("restaurantId");

		then(this.categoriesRepository).should(times(1)).findByRestaurantId("restaurantId");
		then(this.categoriesRepository).shouldHaveNoMoreInteractions();

		Collection<CategoryDto> expected = categories.stream()
				.map(category -> {
					CategoryDto categoryDto = new CategoryDto();
					categoryDto.setId(category.getId());
					categoryDto.setName(category.getName());

					return categoryDto;
				})
				.collect(Collectors.toList());

		assertEquals(expected, actual);
	}

	@Test
	public void deleteCategoryShould_ThrowNotFoundException_WhenCategoryIsNotFound() {
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);
		given(this.categoriesRepository.findById(anyString())).willReturn(Optional.empty());

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantCategoriesService.deleteCategory("restaurantId", "categoryId"));

		assertEquals("Cateogry with id categoryId not found!", exc.getMessage());
	}

	@Test
	public void deleteCategoryShould_ThrowNotFoundException_WhenCategoryDoesNotBelongToRestaurant() {
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		Category category = new Category();
		Restaurant restaurant = new Restaurant();
		restaurant.setId("notRestaurantId");
		category.setRestaurant(restaurant);
		given(this.categoriesRepository.findById(anyString())).willReturn(Optional.of(category));

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantCategoriesService.deleteCategory("restaurantId", "categoryId"));

		assertEquals("Cateogry with id categoryId not found!", exc.getMessage());
	}

	@Test
	public void deleteCategoryShould_ThrowNonEmptyCategoryException_WhenCategoryHasFoodsThatAreNotDeleted() {
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		Category category = new Category();
		category.setName("categoryName");
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		category.setRestaurant(restaurant);

		Food food = new Food();
		category.setFoods(Set.of(food));
		given(this.categoriesRepository.findById(anyString())).willReturn(Optional.of(category));

		NonEmptyCategoryException exc = assertThrows(NonEmptyCategoryException.class,
				() -> this.restaurantCategoriesService.deleteCategory("restaurantId", "categoryId"));

		assertEquals("Category categoryName has foods associated with it!", exc.getMessage());
	}

	@Test
	public void deleteCategoryShould_DeleteCategory_WhenCategoryHasOnlyDeletedFoods() {
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		Category category = new Category();
		category.setName("categoryName");
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		category.setRestaurant(restaurant);

		Food food = new Food();
		food.setIsDeleted(true);
		category.setFoods(Set.of(food));
		given(this.categoriesRepository.findById(anyString())).willReturn(Optional.of(category));

		this.restaurantCategoriesService.deleteCategory("restaurantId", "categoryId");

		then(this.categoriesRepository).should(times(1)).delete(category);
		then(this.categoriesRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	public void deleteCategoryShould_DeleteCategory_WhenAllValidationsPass() {
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);

		Category category = new Category();
		category.setName("categoryName");
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		category.setRestaurant(restaurant);

		given(this.categoriesRepository.findById(anyString())).willReturn(Optional.of(category));

		this.restaurantCategoriesService.deleteCategory("restaurantId", "categoryId");

		then(this.categoriesRepository).should(times(1)).delete(category);
		then(this.categoriesRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	public void addCategoryShould_ThrowNotFoundException_WhenRestaurantIsNotFound() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.restaurantsRepository.findById(anyString())).willReturn(Optional.empty());

		CategoryCreateDto categoryDto = new CategoryCreateDto();
		categoryDto.setName("categoryName");
		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantCategoriesService.addCategoryForRestaurant("restaurantId", categoryDto));

		assertEquals("Restaurant with id restaurantId not found", exc.getMessage());
	}
	
	@Test
	public void addCategoryShould_ThrowDuplicateCategoryException_WhenCategoryAlreadyExistsAndIsNotDeleted() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);
		
		Restaurant restaurant = new Restaurant();
		given(this.restaurantsRepository.findById(anyString())).willReturn(Optional.of(restaurant));
		
		Category category = new Category();
		category.setName("categoryName");
		category.setIsDeleted(false);
		given(this.categoriesRepository.findByRestaurantAndNameIncludingDeleted("restaurantId", "categoryName")).willReturn(Optional.of(category));

		CategoryCreateDto categoryDto = new CategoryCreateDto();
		categoryDto.setName("categoryName");
		DuplicateCategoryException exc = assertThrows(DuplicateCategoryException.class,
				() -> this.restaurantCategoriesService.addCategoryForRestaurant("restaurantId", categoryDto));

		assertEquals("Category categoryName already exists", exc.getMessage());
	}
	
	@Test
	public void addCategoryShould_SetIsDeletedToFalseAndUpdateDatabase_WhenCategoryExistsButIsDeleted() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);
		
		Restaurant restaurant = new Restaurant();
		given(this.restaurantsRepository.findById(anyString())).willReturn(Optional.of(restaurant));
		
		Category category = new Category();
		category.setName("categoryName");
		category.setId("categoryId");
		category.setIsDeleted(true);
		given(this.categoriesRepository.save(category)).willAnswer(answer -> answer.getArgument(0));
		given(this.categoriesRepository.findByRestaurantAndNameIncludingDeleted("restaurantId", "categoryName")).willReturn(Optional.of(category));

		CategoryCreateDto categoryDto = new CategoryCreateDto();
		categoryDto.setName("categoryName");
		CategoryDto result = this.restaurantCategoriesService.addCategoryForRestaurant("restaurantId", categoryDto);

		assertFalse(category.getIsDeleted());
		then(this.categoriesRepository).should(times(1)).save(category);
		then(this.categoriesRepository).shouldHaveNoMoreInteractions();
		assertEquals("categoryId", result.getId());
		assertEquals("categoryName", result.getName());
	}
	
	@Test
	public void addCategoryShould_CreateANewCategory_WhenCategoryDoesNotExistForRestaurant() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.categories()).willReturn(this.categoriesRepository);
		
		Restaurant restaurant = new Restaurant();
		given(this.restaurantsRepository.findById(anyString())).willReturn(Optional.of(restaurant));
		
		given(this.categoriesRepository.save(any(Category.class))).willAnswer(answer -> answer.getArgument(0));
		given(this.categoriesRepository.findByRestaurantAndNameIncludingDeleted("restaurantId", "categoryName")).willReturn(Optional.empty());

		CategoryCreateDto categoryDto = new CategoryCreateDto();
		categoryDto.setName("categoryName");
		CategoryDto result = this.restaurantCategoriesService.addCategoryForRestaurant("restaurantId", categoryDto);

		ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
		then(this.categoriesRepository).should(times(1)).save(categoryCaptor.capture());
		then(this.categoriesRepository).shouldHaveNoMoreInteractions();
		
		Category capturedCategory = categoryCaptor.getValue();
		assertEquals("categoryName", capturedCategory.getName());
		assertEquals(restaurant, capturedCategory.getRestaurant());
		assertFalse(capturedCategory.getIsDeleted());
		assertTrue(capturedCategory.getFoods().isEmpty());
		
		assertEquals("categoryName", result.getName());
	}
}
