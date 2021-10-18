package com.github.velinyordanov.foodorder.services.impl;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.services.DateService;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;

@Service
public class DiscountCodesServiceImpl implements DiscountCodesService {
	private final DateService dateService;

	public DiscountCodesServiceImpl(DateService dateService) {
		this.dateService = dateService;
	}

	@Override
	public void validateDiscountCode(DiscountCode discountCode, String customerId) {
		if (this.dateService.isInTheFuture(discountCode.getValidFrom())) {
			throw new BadRequestException(MessageFormat
					.format("Discount code {0} is not available yet. Try again later.", discountCode.getCode()));
		}

		if (this.dateService.isInThePast(discountCode.getValidTo())) {
			throw new BadRequestException(
					MessageFormat.format("Discount code {0} is expired.", discountCode.getCode()));
		}

		if (discountCode.getIsSingleUse() && !discountCode.getOrders().isEmpty()) {
			throw new BadRequestException(
					MessageFormat.format("Discount code {0} was already used.", discountCode.getCode()));
		}

		boolean hasCustomerUsedThisCodeBefore = discountCode.getOrders()
				.stream()
				.map(order -> order.getCustomer().getId())
				.filter(id -> id.equals(customerId))
				.findFirst()
				.isPresent();

		if (discountCode.getIsOncePerUser() && hasCustomerUsedThisCodeBefore) {
			throw new BadRequestException(
					MessageFormat.format("You have already used code {0}.", discountCode.getCode()));
		}
	}
}
