package com.github.velinyordanov.foodorder.services.restaurants.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.OrdersRepository;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.data.entities.Status;
import com.github.velinyordanov.foodorder.dto.GraphData;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderStatusDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;
import com.github.velinyordanov.foodorder.services.impl.DateServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantsOrdersServiceImplTest {
	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private FoodOrderData foodOrderData;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@Mock
	private DateServiceImpl dateService;

	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());

	@InjectMocks
	private RestaurantsOrdersServiceImpl restaurantsOrdersServiceImpl;

	@Test
	public void getRestaurantOrderShould_throwNotFoundException_whenOrderIsNotFound() {
		given(this.ordersRepository.findById("orderId")).willReturn(Optional.empty());
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsOrdersServiceImpl.getRestaurantOrder("restaurantId", "orderId"));
		assertEquals("Order not found!", exc.getMessage());
	}

	@Test
	public void getRestaurantOrderShould_throwNotFoundException_whenOrderDoesNotBelongToRestaurant() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("notRestaurantId");

		Order order = new Order();
		order.setId("orderId");
		order.setRestaurant(restaurant);
		given(this.ordersRepository.findById("orderId")).willReturn(Optional.of(order));
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsOrdersServiceImpl.getRestaurantOrder("restaurantId", "orderId"));
		assertEquals("No such order found for restaurant.", exc.getMessage());
	}

	@Test
	public void getRestaurantOrderShould_returnTheCorrectData_whenOrderBelongsToRestaurant() {
		Restaurant restaurant = new Restaurant();
		restaurant.setName("restaurantName");
		restaurant.setId("restaurantId");

		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setEmail("customer@email.com");
		customer.setName("customerName");
		customer.setPhoneNumber("phoneNumber");

		Address address = new Address();
		address.setCity(City.Sofia);
		address.setAddressType(AddressType.ApartmentBuilding);
		address.setApartmentBuildingNumber("3");
		address.setStreet("street");
		address.setStreetNumber("5");
		address.setNeighborhood("neighborhood");
		address.setApartmentNumber("9");
		address.setFloor((byte) 15);
		address.setEntrance("C");

		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setCode("discountCode");
		discountCode.setDiscountPercentage(15);

		Order order = new Order();
		order.setId("orderId");
		order.setComment("test comment order");
		order.setStatus(Status.Pending);
		order.setRestaurant(restaurant);
		order.setCreatedOn(new Date());
		order.setCustomer(customer);
		order.setDiscountCode(discountCode);
		order.setAddress(address);

		given(this.ordersRepository.findById("orderId")).willReturn(Optional.of(order));
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderDto result = this.restaurantsOrdersServiceImpl.getRestaurantOrder("restaurantId", "orderId");
		assertThat(result).usingRecursiveComparison().isEqualTo(order);
	}

	@Test
	public void getRestaurantOrdersShould_returnRestaurantOrders_whenDataIsPresent() {
		Order order = new Order();
		order.setId("orderId");
		order.setComment("test comment order");
		order.setStatus(Status.Pending);

		Order order2 = new Order();
		order2.setId("order2Id");
		order2.setComment("test comment order2");
		order2.setStatus(Status.Pending);

		Order order3 = new Order();
		order3.setId("order2Id");
		order3.setComment("test comment order2");
		order3.setStatus(Status.Pending);

		Page<Order> pageOrder = new PageImpl<>(List.of(order, order2, order3));
		given(this.ordersRepository.findByRestaurantId(eq("restaurantId"), any(Pageable.class))).willReturn(pageOrder);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		Page<OrderDto> orders = this.restaurantsOrdersServiceImpl.getRestaurantOrders("restaurantId",
				PageRequest.of(0, 3));

		List<OrderDto> result = orders.get().collect(Collectors.toList());

		assertThat(result).usingRecursiveComparison().isEqualTo(List.of(order, order2, order3));
	}

	@Test
	public void getRestaurantOrdersShould_returnEmptyPage_whenThereAreNoOrders() {
		Page<Order> pageOrder = new PageImpl<>(List.of());
		given(this.ordersRepository.findByRestaurantId(eq("restaurantId"), any(Pageable.class))).willReturn(pageOrder);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		Page<OrderDto> orders = this.restaurantsOrdersServiceImpl.getRestaurantOrders("restaurantId",
				PageRequest.of(0, 3));

		List<OrderDto> result = orders.get().collect(Collectors.toList());

		assertThat(result).usingRecursiveComparison().isEqualTo(List.of());
	}

	@Test
	public void updateRestaurantOrderStatusShould_throwNotFoundException_whenOrderIsNotFound() {
		given(this.ordersRepository.findById("orderId")).willReturn(Optional.empty());
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		NotFoundException exc = assertThrows(NotFoundException.class, () -> this.restaurantsOrdersServiceImpl
				.updateRestaurantOrderStatus("restaurantId", "orderId", new OrderStatusDto()));

		assertEquals("No such order found for restaurant!", exc.getMessage());
	}

	@Test
	public void updateRestaurantOrderStatusShould_throwNotFoundException_whenOrderDoesNotBelongToRestaurant() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("notRestaurantId");

		Order order = new Order();
		order.setRestaurant(restaurant);
		given(this.ordersRepository.findById("orderId")).willReturn(Optional.of(order));
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		NotFoundException exc = assertThrows(NotFoundException.class, () -> this.restaurantsOrdersServiceImpl
				.updateRestaurantOrderStatus("restaurantId", "orderId", new OrderStatusDto()));

		assertEquals("No such order found for restaurant!", exc.getMessage());
	}

	@Test
	public void updateRestaurantOrderStatusShould_saveTheOrderWithUpdatedStatus_whenOrderIsValid() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");

		Customer customer = new Customer();
		customer.setId("customerId");

		Order order = new Order();
		order.setStatus(Status.Pending);
		order.setRestaurant(restaurant);
		order.setCustomer(customer);

		given(this.ordersRepository.findById("orderId")).willReturn(Optional.of(order));
		given(this.ordersRepository.save(order)).willReturn(order);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderStatusDto orderStatusDto = new OrderStatusDto();
		orderStatusDto.setStatus(Status.Accepted);

		this.restaurantsOrdersServiceImpl.updateRestaurantOrderStatus("restaurantId", "orderId", orderStatusDto);

		then(this.ordersRepository).should(times(1)).save(order);
		then(this.ordersRepository).shouldHaveNoMoreInteractions();
		assertEquals(order.getStatus(), orderStatusDto.getStatus());
	}

	@Test
	public void updateRestaurantOrderStatusShould_sendWebsocketMessageToCorrectDestinationWithCorrectData_whenOrderIsValid() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");

		Customer customer = new Customer();
		customer.setId("customerId");

		Order order = new Order();
		order.setId("orderId");
		order.setStatus(Status.Pending);
		order.setRestaurant(restaurant);
		order.setCustomer(customer);

		given(this.ordersRepository.findById("orderId")).willReturn(Optional.of(order));
		given(this.ordersRepository.save(order)).willReturn(order);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderStatusDto orderStatusDto = new OrderStatusDto();
		orderStatusDto.setStatus(Status.Accepted);

		OrderStatusDto result = this.restaurantsOrdersServiceImpl.updateRestaurantOrderStatus("restaurantId", "orderId",
				orderStatusDto);

		then(this.messagingTemplate).should(times(1))
				.convertAndSend("/notifications/customers/customerId/orders/orderId", result);
		then(this.messagingTemplate).shouldHaveNoMoreInteractions();
	}

	@Test
	public void updateRestaurantOrderStatusShould_returnCorrectData_whenOrderIsValid() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");

		Customer customer = new Customer();
		customer.setId("customerId");

		Order order = new Order();
		order.setId("orderId");
		order.setStatus(Status.Pending);
		order.setRestaurant(restaurant);
		order.setCustomer(customer);

		given(this.ordersRepository.findById("orderId")).willReturn(Optional.of(order));
		given(this.ordersRepository.save(order)).willReturn(order);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderStatusDto orderStatusDto = new OrderStatusDto();
		orderStatusDto.setStatus(Status.Accepted);

		OrderStatusDto result = this.restaurantsOrdersServiceImpl.updateRestaurantOrderStatus("restaurantId", "orderId",
				orderStatusDto);

		assertThat(result).usingRecursiveComparison().isEqualTo(orderStatusDto);
	}

	@Test
	public void getOrderMonthlyGraphDataShould_addZeroToMissingDaysAndSort() {
		java.sql.Date date = new java.sql.Date(new GregorianCalendar(2021, 0, 15).getTime().getTime());
		GraphData<java.sql.Date, Long> graphData = new GraphData<>(date, 3l);

		java.sql.Date date2 = new java.sql.Date(new GregorianCalendar(2021, 0, 17).getTime().getTime());
		GraphData<java.sql.Date, Long> graphData2 = new GraphData<>(date2, 5l);

		java.sql.Date date3 = new java.sql.Date(new GregorianCalendar(2021, 0, 19).getTime().getTime());
		GraphData<java.sql.Date, Long> graphData3 = new GraphData<>(date3, 7l);

		java.sql.Date date4 = new java.sql.Date(new GregorianCalendar(2021, 0, 21).getTime().getTime());
		GraphData<java.sql.Date, Long> graphData4 = new GraphData<>(date4, 3l);

		java.sql.Date date5 = new java.sql.Date(new GregorianCalendar(2021, 0, 23).getTime().getTime());
		GraphData<java.sql.Date, Long> graphData5 = new GraphData<>(date5, 9l);

		Collection<GraphData<java.sql.Date, Long>> graphDataEntries = List.of(graphData, graphData2, graphData3,
				graphData4, graphData5);
		given(this.ordersRepository.getOrderMonthlyGraphData("restaurantId", 1, 2021)).willReturn(graphDataEntries);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);
		given(this.dateService.getNumberOfDaysForMonth(2021, 1)).willReturn(30);

		Collection<GraphData<LocalDate, Long>> expected = List.of(
				new GraphData<>(LocalDate.of(2021, 1, 1), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 2), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 3), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 4), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 5), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 6), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 7), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 8), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 9), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 10), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 11), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 12), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 13), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 14), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 15), 3l),
				new GraphData<>(LocalDate.of(2021, 1, 16), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 17), 5l),
				new GraphData<>(LocalDate.of(2021, 1, 18), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 19), 7l),
				new GraphData<>(LocalDate.of(2021, 1, 20), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 21), 3l),
				new GraphData<>(LocalDate.of(2021, 1, 22), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 23), 9l),
				new GraphData<>(LocalDate.of(2021, 1, 24), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 25), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 26), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 27), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 28), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 29), 0l),
				new GraphData<>(LocalDate.of(2021, 1, 30), 0l));

		Collection<GraphData<LocalDate, Long>> result = this.restaurantsOrdersServiceImpl
				.getOrderMonthlyGraphData("restaurantId", 2021, 1);

		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void getYearlyGraphDataaShould_addZeroToMissingDaysAndSort() {
		GraphData<Integer, Long> graphData = new GraphData<>(1, 15l);
		GraphData<Integer, Long> graphData2 = new GraphData<>(3, 30l);
		GraphData<Integer, Long> graphData3 = new GraphData<>(5, 70l);
		GraphData<Integer, Long> graphData4 = new GraphData<>(7, 50l);
		GraphData<Integer, Long> graphData5 = new GraphData<>(12, 135l);

		Collection<GraphData<Integer, Long>> mockData = new ArrayList<>(
				List.of(graphData, graphData2, graphData3, graphData4, graphData5));
		given(this.ordersRepository.getYearlyGraphData("restaurantId", 2021))
				.willReturn(mockData);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);
		given(this.dateService.getMonthName(anyInt())).willCallRealMethod();

		Collection<GraphData<String, Long>> expected = List.of(
				new GraphData<>("January", 15l),
				new GraphData<>("February", 0l),
				new GraphData<>("March", 30l),
				new GraphData<>("April", 0l),
				new GraphData<>("May", 70l),
				new GraphData<>("June", 0l),
				new GraphData<>("July", 50l),
				new GraphData<>("August", 0l),
				new GraphData<>("September", 0l),
				new GraphData<>("October", 0l),
				new GraphData<>("November", 0l),
				new GraphData<>("December", 135l));

		Collection<GraphData<String, Long>> result = this.restaurantsOrdersServiceImpl
				.getYearlyGraphData("restaurantId", 2021);

		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
}
