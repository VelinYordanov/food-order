package com.github.velinyordanov.foodorder.mapping.impl;

import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.data.entities.OrderFood;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;

@Component
public class OrderCreateDtoToOrderConverter extends AbstractConverter<OrderCreateDto, Order> {
    @Override
    protected Order convert(OrderCreateDto source) {
	Order result = new Order();
	Address customerAddress = new Address();
	customerAddress.setId(source.getAddressId());
	result.setAddress(customerAddress);

	Customer customer = new Customer();
	customer.setId(source.getCustomerId());
	result.setCustomer(customer);

	Restaurant restaurant = new Restaurant();
	restaurant.setId(source.getRestaurantId());
	result.setRestaurant(restaurant);

	result
		.setFoods(source.getFoods()
			.stream()
			.map(foodDto -> {
			    Food food = new Food();
			    food.setId(foodDto.getId());
			    OrderFood orderFood = new OrderFood();
			    orderFood.setFood(food);
			    orderFood.setOrder(result);
			    return orderFood;
			})
			.collect(Collectors.toSet()));

	return result;
    }

}
