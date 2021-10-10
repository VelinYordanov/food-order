package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeEditDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeListDto;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.ExistingDiscountCodeException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsDiscountCodesService;

@Service
public class RestaurantsDiscountCodesServiceImpl implements RestaurantsDiscountCodesService {
	private final FoodOrderData foodOrderData;
	private final Mapper mapper;
	private final DiscountCodesService discountCodesService;

	public RestaurantsDiscountCodesServiceImpl(FoodOrderData foodOrderData, Mapper mapper,
			DiscountCodesService discountCodesService) {
		this.foodOrderData = foodOrderData;
		this.mapper = mapper;
		this.discountCodesService = discountCodesService;
	}

	@Override
	public DiscountCodeDto addDiscountCodeToRestaurant(String restaurantId, DiscountCodeCreateDto discountCode) {
		Restaurant restaurant = this.foodOrderData.restaurants()
				.findById(restaurantId)
				.orElseThrow(() -> new NotFoundException("Restaurant not found"));

		discountCode.setCode(discountCode.getCode().toUpperCase());
		this.foodOrderData.discountCodes()
				.findByCodeAndRestaurantIdWithDeleted(restaurantId, discountCode.getCode())
				.ifPresent(code -> {
					throw new ExistingDiscountCodeException(
							MessageFormat.format("Discount code {0} already exists for restaurant {1}", code.getCode(),
									code.getRestaurant().getName()));
				});

		DiscountCode discountCodeToBeAdded = this.mapper.map(discountCode, DiscountCode.class);
		discountCodeToBeAdded.setRestaurant(restaurant);

		return this.mapper.map(this.foodOrderData.discountCodes().save(discountCodeToBeAdded), DiscountCodeDto.class);
	}

	@Override
	public DiscountCodeDto getDiscountByCode(String restaurantId, String code, String customerId) {
		DiscountCode discountCode = this.foodOrderData.discountCodes()
				.findByCodeAndRestaurantId(restaurantId, code)
				.orElseThrow(() -> new NotFoundException("Discount code not found"));

		this.discountCodesService.validateDiscountCode(discountCode, customerId);

		return this.mapper.map(discountCode, DiscountCodeDto.class);
	}

	@Override
	public Collection<DiscountCodeListDto> getDiscountCodesForRestaurant(String restaurantId) {
		return this.foodOrderData.discountCodes()
				.findByRestaurant(restaurantId)
				.stream()
				.map(discountCode -> {
					DiscountCodeListDto discountCodeListDto = this.mapper.map(discountCode, DiscountCodeListDto.class);
					discountCodeListDto.setTimesUsed(discountCode.getOrders().size());
					return discountCodeListDto;
				}).collect(Collectors.toList());
	}

	@Override
	public DiscountCodeDto deleteDiscountCode(String restaurantId, String discountCodeId) {
		DiscountCode code = this.foodOrderData.discountCodes()
				.findByIdAndRestaurant(discountCodeId, restaurantId)
				.orElseThrow(() -> new NotFoundException("Discount code not found"));

		if (!code.getOrders().isEmpty()) {
			throw new BadRequestException("Discount code has orders associated with it and cannot be deleted.");
		}

		this.foodOrderData.discountCodes().delete(code);

		return this.mapper.map(code, DiscountCodeDto.class);
	}

	@Override
	public DiscountCodeListDto editDiscountCode(String restaurantId, String discountCodeId,
			DiscountCodeEditDto discountCode) {
		DiscountCode code = this.foodOrderData.discountCodes()
				.findByIdAndRestaurant(discountCodeId, restaurantId)
				.orElseThrow(() -> new NotFoundException("Discount code not found"));

		if (!code.getOrders().isEmpty()) {
			if (discountCode.getDiscountPercentage() != code.getDiscountPercentage()) {
				throw new BadRequestException(
						"Discount percentage can be changed only to discount codes that have not been used yet.");
			}
			
			if (!discountCode.getValidFrom().isEqual(code.getValidFrom())) {
				throw new BadRequestException(
						"Valid from can only be changed for orders that have not been used yet.");
			}

			if (!discountCode.getIsSingleUse() && code.getOrders().size() > 1) {
				throw new BadRequestException("Cannot disable single use as the discount code has been used.");
			}

			boolean userHasMoreThan1OrdersWithDiscountCode = code.getOrders()
					.stream()
					.collect(Collectors.groupingBy(x -> x.getCustomer().getId(), Collectors.counting()))
					.values()
					.stream()
					.filter(x -> x > 1)
					.findAny()
					.isPresent();

			if (discountCode.getIsOncePerUser() && userHasMoreThan1OrdersWithDiscountCode) {
				throw new BadRequestException(
						"Cannot enable once per user as users have used the discount code more than once already");
			}
		}

		this.mapper.map(discountCode, code);

		DiscountCode savedDiscountCode = this.foodOrderData.discountCodes().save(code);

		DiscountCodeListDto result = this.mapper.map(savedDiscountCode, DiscountCodeListDto.class);
		result.setTimesUsed(savedDiscountCode.getOrders().size());

		return result;
	}
}
