package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.dto.GraphData;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderStatusDto;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.DateService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsOrdersService;

@Service
public class RestaurantsOrdersServiceImpl implements RestaurantsOrdersService {
    private final FoodOrderData foodOrderData;
    private final Mapper mapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final DateService dateService;

    public RestaurantsOrdersServiceImpl(
	    FoodOrderData foodOrderData,
	    Mapper mapper,
	    SimpMessagingTemplate messagingTemplate,
	    DateService dateService) {
	this.foodOrderData = foodOrderData;
	this.mapper = mapper;
	this.messagingTemplate = messagingTemplate;
	this.dateService = dateService;
    }

    @Override
    public Page<OrderDto> getRestaurantOrders(String restaurantId, Pageable pageable) {
	return this.foodOrderData.orders()
		.findByRestaurantId(restaurantId, pageable)
		.map(order -> this.mapper.map(order, OrderDto.class));
    }

    @Override
    public OrderDto getRestaurantOrder(String restaurantId, String orderId) {
	Order order = this.foodOrderData.orders()
		.findById(orderId)
		.orElseThrow(() -> new NotFoundException("Order not found!"));

	if (!restaurantId.equals(order.getRestaurant().getId())) {
	    throw new NotFoundException("No such order found for restaurant.");
	}

	return this.mapper.map(order, OrderDto.class);
    }

    @Override
    public OrderStatusDto updateRestaurantOrderStatus(
	    String restaurantId,
	    String orderId,
	    OrderStatusDto orderStatusDto) {
	Order order = this.foodOrderData.orders()
		.findById(orderId)
		.filter(restaurantOrder -> restaurantId.equals(restaurantOrder.getRestaurant().getId()))
		.orElseThrow(() -> new BadRequestException("No such order found for restaurant!"));

	order.setStatus(orderStatusDto.getStatus());

	OrderStatusDto result = new OrderStatusDto(this.foodOrderData.orders().save(order).getStatus());

	this.messagingTemplate.convertAndSend(
		MessageFormat.format(
			"/notifications/customers/{0}/orders/{1}",
			order.getCustomer().getId(),
			order.getId()),
		result);

	return result;
    }

    @Override
    public Collection<GraphData<LocalDate, Long>> getOrderMonthlyGraphData(String restaurantId, int year, int month) {
	Collection<GraphData<LocalDate, Long>> result = this.foodOrderData.orders()
		.getOrderMonthlyGraphData(restaurantId, month, year)
		.stream()
		.map(graphData -> new GraphData<LocalDate, Long>(
			graphData.getX().toLocalDate(),
			graphData.getY()))
		.collect(Collectors.toList());

	int daysForMonth = this.dateService.getNumberOfDaysForMonth(year, month);

	for (int i = 1; i <= daysForMonth; i++) {
	    LocalDate date = LocalDate.of(year, month, i);
	    if (result.stream()
		    .filter(graphData -> date.isEqual(graphData.getX()))
		    .findFirst()
		    .isEmpty()) {
		result.add(new GraphData<LocalDate, Long>(date, 0l));
	    }
	}

	return result.stream()
		.sorted(Comparator.comparing(GraphData::getX))
		.collect(Collectors.toList());
    }

    @Override
    public Collection<GraphData<String, Long>> getYearlyGraphData(String restaurantId, int year) {
	Collection<GraphData<Integer, Long>> result = this.foodOrderData.orders()
		.getYearlyGraphData(restaurantId, year);

	for (int i = 1; i <= 12; i++) {
	    int current = i;
	    if (result.stream().filter(graphData -> current == graphData.getX()).findAny().isEmpty()) {
		result.add(new GraphData<Integer, Long>(i, 0l));
	    }
	}

	return result
		.stream()
		.sorted(Comparator.comparing(GraphData::getX))
		.map(graphData -> new GraphData<String, Long>(this.dateService.getMonthName(graphData.getX()),
			graphData.getY()))
		.collect(Collectors.toList());
    }
}
