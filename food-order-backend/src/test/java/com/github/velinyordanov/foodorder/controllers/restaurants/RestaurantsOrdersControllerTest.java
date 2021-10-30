package com.github.velinyordanov.foodorder.controllers.restaurants;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_STATUS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.NotValidRestaurantArgumentsProvider;
import com.github.velinyordanov.foodorder.controllers.test.ValidUserProvider;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.data.entities.Status;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.dto.CustomerOrderDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.GraphData;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderStatusDto;
import com.github.velinyordanov.foodorder.dto.RestaurantOrderDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.DateService;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsOrdersService;

@WebMvcTest(RestaurantsOrdersController.class)
public class RestaurantsOrdersControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestaurantsOrdersService restaurantsOrdersService;

	@MockBean
	private DateService dateService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;

	ValidUserProvider validRestaurantProvider = new ValidUserProvider();

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void getRestaurantOrdersShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		this.mockMvc.perform(get("/restaurants/restaurantId/orders")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@ParameterizedTest
	@MethodSource
	public void getRestaurantOrdersShould_returnOkAndCorrectDataWithCorrectPage_whenUserIsAuthorized(int page,
			int expectedPage)
			throws Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		RestaurantOrderDto restaurantDto = new RestaurantOrderDto();
		restaurantDto.setName("restaurantName");
		restaurantDto.setId("restaurantId");

		CustomerOrderDto customer = new CustomerOrderDto();
		customer.setId("customerId");
		customer.setEmail("customer@email.com");
		customer.setName("customerName");
		customer.setPhoneNumber("phoneNumber");

		AddressDto address = new AddressDto();
		address.setCity(City.Sofia);
		address.setAddressType(AddressType.ApartmentBuilding);
		address.setApartmentBuildingNumber("3");
		address.setStreet("street");
		address.setStreetNumber("5");
		address.setNeighborhood("neighborhood");
		address.setApartmentNumber("9");
		address.setFloor((byte) 15);
		address.setEntrance("C");

		DiscountCodeDto discountCode = new DiscountCodeDto();
		discountCode.setId("discountCodeId");
		discountCode.setCode("discountCode");
		discountCode.setDiscountPercentage(15);

		OrderDto order = new OrderDto();
		order.setId("orderId");
		order.setComment("test comment order");
		order.setStatus(Status.Pending);
		order.setRestaurant(restaurantDto);
		order.setCreatedOn(new Date());
		order.setCustomer(customer);
		order.setDiscountCode(discountCode);
		order.setAddress(address);

		Page<OrderDto> pagedOrders = new PageImpl<OrderDto>(List.of(order));
		String expectedResult = this.objectMapper.writeValueAsString(pagedOrders);
		given(this.restaurantsOrdersService.getRestaurantOrders(eq("restaurantId"), any(Pageable.class)))
				.willReturn(pagedOrders);

		this.mockMvc.perform(get("/restaurants/restaurantId/orders")
				.with(user(restaurant))
				.queryParam("page", Integer.toString(page)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResult, result.getResponse().getContentAsString()));

		then(this.restaurantsOrdersService).should(times(1)).getRestaurantOrders(eq("restaurantId"),
				argThat(p -> p.getPageNumber() == expectedPage));
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void getRestaurantOrderShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		this.mockMvc.perform(get("/restaurants/restaurantId/orders/orderId")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@Test
	public void getRestaurantOrderShould_returnCorrectData_whenUserIsAuthorized()
			throws Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		RestaurantOrderDto restaurantDto = new RestaurantOrderDto();
		restaurantDto.setName("restaurantName");
		restaurantDto.setId("restaurantId");

		CustomerOrderDto customer = new CustomerOrderDto();
		customer.setId("customerId");
		customer.setEmail("customer@email.com");
		customer.setName("customerName");
		customer.setPhoneNumber("phoneNumber");

		AddressDto address = new AddressDto();
		address.setCity(City.Sofia);
		address.setAddressType(AddressType.ApartmentBuilding);
		address.setApartmentBuildingNumber("3");
		address.setStreet("street");
		address.setStreetNumber("5");
		address.setNeighborhood("neighborhood");
		address.setApartmentNumber("9");
		address.setFloor((byte) 15);
		address.setEntrance("C");

		DiscountCodeDto discountCode = new DiscountCodeDto();
		discountCode.setId("discountCodeId");
		discountCode.setCode("discountCode");
		discountCode.setDiscountPercentage(15);

		OrderDto order = new OrderDto();
		order.setId("orderId");
		order.setComment("test comment order");
		order.setStatus(Status.Pending);
		order.setRestaurant(restaurantDto);
		order.setCreatedOn(new Date());
		order.setCustomer(customer);
		order.setDiscountCode(discountCode);
		order.setAddress(address);

		String expectedResponse = this.objectMapper.writeValueAsString(order);

		given(this.restaurantsOrdersService.getRestaurantOrder("restaurantId", "orderId")).willReturn(order);

		this.mockMvc.perform(get("/restaurants/restaurantId/orders/orderId")
				.with(user(restaurant)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void patchRestaurantOrderShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		OrderStatusDto orderStatusDto = new OrderStatusDto();
		orderStatusDto.setStatus(Status.Accepted);

		this.mockMvc.perform(patch("/restaurants/restaurantId/orders/orderId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderStatusDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@Test
	public void patchRestaurantOrderShould_returnBadRequest_whenOrderStatusDtoIsNotValid()
			throws JsonProcessingException, Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		OrderStatusDto orderStatusDto = new OrderStatusDto();
		orderStatusDto.setStatus(null);

		this.mockMvc.perform(patch("/restaurants/restaurantId/orders/orderId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderStatusDto)))
				.andExpect(status().isBadRequest())
				.andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(EMPTY_STATUS)));
	}

	@Test
	public void patchRestaurantOrderShould_returnCorrectData_whenOrderStatusDtoIsValid()
			throws Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		OrderStatusDto orderStatusDto = new OrderStatusDto();
		orderStatusDto.setStatus(Status.Accepted);
		String expectedResponse = this.objectMapper.writeValueAsString(orderStatusDto);

		given(this.restaurantsOrdersService.updateRestaurantOrderStatus("restaurantId", "orderId", orderStatusDto))
				.willReturn(orderStatusDto);

		this.mockMvc.perform(patch("/restaurants/restaurantId/orders/orderId")
				.with(user(restaurant))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderStatusDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void getOrdersMonthlyGraphDataShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		this.mockMvc.perform(get("/restaurants/restaurantId/orders/monthly-graph")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@ParameterizedTest
	@MethodSource
	public void getOrdersMonthlyGraphDataShould_returnOkAndCorrectData_whenUserIsAuthorized(int year, int month,
			int expectedYear, int expectedMonth) throws Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();

		given(this.dateService.now()).willReturn(LocalDate.of(2021, Month.SEPTEMBER, 15));

		GraphData<LocalDate, Long> graphData = new GraphData<LocalDate, Long>(LocalDate.of(2021, Month.SEPTEMBER, 15),
				15l);
		Collection<GraphData<LocalDate, Long>> data = List.of(graphData);
		String expectedResponse = this.objectMapper.writeValueAsString(data);

		given(this.restaurantsOrdersService.getOrderMonthlyGraphData(eq("restaurantId"), any(Integer.class),
				any(Integer.class))).willReturn(data);

		this.mockMvc.perform(get("/restaurants/restaurantId/orders/monthly-graph")
				.with(user(restaurant))
				.param("year",Integer.toString(year))
				.param("month", Integer.toString(month)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.restaurantsOrdersService).should(times(1)).getOrderMonthlyGraphData("restaurantId", expectedYear,
				expectedMonth);
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidRestaurantArgumentsProvider.class)
	public void getOrdersYearlyGraphDataShould_returnUnauthorized_whenUserIsNotAuthorized(Restaurant restaurant)
			throws Exception {
		this.mockMvc.perform(get("/restaurants/restaurantId/orders/yearly-graph")
				.with(user(restaurant)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}
	
	@ParameterizedTest
	@MethodSource
	public void getOrdersYearlyGraphDataShould_returnUnauthorized_whenUserIsNotAuthorized(int year, int expectedYear)
			throws Exception {
		Restaurant restaurant = this.validRestaurantProvider.getValidRestaurant();
		
		GraphData<String, Long> graphData = new GraphData<>("October", 35l);
		Collection<GraphData<String, Long>> data = List.of(graphData);
		String expectedResponse = this.objectMapper.writeValueAsString(data);
		
		given(this.dateService.now()).willReturn(LocalDate.of(2021, Month.SEPTEMBER, 15));
		given(this.restaurantsOrdersService.getYearlyGraphData(eq("restaurantId"), any(Integer.class))).willReturn(data);
		
		this.mockMvc.perform(get("/restaurants/restaurantId/orders/yearly-graph")
				.with(user(restaurant))
				.param("year", Integer.toString(year)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
		
		then(this.restaurantsOrdersService).should(times(1)).getYearlyGraphData("restaurantId", expectedYear);
	}

	private static Stream<Arguments> getRestaurantOrdersShould_returnOkAndCorrectDataWithCorrectPage_whenUserIsAuthorized() {
		return Stream.of(
				Arguments.of(0, 0),
				Arguments.of(-5, 0),
				Arguments.of(3, 3));
	}

	private static Stream<Arguments> getOrdersMonthlyGraphDataShould_returnOkAndCorrectData_whenUserIsAuthorized() {
		return Stream.of(
				Arguments.of(2020, 5, 2020, 5),
				Arguments.of(0, 9, 2021, 9),
				Arguments.of(2021, -5, 2021, 9),
				Arguments.of(2021, 13, 2021, 9),
				Arguments.of(-5, 15, 2021, 9));
	}
	
	private static Stream<Arguments> getOrdersYearlyGraphDataShould_returnUnauthorized_whenUserIsNotAuthorized() {
		return Stream.of(
				Arguments.of(2020, 2020),
				Arguments.of(0, 2021));
	}
}
