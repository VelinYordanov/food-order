package com.github.velinyordanov.foodorder.controllers.customers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.ValidUserProvider;
import com.github.velinyordanov.foodorder.data.AddressesRepository;
import com.github.velinyordanov.foodorder.data.CategoriesRepository;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.DiscountCodesRepository;
import com.github.velinyordanov.foodorder.data.OrdersRepository;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.OrderCreateDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderFoodDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CustomersOrdersControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CustomersRepository customersRepository;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private AddressesRepository addressesRepository;

	@Autowired
	private RestaurantsRepository restaurantsRepository;

	@Autowired
	private DiscountCodesRepository discountCodesRepository;

	@Autowired
	private CategoriesRepository categoriesRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EntityManager entityManager;
	
	private final ValidUserProvider validUserProvider = new ValidUserProvider();

	private Customer customer;
	private Restaurant restaurant;
	private DiscountCode discountCode;
	private Address address;
	private Food food;

	@BeforeTransaction
	public void beforeEach() {
		Customer customer = this.validUserProvider.getValidCustomer();
		customer.setEmail("validEmail@asd.asd");
		customer.setPassword(this.passwordEncoder.encode("Password_123"));
		this.customer = this.customersRepository.save(customer);
		
		Restaurant restaurant = this.validUserProvider.getValidRestaurant();
		restaurant.setEmail("restaurantEmail@asd.asd");
		restaurant.setPassword(this.passwordEncoder.encode("Restaurant_123"));
		this.restaurant = this.restaurantsRepository.save(restaurant);

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
		this.address = this.addressesRepository.save(address);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setCode("CODE3");
		discountCode.setDiscountPercentage(30);
		discountCode.setIsOncePerUser(true);
		discountCode.setIsSingleUse(true);
		discountCode.setValidFrom(LocalDate.now());
		discountCode.setValidTo(LocalDate.now().plusMonths(1));
		discountCode.setRestaurant(restaurant);
		this.discountCode = this.discountCodesRepository.save(discountCode);

		Category category = new Category();
		category.setId("firstCategoryId");
		category.setName("firstCategory");
		category.setRestaurant(restaurant);

		Food food = new Food();
		food.setId("foodId");
		food.setName("name");
		food.setDescription("description");
		food.setPrice(BigDecimal.valueOf(15));
		category.addFood(food);
		this.categoriesRepository.save(category);
		this.food = food;
	}

	@Test
	@Transactional
	public void addOrderToCustomerShould_addTheOrderToTheDatabase() throws JsonProcessingException, Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setComment("comment");
		
		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isOk());
		
		this.entityManager.flush();

		Optional<Order> savedOrderOptional = this.ordersRepository.findAll().stream().findFirst();
		assertTrue(savedOrderOptional.isPresent());
		assertThat(savedOrderOptional.get()).matches(o -> o.getAddress().equals(this.address));
		assertThat(savedOrderOptional.get()).matches(o -> o.getCustomer().equals(this.customer));
		assertThat(savedOrderOptional.get()).matches(o -> o.getRestaurant().equals(this.restaurant));
		assertThat(savedOrderOptional.get()).matches(o -> o.getDiscountCode().equals(this.discountCode));
		assertThat(savedOrderOptional.get()).matches(o -> o.getFoods()
				.stream()
				.filter(of -> of.getQuantity() == 3 && of.getFood().equals(this.food))
				.findFirst()
				.isPresent());
	}
	
	@Test
	public void addOrderToCustomerShould_returnBadRequest_whenDiscountCodeDoesNotExist() throws JsonProcessingException, Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("restaurantId");
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setDiscountCodeId("notDiscountCodeId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setComment("comment");
		
		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void addOrderToCustomerShould_returnBadRequest_whenRestaurantDoesNotExist() throws JsonProcessingException, Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("addressId");
		orderCreateDto.setRestaurantId("notRestaurantId");
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setComment("comment");
		
		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void addOrderToCustomerShould_returnBadRequest_whenAddressDoesNotExist() throws JsonProcessingException, Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("notAddressId");
		orderCreateDto.setRestaurantId("notRestaurantId");
		orderCreateDto.setCustomerId("customerId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setComment("comment");
		
		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void addOrderToCustomerShould_returnBadRequest_whenCustomerDoesNotExist() throws JsonProcessingException, Exception {
		OrderFoodDto orderFoodDto = new OrderFoodDto();
		orderFoodDto.setId("foodId");
		orderFoodDto.setQuantity(3);

		OrderCreateDto orderCreateDto = new OrderCreateDto();
		orderCreateDto.setAddressId("notAddressId");
		orderCreateDto.setRestaurantId("notRestaurantId");
		orderCreateDto.setCustomerId("notCustomerId");
		orderCreateDto.setDiscountCodeId("discountCodeId");
		orderCreateDto.setFoods(Set.of(orderFoodDto));
		orderCreateDto.setComment("comment");
		
		this.mockMvc.perform(post("/customers/customerId/orders")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(orderCreateDto)))
				.andExpect(status().isNotFound());
	}
}
