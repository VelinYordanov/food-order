package com.github.velinyordanov.foodorder.services.customers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.github.velinyordanov.foodorder.data.AddressesRepository;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.DiscountCodesRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.FoodsRepository;
import com.github.velinyordanov.foodorder.data.OrdersRepository;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.data.entities.OrderFood;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.data.entities.Status;
import com.github.velinyordanov.foodorder.dto.FoodOrderDto;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderFoodDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.ExistingUnfinishedOrderException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;
import com.github.velinyordanov.foodorder.mapping.impl.OrderCreateDtoToOrderConverter;
import com.github.velinyordanov.foodorder.mapping.impl.OrderFoodToFoodOrderDtoConverter;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;

@ExtendWith(MockitoExtension.class)
public class CustomersOrdersServiceImplTest {
	@Mock
	private FoodOrderData foodOrderData;

	@Mock
	private CustomersRepository customersRepository;

	@Mock
	private AddressesRepository addressesRepository;

	@Mock
	private RestaurantsRepository restaurantsRepository;

	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private FoodsRepository foodsRepository;

	@Mock
	private DiscountCodesRepository discountCodesRepository;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@Mock
	private DiscountCodesService discountCodesService;

	@InjectMocks
	private CustomersOrdersServiceImpl customersOrdersServiceImpl;

	@Spy
	private static MapperImpl mapper;

	@BeforeAll
	public static void beforeAll() {
		ModelMapper modelMapper = new ModelMapper();
		OrderCreateDtoToOrderConverter orderCreateDtoToOrderConverter = new OrderCreateDtoToOrderConverter();
		OrderFoodToFoodOrderDtoConverter orderFoodToFoodOrderDtoConverter = new OrderFoodToFoodOrderDtoConverter();
		modelMapper.addConverter(orderCreateDtoToOrderConverter);
		modelMapper.addConverter(orderFoodToFoodOrderDtoConverter);
		mapper = new MapperImpl(modelMapper);
	}

	@Test
	public void addOrderToCustomerShould_throwBadRequestException_whenProvidedCustomerIdAndCustomerIdInOrderDoNotMatch() {
		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("notCustomerId");

		BadRequestException exc = assertThrows(BadRequestException.class,
				() -> this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto));

