package com.github.velinyordanov.foodorder.controllers.restaurants;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeEditDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeListDto;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsDiscountCodesService;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@RestController
@RequestMapping("{restaurantId}/discount-codes")
@PreAuthorize(ValidationConstraints.ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
public class RestaurantsDiscountCodesController {
    private final RestaurantsDiscountCodesService restaurantsDiscountCodesService;

    public RestaurantsDiscountCodesController(RestaurantsDiscountCodesService restaurantsDiscountCodesService) {
	this.restaurantsDiscountCodesService = restaurantsDiscountCodesService;
    }

    @PostMapping("{restaurantId}/discount-codes")
    public DiscountCodeDto addDiscountCodeToRestaurant(@PathVariable String restaurantId,
	    @Valid @RequestBody DiscountCodeCreateDto discountCode) {
	return this.restaurantsDiscountCodesService.addDiscountCodeToRestaurant(restaurantId, discountCode);
    }

    @GetMapping("{restaurantId}/discount-codes")
    public Collection<DiscountCodeListDto> getDiscountCodesForRestaurant(@PathVariable String restaurantId) {
	return this.restaurantsDiscountCodesService.getDiscountCodesForRestaurant(restaurantId);
    }

    @GetMapping("{restaurantId}/discount-codes/{code}")
    public DiscountCodeDto getDiscountCode(
	    @PathVariable String restaurantId,
	    @PathVariable String code,
	    @AuthenticationPrincipal Customer customer) {
	return this.restaurantsDiscountCodesService.getDiscountByCode(restaurantId, code, customer.getId());
    }

    @DeleteMapping("{restaurantId}/discount-codes/{discountCodeId}")
    public DiscountCodeDto deleteDiscountCode(
	    @PathVariable String restaurantId,
	    @PathVariable String discountCodeId) {
	return this.restaurantsDiscountCodesService.deleteDiscountCode(restaurantId, discountCodeId);
    }

    @PutMapping("{restaurantId}/discount-codes/{discountCodeId}")
    public DiscountCodeListDto editDiscountCode(
	    @PathVariable String restaurantId,
	    @PathVariable String discountCodeId,
	    @RequestBody @Valid DiscountCodeEditDto discountCodeEditDto) {
	return this.restaurantsDiscountCodesService.editDiscountCode(restaurantId, discountCodeId, discountCodeEditDto);
    }
}
