package com.github.velinyordanov.foodorder.services.customers.impl;

import java.text.MessageFormat;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;
import com.github.velinyordanov.foodorder.services.customers.CustomersOrdersService;

@Service
public class CustomersOrdersServiceImpl implements CustomersOrdersService {
	private final Mapper mapper;
	private final FoodOrderData foodOrderData;
	private final SimpMessagingTemplate messagingTemplate;
	private final DiscountCodesService discountCodesService;

	public CustomersOrdersServiceImpl(Mapper mapper, FoodOrderData foodOrderData,
			SimpMessagingTemplate messagingTemplate, DiscountCodesService discountCodesService) {
		this.mapper = mapper;
		this.foodOrderData = foodOrderData;
		this.messagingTemplate = messagingTemplate;
		this.discountCodesService = discountCodesService;
	}

	@Override
	public OrderDto addOrderToCustomer(String customerId, OrderCreateDto order) {
		if (!customerId.equals(order.getCustomerId())) {
			throw new BadRequestException("Customer is not valid");
		}

		Address address = this.foodOrderData.addresses().findById(order.getAddressId())
				.orElseThrow(() -> new NotFoundException("No such address found"));

		if (!order.getCustomerId().equals(address.getCustomer().getId())) {
			throw new NotFoundException("No such address found");
		}

		this.foodOrderData.restaurants().findById(order.getRestaurantId())
				.orElseThrow(() -> new NotFoundException("No such restaurant found"));

		Collection<Food> restaurantFoods = this.foodOrderData.foods().findByRestaurantId(order.getRestaurantId());

		if (!order.getFoods().stream()
				.allMatch(food -> restaurantFoods.stream().anyMatch(f -> f.getId().equals(food.getId())))) {
			throw new NotFoundException("No such food found");
		}

		Order orderToAdd = this.mapper.map(order, Order.class);

		if (order.getDiscountCodeId() != null) {
			DiscountCode discountCode = this.foodOrderData.discountCodes().findById(order.getDiscountCodeId())
					.orElseThrow(() -> new NotFoundException("Discount code not found"));

			this.discountCodesService.validateDiscountCode(discountCode, customerId);

			orderToAdd.setDiscountCode(discountCode);
		}

		OrderDto result = this.mapper.map(this.foodOrderData.orders().save(orderToAdd), OrderDto.class);
		this.messagingTemplate.convertAndSend(
				MessageFormat.format("/notifications/restaurants/{0}/orders", result.getRestaurant().getId()), result);
		return result;
	}

	@Override
	public Page<OrderDto> getCustomerOrders(String customerId, Pageable pageable) {
		return this.foodOrderData.orders().findByCustomerId(customerId, pageable)
				.map(order -> this.mapper.map(order, OrderDto.class));
	}

	@Override
	public OrderDto getCustomerOrder(String customerId, String orderId) {
		Order order = this.foodOrderData.orders().findById(orderId)
				.orElseThrow(() -> new NotFoundException("Order not found"));

		if (!customerId.equals(order.getCustomer().getId())) {
			throw new NotFoundException("Order not found for customer");
		}

		return this.mapper.map(order, OrderDto.class);
	}
}
