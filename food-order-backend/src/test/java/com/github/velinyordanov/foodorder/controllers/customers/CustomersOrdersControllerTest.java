package com.github.velinyordanov.foodorder.controllers.customers;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_ADDRESS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_CUSTOMER;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_FOODS;
import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_ORDER_RESTAURANT;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.NotValidCustomerArgumentsProvider;
import com.github.velinyordanov.foodorder.controllers.test.ValidUserProvider;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.Status;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.dto.CustomerOrderDto;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderFoodDto;
import com.github.velinyordanov.foodorder.dto.RestaurantOrderDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.customers.CustomersOrdersService;

@WebMvcTest(CustomersOrdersController.class)
public class CustomersOrdersControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CustomersOrdersService customersOrdersService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;

	private ValidUserProvider validUserProvider = new ValidUserProvider();

	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void addOrderToCustomerShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer)
			throws Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setComment("comment");

		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersOrdersService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource
	public void addOrderToCustomerShould_returnBadRequest_whenOrderCreateDtoIsNotValid(String fieldName, Object value,
			String expectedError)
			throws Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setComment("comment");

		Field field = orderCreateDto.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(orderCreateDto, value);

		Customer customer = this.validUserProvider.getValidCustomer();

		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));
		;

		then(this.customersOrdersService).shouldHaveNoInteractions();
	}

	@Test
	public void addOrderToCustomerShould_returnOkWithCorrectData_whenOrderCreateDtoIsValid()
			throws Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setComment("comment");

		OrderDto order = new OrderDto();
		order.setStatus(Status.Delivered);

		CustomerOrderDto customerOrderDto = new CustomerOrderDto();
		customerOrderDto.setId("customerId");
		customerOrderDto.setName("customerName");
		customerOrderDto.setEmail("customerEmail");
		order.setCustomer(customerOrderDto);

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
		order.setAddress(address);

		RestaurantOrderDto restaurant = new RestaurantOrderDto();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		order.setRestaurant(restaurant);

		given(this.customersOrdersService.addOrderToCustomer("customerId", orderCreateDto)).willReturn(order);
		String expectedResponse = this.objectMapper.writeValueAsString(order);

		Customer customer = this.validUserProvider.getValidCustomer();

		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.customersOrdersService).should(times(1)).addOrderToCustomer("customerId", orderCreateDto);
		then(this.customersOrdersService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void getCustomerOrdersShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer) throws Exception {
		this.mockMvc.perform(get("/customers/customerId/orders")
				.with(user(customer)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersOrdersService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource
	public void getCustomerOrdersShould_returnOkWithCorrectData_whenUserIsAuthorized(String page, int expectedPage)
			throws Exception {
		OrderDto order = new OrderDto();
		order.setStatus(Status.Delivered);

		CustomerOrderDto customerOrderDto = new CustomerOrderDto();
		customerOrderDto.setId("customerId");
		customerOrderDto.setName("customerName");
		customerOrderDto.setEmail("customerEmail");
		order.setCustomer(customerOrderDto);

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
		order.setAddress(address);

		RestaurantOrderDto restaurant = new RestaurantOrderDto();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		order.setRestaurant(restaurant);

		Customer customer = this.validUserProvider.getValidCustomer();
		Page<OrderDto> data = new PageImpl<>(List.of(order));

		given(this.customersOrdersService.getCustomerOrders(eq("customerId"), any(Pageable.class))).willReturn(data);
		String expectedResponse = this.objectMapper.writeValueAsString(data);

		this.mockMvc.perform(get("/customers/customerId/orders")
				.with(user(customer))
				.param("page", page))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));

		then(this.customersOrdersService).should(times(1)).getCustomerOrders(eq("customerId"),
				argThat(p -> p.getPageNumber() == expectedPage));
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void getCustomerOrderShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer) throws Exception {
		this.mockMvc.perform(get("/customers/customerId/orders/orderId")
				.with(user(customer)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersOrdersService).shouldHaveNoInteractions();
	}

	@Test
	public void getCustomerOrderShould_returnOkWithCorrectData_whenUserIsAuthorized() throws Exception {
		Customer customer = this.validUserProvider.getValidCustomer();

		OrderDto order = new OrderDto();
		order.setStatus(Status.Delivered);

		CustomerOrderDto customerOrderDto = new CustomerOrderDto();
		customerOrderDto.setId("customerId");
		customerOrderDto.setName("customerName");
		customerOrderDto.setEmail("customerEmail");
		order.setCustomer(customerOrderDto);

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
		order.setAddress(address);

		RestaurantOrderDto restaurant = new RestaurantOrderDto();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		order.setRestaurant(restaurant);
		
		given(customersOrdersService.getCustomerOrder("customerId", "orderId")).willReturn(order);
		String expectedResponse = this.objectMapper.writeValueAsString(order);

		this.mockMvc.perform(get("/customers/customerId/orders/orderId")
				.with(user(customer)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	private static Stream<Arguments> getCustomerOrdersShould_returnOkWithCorrectData_whenUserIsAuthorized() {
		return Stream.of(
				Arguments.of(null, 0),
				Arguments.of("-1", 0),
				Arguments.of("0", 0),
				Arguments.of("5", 5));
	}

	private static Stream<Arguments> addOrderToCustomerShould_returnBadRequest_whenOrderCreateDtoIsNotValid() {
		return Stream.of(
				// restaurantId
				Arguments.of("restaurantId", null, EMPTY_ORDER_RESTAURANT),
				Arguments.of("restaurantId", "", EMPTY_ORDER_RESTAURANT),
				Arguments.of("restaurantId", "   ", EMPTY_ORDER_RESTAURANT),

				// customerId
				Arguments.of("customerId", null, EMPTY_ORDER_CUSTOMER),
				Arguments.of("customerId", "", EMPTY_ORDER_CUSTOMER),
				Arguments.of("customerId", "   ", EMPTY_ORDER_CUSTOMER),

				// addressId
				Arguments.of("addressId", null, EMPTY_ORDER_ADDRESS),
				Arguments.of("addressId", "", EMPTY_ORDER_ADDRESS),
				Arguments.of("addressId", "   ", EMPTY_ORDER_ADDRESS),

				// foods
				Arguments.of("foods", null, EMPTY_ORDER_FOODS),
				Arguments.of("foods", Set.of(), EMPTY_ORDER_FOODS));
	}
}
