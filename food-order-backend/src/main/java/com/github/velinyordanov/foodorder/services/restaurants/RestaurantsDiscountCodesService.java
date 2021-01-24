package com.github.velinyordanov.foodorder.services.restaurants;

import java.util.Collection;

import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeEditDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeListDto;

public interface RestaurantsDiscountCodesService {
    DiscountCodeDto addDiscountCodeToRestaurant(String restaurantId, DiscountCodeCreateDto discountCode);

    DiscountCodeDto getDiscountByCode(String restaurantId, String code, String customerId);

    Collection<DiscountCodeListDto> getDiscountCodesForRestaurant(String restaurantId);

    DiscountCodeDto deleteDiscountCode(String restaurantId, String discountCodeId);

    DiscountCodeListDto editDiscountCode(String restaurantId, String discountCodeId, DiscountCodeEditDto discountCode);
}