		assertEquals("Customer is not valid", exc.getMessage());
	}

	@Test
	public void addOrderToCustomerShould_throwBadRequestException_whenCustomerIsNotFound() {
		given(this.customersRepository.findById("customerId")).willReturn(Optional.empty());
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto));

		assertEquals("Customer not found", exc.getMessage());
	}

	@ParameterizedTest
	@EnumSource(names = { "Pending", "Accepted" })
	public void addOrderToCustomerShould_throwExistingUnfinishedOrderException_whenCustomerHasAPendingOrAcceptedOrder(
			Status orderStatus) {
		Customer customer = new Customer();
		Order order = new Order();
		order.setStatus(orderStatus);
		customer.setOrders(Set.of(order));

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");

		ExistingUnfinishedOrderException exc = assertThrows(ExistingUnfinishedOrderException.class,
				() -> this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto));

		assertEquals("You already have a pending order. If you wish to make changes contact us.", exc.getMessage());
	}

	@Test
	public void addOrderToCustomerShould_throwNotFoundException_whenAddressIsNotFound() {
		Customer customer = new Customer();
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId")).willReturn(Optional.empty());

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto));

		assertEquals("No such address found", exc.getMessage());
	}

	@Test
	public void addOrderToCustomerShould_throwNotFoundException_whenRestaurantIsNotFound() {
		Customer customer = new Customer();
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(new Address()));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.empty());

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto));

		assertEquals("No such restaurant found", exc.getMessage());
	}

	@Test
	public void addOrderToCustomerShould_throwNotFoundException_whenAFoodThatIsNotFromTheRestaurantIsInTheOrder() {
		Customer customer = new Customer();
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(new Address()));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(new Restaurant()));

		Food food = new Food();
		food.setId("notFoodId");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto));

		assertEquals("No such food found", exc.getMessage());
	}

	@Test
	public void addOrderToCustomerShould_throwNotFoundException_whenProvidedDiscountCodeIsNotFound() {
		Customer customer = new Customer();
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(new Address()));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(new Restaurant()));
		given(this.discountCodesRepository.findById("discountCodeId")).willReturn(Optional.empty());

		Food food = new Food();
		food.setId("foodId");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setDiscountCodeId("discountCodeId");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto));

		assertEquals("Discount code not found", exc.getMessage());
	}

	@Test
	public void addOrderToCustomerShould_saveTheOrderWithCorrectData_whenValidationsPass() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

		Address address = new Address();
		address.setId("addressId");
		address.setCity(City.Sofia);
		address.setAddressType(AddressType.ApartmentBuilding);
		address.setApartmentBuildingNumber("3");
		address.setStreet("street");
		address.setStreetNumber("5");
		address.setNeighborhood("neighborhood");
		address.setApartmentNumber("9");
		address.setFloor((byte) 15);
		address.setEntrance("C");
		address.setCustomer(customer);

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("code1");
		discountCode.setId("id1");
		discountCode.setDiscountPercentage(10);
		discountCode.setOrders(Set.of());

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.discountCodesRepository.findById("discountCodeId")).willReturn(Optional.of(discountCode));
		given(this.ordersRepository.save(any(Order.class))).willAnswer(answer -> answer.getArgument(0));

		Food food = new Food();
		food.setId("foodId");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(1);
		orderCreateDto.setFoods(Set.of(orderFoodDto));

		this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto);

		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		then(this.ordersRepository).should(times(1)).save(orderCaptor.capture());
		then(this.ordersRepository).shouldHaveNoMoreInteractions();
		Order capturedOrder = orderCaptor.getValue();

		assertEquals(orderCreateDto.getComment(), capturedOrder.getComment());
		assertEquals(discountCode, capturedOrder.getDiscountCode());
		assertEquals(restaurant, capturedOrder.getRestaurant());
		assertEquals(customer, capturedOrder.getCustomer());
		assertEquals(address, capturedOrder.getAddress());
		assertEquals(Status.Pending, capturedOrder.getStatus());
		assertThat(capturedOrder.getFoods()).hasSize(1)
				.allMatch(orderFood -> "foodId".equals(orderFood.getFood().getId()) && orderFood.getQuantity() == 1);
	}

	@Test
	public void addOrderToCustomerShould_saveTheOrderWithCorrectData_whenValidationsPassWithoutDiscountCode() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

		Address address = new Address();
		address.setId("addressId");
		address.setCity(City.Sofia);
		address.setAddressType(AddressType.ApartmentBuilding);
		address.setApartmentBuildingNumber("3");
		address.setStreet("street");
		address.setStreetNumber("5");
		address.setNeighborhood("neighborhood");
		address.setApartmentNumber("9");
		address.setFloor((byte) 15);
		address.setEntrance("C");
		address.setCustomer(customer);

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.ordersRepository.save(any(Order.class))).willAnswer(answer -> answer.getArgument(0));

		Food food = new Food();
		food.setId("foodId");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);
		orderCreateDto.setFoods(Set.of(orderFoodDto));

		this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto);

		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		then(this.ordersRepository).should(times(1)).save(orderCaptor.capture());
		then(this.ordersRepository).shouldHaveNoMoreInteractions();
		Order capturedOrder = orderCaptor.getValue();

		assertEquals(orderCreateDto.getComment(), capturedOrder.getComment());
		assertNull(capturedOrder.getDiscountCode());
		assertEquals(restaurant, capturedOrder.getRestaurant());
		assertEquals(customer, capturedOrder.getCustomer());
		assertEquals(address, capturedOrder.getAddress());
		assertEquals(Status.Pending, capturedOrder.getStatus());
		assertThat(capturedOrder.getFoods()).hasSize(1)
				.allMatch(orderFood -> "foodId".equals(orderFood.getFood().getId()) && orderFood.getQuantity() == 3);
	}

	@Test
	public void addOrderToCustomerShould_sendAMessageWithCorrectDataAndToCorrectChannelThroughMessagingTemplate_whenValidationsPass() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

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
		address.setCustomer(customer);

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("code1");
		discountCode.setId("id1");
		discountCode.setDiscountPercentage(10);
		discountCode.setOrders(Set.of());

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.discountCodesRepository.findById("discountCodeId")).willReturn(Optional.of(discountCode));
		given(this.ordersRepository.save(any(Order.class))).willAnswer(answer -> answer.getArgument(0));

		Food food = new Food();
		food.setId("foodId");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(1);
		orderCreateDto.setFoods(Set.of(orderFoodDto));

		this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto);

		ArgumentCaptor<OrderDto> orderCaptor = ArgumentCaptor.forClass(OrderDto.class);
		then(this.messagingTemplate).should(times(1))
				.convertAndSend(eq("/notifications/restaurants/restaurantId/orders"), orderCaptor.capture());
		then(this.messagingTemplate).shouldHaveNoMoreInteractions();
		OrderDto capturedOrder = orderCaptor.getValue();

		assertEquals(orderCreateDto.getComment(), capturedOrder.getComment());
		assertThat(capturedOrder.getDiscountCode()).usingRecursiveComparison().isEqualTo(discountCode);
		assertEquals(restaurant.getId(), capturedOrder.getRestaurant().getId());
		assertEquals(customer.getId(), capturedOrder.getCustomer().getId());
		assertThat(capturedOrder.getFoods()).hasSize(1)
				.allMatch(foodOrderDto -> "foodId".equals(foodOrderDto.getId()) && foodOrderDto.getQuantity() == 1);
	}

	@Test
	public void addOrderToCustomerShould_sendAMessageWithCorrectDataAndToCorrectChannelThroughMessagingTemplate_whenValidationsPassWithoutDiscountCode() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

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
		address.setCustomer(customer);

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.ordersRepository.save(any(Order.class))).willAnswer(answer -> answer.getArgument(0));

		Food food = new Food();
		food.setId("foodId");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(1);
		orderCreateDto.setFoods(Set.of(orderFoodDto));

		this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto);

		ArgumentCaptor<OrderDto> orderCaptor = ArgumentCaptor.forClass(OrderDto.class);
		then(this.messagingTemplate).should(times(1))
				.convertAndSend(eq("/notifications/restaurants/restaurantId/orders"), orderCaptor.capture());
		then(this.messagingTemplate).shouldHaveNoMoreInteractions();
		OrderDto capturedOrder = orderCaptor.getValue();

		assertEquals(orderCreateDto.getComment(), capturedOrder.getComment());
		assertNull(capturedOrder.getDiscountCode());
		assertEquals(restaurant.getId(), capturedOrder.getRestaurant().getId());
		assertEquals(customer.getId(), capturedOrder.getCustomer().getId());
		assertThat(capturedOrder.getFoods()).hasSize(1)
				.allMatch(foodOrderDto -> "foodId".equals(foodOrderDto.getId()) && foodOrderDto.getQuantity() == 1);
	}

	@Test
	public void addOrderToCustomerShould_returnCorrectData_whenValidationsPass() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

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
		address.setCustomer(customer);

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("code1");
		discountCode.setId("id1");
		discountCode.setDiscountPercentage(10);
		discountCode.setOrders(Set.of());

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.discountCodesRepository.findById("discountCodeId")).willReturn(Optional.of(discountCode));
		given(this.ordersRepository.save(any(Order.class))).willAnswer(answer -> answer.getArgument(0));

		Food food = new Food();
		food.setId("foodId");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(1);
		orderCreateDto.setFoods(Set.of(orderFoodDto));

		OrderDto orderDto = this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto);

		assertEquals(orderCreateDto.getComment(), orderDto.getComment());
		assertThat(orderDto.getDiscountCode()).usingRecursiveComparison().isEqualTo(discountCode);
		assertEquals(restaurant.getId(), orderDto.getRestaurant().getId());
		assertEquals(customer.getId(), orderDto.getCustomer().getId());
		assertThat(orderDto.getFoods()).hasSize(1)
				.allMatch(foodOrderDto -> "foodId".equals(foodOrderDto.getId()) && foodOrderDto.getQuantity() == 1);
	}

	@Test
	public void addOrderToCustomerShould_returnCorrectData_whenValidationsPassWithoutDiscountCode() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");
		Order order = new Order();
		order.setStatus(Status.Delivered);
		customer.setOrders(Set.of(order));

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
		address.setCustomer(customer);

		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(restaurant));
		given(this.ordersRepository.save(any(Order.class))).willAnswer(answer -> answer.getArgument(0));

		Food food = new Food();
		food.setId("foodId");
		Food food2 = new Food();
		food2.setId("foodId2");
		Food food3 = new Food();
		food3.setId("foodId3");
		given(this.foodsRepository.findByRestaurantId("restaurantId")).willReturn(List.of(food, food2, food3));

		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.foodOrderData.foods()).willReturn(this.foodsRepository);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setComment("comment");

		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(1);

		OrderFoodDto orderFoodDto2 = new OrderFoodDto();
		orderFoodDto2.setId("foodId2");
		orderFoodDto2.setQuantity(3);

		OrderFoodDto orderFoodDto3 = new OrderFoodDto();
		orderFoodDto3.setId("foodId3");
		orderFoodDto3.setQuantity(5);
		orderCreateDto.setFoods(Set.of(orderFoodDto, orderFoodDto2, orderFoodDto3));

		OrderDto orderDto = this.customersOrdersServiceImpl.addOrderToCustomer("customerId", orderCreateDto);

		assertEquals(orderCreateDto.getComment(), orderDto.getComment());
		assertNull(orderDto.getDiscountCode());
		assertEquals(restaurant.getId(), orderDto.getRestaurant().getId());
		assertEquals(customer.getId(), orderDto.getCustomer().getId());
		assertThat(orderDto.getFoods())
				.hasSize(3)
				.usingRecursiveComparison()
				.ignoringFields("price", "name", "description")
				.ignoringCollectionOrder()
				.isEqualTo(List.of(orderFoodDto, orderFoodDto2, orderFoodDto3));
	}

	@Test
	public void getCustomerOrdersShould_returnCorrectData() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");

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
		address.setCustomer(customer);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("code1");
		discountCode.setId("id1");
		discountCode.setDiscountPercentage(10);

		Food food = new Food();
		food.setId("foodId");
		food.setName("foodName");
		food.setPrice(BigDecimal.valueOf(15));

		Food food2 = new Food();
		food.setId("food2Id");
		food.setName("food2Name");
		food.setPrice(BigDecimal.valueOf(30));

		Food food3 = new Food();
		food.setId("food3Id");
		food.setName("food3Name");
		food.setPrice(BigDecimal.valueOf(50));

		OrderFood orderFood = new OrderFood();
		orderFood.setFood(food);
		OrderFood orderFood2 = new OrderFood();
		orderFood2.setFood(food2);
		OrderFood orderFood3 = new OrderFood();
		orderFood3.setFood(food3);

		Order order = new Order();
		order.setId("orderId");
		order.setStatus(Status.Delivered);
		order.setCreatedOn(new Date());
		order.setRestaurant(restaurant);
		order.setCustomer(customer);
		order.setAddress(address);
		order.setDiscountCode(discountCode);
		order.setComment("comment");

		orderFood.setOrder(order);
		orderFood2.setOrder(order);
		orderFood3.setOrder(order);

		order.setFoods(Set.of(orderFood, orderFood2, orderFood3));

		PageImpl<Order> pagedOrders = new PageImpl<>(List.of(order));

		given(this.ordersRepository.findByCustomerId(eq("customerId"), any(Pageable.class))).willReturn(pagedOrders);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		Page<OrderDto> pagedResult = this.customersOrdersServiceImpl.getCustomerOrders("customerId",
				mock(Pageable.class));

		List<OrderDto> result = pagedResult.getContent();

		assertThat(result)
				.usingRecursiveComparison()
				.ignoringFields("foods")
				.isEqualTo(List.of(order));

		Collection<FoodOrderDto> foodsResult = result
				.stream()
				.flatMap(x -> x.getFoods().stream())
				.collect(Collectors.toList());

		assertThat(foodsResult)
				.usingRecursiveComparison()
				.ignoringFields("quantity")
				.ignoringCollectionOrder()
				.isEqualTo(List.of(food, food2, food3));
	}

	@Test
	public void getCustomerOrdersShould_returnEmptyCollection_whenOrdersAreEmpty() {
		PageImpl<Order> pagedOrders = new PageImpl<>(List.of());

		given(this.ordersRepository.findByCustomerId(eq("customerId"), any(Pageable.class))).willReturn(pagedOrders);
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		Page<OrderDto> pagedResult = this.customersOrdersServiceImpl.getCustomerOrders("customerId",
				mock(Pageable.class));

		List<OrderDto> result = pagedResult.getContent();

		assertTrue(result.isEmpty());
	}

	@Test
	public void getCustomerOrderShould_throwNotFoundException_whenOrderIsNotFound() {
		given(this.ordersRepository.findByIdAndCustomerId("orderId", "customerId")).willReturn(Optional.empty());
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersOrdersServiceImpl.getCustomerOrder("customerId", "orderId"));

		assertEquals("Order not found", exc.getMessage());
	}

	@Test
	public void getCustomerOrderShould_returnCorrectData_whenOrderIsFound() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("restaurantId");
		restaurant.setName("restaurantName");
		restaurant.setDescription("restaurantDescription");

		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setName("customerName");
		customer.setEmail("customerEmail");

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
		address.setCustomer(customer);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("code1");
		discountCode.setId("id1");
		discountCode.setDiscountPercentage(10);

		Food food = new Food();
		food.setId("foodId");
		food.setName("foodName");
		food.setPrice(BigDecimal.valueOf(15));

		Food food2 = new Food();
		food.setId("food2Id");
		food.setName("food2Name");
		food.setPrice(BigDecimal.valueOf(30));

		Food food3 = new Food();
		food.setId("food3Id");
		food.setName("food3Name");
		food.setPrice(BigDecimal.valueOf(50));

		OrderFood orderFood = new OrderFood();
		orderFood.setFood(food);
		OrderFood orderFood2 = new OrderFood();
		orderFood2.setFood(food2);
		OrderFood orderFood3 = new OrderFood();
		orderFood3.setFood(food3);

		Order order = new Order();
		order.setId("orderId");
		order.setStatus(Status.Delivered);
		order.setCreatedOn(new Date());
		order.setRestaurant(restaurant);
		order.setCustomer(customer);
		order.setAddress(address);
		order.setDiscountCode(discountCode);
		order.setComment("comment");

		orderFood.setOrder(order);
		orderFood2.setOrder(order);
		orderFood3.setOrder(order);

		order.setFoods(Set.of(orderFood, orderFood2, orderFood3));

		given(this.ordersRepository.findByIdAndCustomerId("orderId", "customerId")).willReturn(Optional.of(order));
		given(this.foodOrderData.orders()).willReturn(this.ordersRepository);

		OrderDto orderDto = this.customersOrdersServiceImpl.getCustomerOrder("customerId", "orderId");

		assertThat(orderDto)
				.usingRecursiveComparison()
				.ignoringFields("foods")
				.isEqualTo(order);

		assertThat(orderDto.getFoods())
				.usingRecursiveComparison()
				.ignoringFields("quantity")
				.ignoringCollectionOrder()
				.isEqualTo(List.of(food, food2, food3));
	}
}
