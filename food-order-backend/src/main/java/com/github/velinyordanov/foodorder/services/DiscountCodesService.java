package com.github.velinyordanov.foodorder.services;

import com.github.velinyordanov.foodorder.data.entities.DiscountCode;

public interface DiscountCodesService {
	void validateDiscountCode(DiscountCode discountCode, String customerId);
}
