package com.github.velinyordanov.foodorder.mapping.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.OrderFood;
import com.github.velinyordanov.foodorder.dto.FoodOrderDto;

public class OrderFoodToFoodOrderDtoConverterTest {
	private OrderFoodToFoodOrderDtoConverter orderFoodToFoodOrderDtoConverter = new OrderFoodToFoodOrderDtoConverter();
	
	@Test
	public void convertShould_ReturnCorrectlyMappedResult() {
		Food food = new Food();
		food.setId("foodId");
		food.setName("name");
		food.setDescription("description");
		food.setPrice(BigDecimal.valueOf(15));
		
		OrderFood orderFood = new OrderFood();
		orderFood.setQuantity(3);
		orderFood.setFood(food);
		
		FoodOrderDto foodOrderDto = this.orderFoodToFoodOrderDtoConverter.convert(orderFood);
		
		assertEquals(food.getId(), foodOrderDto.getId());
		assertEquals(food.getName(), foodOrderDto.getName());
		assertEquals(food.getDescription(), foodOrderDto.getDescription());
		assertEquals(food.getPrice(), foodOrderDto.getPrice());
		assertEquals(orderFood.getQuantity(), foodOrderDto.getQuantity());
	}
}
