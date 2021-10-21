package com.github.velinyordanov.foodorder.mapping.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderFoodDto;

public class OrderCreateDtoToOrderConverterTest {
	private OrderCreateDtoToOrderConverter orderCreateDtoToOrderConverter = new OrderCreateDtoToOrderConverter();

	@Test
	public void convertShould_ReturnCorrectlyMappedResult_whenDiscountCodeIdIsPresent() {
		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(1);
		OrderFoodDto orderFoodDto2 = new OrderFoodDto();
		orderFoodDto2.setId("food2Id");
		orderFoodDto2.setQuantity(3);
		OrderFoodDto orderFoodDto3 = new OrderFoodDto();
		orderFoodDto3.setId("food3Id");
		orderFoodDto3.setQuantity(5);
		orderCreateDto.setFoods(Set.of(orderFoodDto, orderFoodDto2, orderFoodDto3));

		Order order = this.orderCreateDtoToOrderConverter.convert(orderCreateDto);

		assertEquals(orderCreateDto.getCustomerId(), order.getCustomer().getId());
		assertEquals(orderCreateDto.getRestaurantId(), order.getRestaurant().getId());
		assertEquals(orderCreateDto.getAddressId(), order.getAddress().getId());
		assertEquals(orderCreateDto.getComment(), order.getComment());
		assertEquals(orderCreateDto.getDiscountCodeId(), order.getDiscountCode().getId());
		assertThat(order.getFoods())
				.hasSize(3)
				.allMatch(orderFood -> orderCreateDto.getFoods()
						.stream()
						.filter(of -> {
							return of.getId().equals(orderFood.getFood().getId())
									&& of.getQuantity() == orderFood.getQuantity();
						})
						.findAny()
						.isPresent());
	}
	
	@Test
	public void convertShould_ReturnCorrectlyMappedResult_whenDiscountCodeIdIsNotPresent() {
		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(1);
		OrderFoodDto orderFoodDto2 = new OrderFoodDto();
		orderFoodDto2.setId("food2Id");
		orderFoodDto2.setQuantity(3);
		OrderFoodDto orderFoodDto3 = new OrderFoodDto();
		orderFoodDto3.setId("food3Id");
		orderFoodDto3.setQuantity(5);
		orderCreateDto.setFoods(Set.of(orderFoodDto, orderFoodDto2, orderFoodDto3));

		Order order = this.orderCreateDtoToOrderConverter.convert(orderCreateDto);

		assertEquals(orderCreateDto.getCustomerId(), order.getCustomer().getId());
		assertEquals(orderCreateDto.getRestaurantId(), order.getRestaurant().getId());
		assertEquals(orderCreateDto.getAddressId(), order.getAddress().getId());
		assertEquals(orderCreateDto.getComment(), order.getComment());
		assertNull(order.getDiscountCode());
		assertThat(order.getFoods())
				.hasSize(3)
				.allMatch(orderFood -> orderCreateDto.getFoods()
						.stream()
						.filter(of -> {
							return of.getId().equals(orderFood.getFood().getId())
									&& of.getQuantity() == orderFood.getQuantity();
						})
						.findAny()
						.isPresent());
	}
}
