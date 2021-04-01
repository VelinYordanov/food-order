package com.github.velinyordanov.foodorder.controllers.restaurants;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.GraphData;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderStatusDto;
import com.github.velinyordanov.foodorder.services.DateService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsOrdersService;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@RestController
@RequestMapping("restaurants/{restaurantId}/orders")
@PreAuthorize(ValidationConstraints.ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
public class RestaurantsOrdersController {
	private static final int DEFAULT_PAGE_SIZE = 15;

	private final RestaurantsOrdersService restaurantsOrdersService;
	private final DateService dateService;

	public RestaurantsOrdersController(RestaurantsOrdersService restaurantsOrdersService, DateService dateService) {
		this.restaurantsOrdersService = restaurantsOrdersService;
		this.dateService = dateService;
	}

	@GetMapping()
	public Page<OrderDto> getRestaurantOrders(@PathVariable String restaurantId,
			@RequestParam("page") Optional<Integer> pageOptional) {
		int page = pageOptional.filter(selectedPage -> selectedPage >= 0).orElse(0);

		Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("createdOn").descending());

		return this.restaurantsOrdersService.getRestaurantOrders(restaurantId, pageable);
	}

	@GetMapping("{orderId}")
	public OrderDto getRestaurantOrder(@PathVariable String restaurantId, @PathVariable String orderId) {
		return this.restaurantsOrdersService.getRestaurantOrder(restaurantId, orderId);
	}

	@PatchMapping("{orderId}")
	public OrderStatusDto patchRestaurantOrder(@PathVariable String restaurantId, @PathVariable String orderId,
			@RequestBody @Valid OrderStatusDto orderStatusDto) {
		return this.restaurantsOrdersService.updateRestaurantOrderStatus(restaurantId, orderId, orderStatusDto);
	}

	@GetMapping("monthly-graph")
	public Collection<GraphData<LocalDate, Long>> getOrdersMonthlyGraphData(@PathVariable String restaurantId,
			@RequestParam("year") Optional<Integer> yearOptional,
			@RequestParam("month") Optional<Integer> monthOptional) {
		LocalDate now = this.dateService.now();

		int year = yearOptional.filter(requestedYear -> requestedYear > 0).orElse(now.getYear());

		int month = monthOptional.filter(requestedMonth -> requestedMonth > 0 && requestedMonth <= 12)
				.orElse(now.getMonthValue());

		return this.restaurantsOrdersService.getOrderMonthlyGraphData(restaurantId, year, month);
	}

	@GetMapping("yearly-graph")
	public Collection<GraphData<String, Long>> getOrdersYearlyGraphData(@PathVariable String restaurantId,
			@RequestParam("year") Optional<Integer> yearOptional) {
		LocalDate now = this.dateService.now();

		int year = yearOptional.filter(requestedYear -> requestedYear > 0).orElse(now.getYear());

		return this.restaurantsOrdersService.getYearlyGraphData(restaurantId, year);
	}
}
