package com.github.velinyordanov.foodorder.controllers;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeEditDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeListDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.dto.GraphData;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderStatusDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.services.DateService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;

@RestController
@RequestMapping("restaurants")
public class RestaurantsController {
    private static final String ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION =
	    "hasAuthority('ROLE_RESTAURANT') and principal.id == #restaurantId";

    private static final int DEFAULT_PAGE_SIZE = 15;

    private final RestaurantsService restaurantsService;
    private final DateService dateService;

    public RestaurantsController(
	    RestaurantsService restaurantsService,
	    DateService dateService) {
	this.restaurantsService = restaurantsService;
	this.dateService = dateService;
    }

    @GetMapping()
    public Collection<RestaurantDto> getAll() {
	return this.restaurantsService.getAll();
    }

    @GetMapping("{restaurantId}")
    public RestaurantDataDto getRestaurantData(@PathVariable String restaurantId) {
	return this.restaurantsService.getRestaurantData(restaurantId);
    }

    @PostMapping()
    public JwtTokenDto register(@Valid @RequestBody RestaurantRegisterDto userDto) {
	return new JwtTokenDto(restaurantsService.register(userDto));
    }

    @PostMapping("tokens")
    public JwtTokenDto login(@Valid @RequestBody UserDto user) {
	return new JwtTokenDto(restaurantsService.login(user));
    }

    @PostMapping("{restaurantId}/foods")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public FoodDto addFoodToRestaurant(@PathVariable String restaurantId,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	return this.restaurantsService.addFoodToRestaurant(restaurantId, foodCreateDto);
    }

    @PutMapping("{restaurantId}/foods/{foodId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public FoodDto editFood(
	    @PathVariable String restaurantId,
	    @PathVariable String foodId,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	return this.restaurantsService.editFood(restaurantId, foodId, foodCreateDto);
    }

    @GetMapping("{restaurantId}/categories")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Collection<CategoryDto> getCategories(@PathVariable String restaurantId) {
	return this.restaurantsService.getCategoriesForRestaurant(restaurantId);
    }

    @PutMapping("{restaurantId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public RestaurantDataDto editRestaurant(
	    @PathVariable String restaurantId,
	    @RequestBody @Valid RestaurantEditDto restaurantEditDto) {
	return this.restaurantsService.editRestaurant(restaurantId, restaurantEditDto);
    }

    @DeleteMapping("{restaurantId}/categories/{categoryId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public void deleteCategoryFromRestaurant(
	    @PathVariable String restaurantId,
	    @PathVariable String categoryId) {
	this.restaurantsService.deleteCategory(restaurantId, categoryId);
    }

    @DeleteMapping("{restaurantId}/foods/{foodId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public void deleteFoodFromRestaurant(
	    @PathVariable String restaurantId,
	    @PathVariable String foodId) {
	this.restaurantsService.deleteFood(restaurantId, foodId);
    }

    @PostMapping("{restaurantId}/categories")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public CategoryDto addCategoryToRestaurant(
	    @PathVariable String restaurantId,
	    @RequestBody @Valid CategoryCreateDto categoryCreateDto) {
	return this.restaurantsService.addCategoryForRestaurant(restaurantId, categoryCreateDto)
		.orElseThrow(() -> new IllegalStateException("An error occurred while creating category"));
    }

    @GetMapping("{restaurantId}/orders")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Page<OrderDto> getRestaurantOrders(
	    @PathVariable String restaurantId,
	    @RequestParam("page") Optional<Integer> pageOptional) {
	int page = pageOptional
		.filter(selectedPage -> selectedPage >= 0)
		.orElse(0);

	Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("createdOn").descending());

	return this.restaurantsService.getRestaurantOrders(restaurantId, pageable);
    }

    @GetMapping("{restaurantId}/orders/{orderId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public OrderDto getRestaurantOrder(@PathVariable String restaurantId, @PathVariable String orderId) {
	return this.restaurantsService.getRestaurantOrder(restaurantId, orderId);
    }

    @PatchMapping("{restaurantId}/orders/{orderId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public OrderStatusDto patchRestaurantOrder(
	    @PathVariable String restaurantId,
	    @PathVariable String orderId,
	    @RequestBody @Valid OrderStatusDto orderStatusDto) {
	return this.restaurantsService.updateRestaurantOrderStatus(restaurantId, orderId, orderStatusDto);
    }

    @PostMapping("{restaurantId}/discount-codes")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public DiscountCodeDto addDiscountCodeToRestaurant(@PathVariable String restaurantId,
	    @Valid @RequestBody DiscountCodeCreateDto discountCode) {
	return this.restaurantsService.addDiscountCodeToRestaurant(restaurantId, discountCode);
    }

    @GetMapping("{restaurantId}/discount-codes")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Collection<DiscountCodeListDto> getDiscountCodesForRestaurant(@PathVariable String restaurantId) {
	return this.restaurantsService.getDiscountCodesForRestaurant(restaurantId);
    }

    @GetMapping("{restaurantId}/discount-codes/{code}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public DiscountCodeDto getDiscountCode(
	    @PathVariable String restaurantId,
	    @PathVariable String code,
	    @AuthenticationPrincipal Customer customer) {
	return this.restaurantsService.getDiscountByCode(restaurantId, code, customer.getId());
    }

    @DeleteMapping("{restaurantId}/discount-codes/{discountCodeId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public DiscountCodeDto deleteDiscountCode(
	    @PathVariable String restaurantId,
	    @PathVariable String discountCodeId) {
	return this.restaurantsService.deleteDiscountCode(restaurantId, discountCodeId);
    }

    @PutMapping("{restaurantId}/discount-codes/{discountCodeId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public DiscountCodeListDto editDiscountCode(
	    @PathVariable String restaurantId,
	    @PathVariable String discountCodeId,
	    @RequestBody @Valid DiscountCodeEditDto discountCodeEditDto) {
	return this.restaurantsService.editDiscountCode(restaurantId, discountCodeId, discountCodeEditDto);
    }

    @GetMapping("{restaurantId}/orders/monthly-graph")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Collection<GraphData<LocalDate, Long>> getOrdersMonthlyGraphData(
	    @PathVariable String restaurantId,
	    @RequestParam("year") Optional<Integer> yearOptional,
	    @RequestParam("month") Optional<Integer> monthOptional) {
	LocalDate now = this.dateService.now();

	int year = yearOptional
		.filter(requestedYear -> requestedYear > 0)
		.orElse(now.getYear());

	int month = monthOptional
		.filter(requestedMonth -> requestedMonth > 0 && requestedMonth <= 12)
		.orElse(now.getMonthValue());

	return this.restaurantsService.getOrderMonthlyGraphData(restaurantId, year, month);
    }

    @GetMapping("{restaurantId}/orders/yearly-graph")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Collection<GraphData<String, Long>> getOrdersYearlyGraphData(
	    @PathVariable String restaurantId,
	    @RequestParam("year") Optional<Integer> yearOptional) {
	LocalDate now = this.dateService.now();

	int year = yearOptional
		.filter(requestedYear -> requestedYear > 0)
		.orElse(now.getYear());

	return this.restaurantsService.getYearlyGraphData(restaurantId, year);
    }

}
