package com.github.velinyordanov.foodorder.mapping.impl;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.velinyordanov.foodorder.data.entities.OrderFood;
import com.github.velinyordanov.foodorder.dto.FoodOrderDto;

@Component
public class OrderFoodToFoodOrderDtoConverter extends AbstractConverter<OrderFood, FoodOrderDto> {

	@Override
	protected FoodOrderDto convert(OrderFood source) {
		FoodOrderDto foodOrderDto = new FoodOrderDto();
		foodOrderDto.setDescription(source.getFood().getDescription());
		foodOrderDto.setId(source.getFood().getId());
		foodOrderDto.setName(source.getFood().getName());
		foodOrderDto.setPrice(source.getFood().getPrice());
		foodOrderDto.setQuantity(source.getQuantity());

		return foodOrderDto;
	}
}
