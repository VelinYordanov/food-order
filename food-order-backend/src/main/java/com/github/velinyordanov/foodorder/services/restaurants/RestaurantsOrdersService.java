package com.github.velinyordanov.foodorder.services.restaurants;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.velinyordanov.foodorder.dto.GraphData;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderStatusDto;

public interface RestaurantsOrdersService {
    Page<OrderDto> getRestaurantOrders(String restaurantId, Pageable pageable);

    OrderDto getRestaurantOrder(String restaurantId, String orderId);

    OrderStatusDto updateRestaurantOrderStatus(String restaurantId, String orderId, OrderStatusDto orderStatusDto);

    Collection<GraphData<LocalDate, Long>> getOrderMonthlyGraphData(String restaurantId, int year, int month);

    Collection<GraphData<String, Long>> getYearlyGraphData(String restaurantId, int year);
}
