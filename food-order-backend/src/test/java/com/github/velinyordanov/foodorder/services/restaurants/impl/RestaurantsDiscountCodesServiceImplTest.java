package com.github.velinyordanov.foodorder.services.restaurants.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.argThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.github.velinyordanov.foodorder.data.DiscountCodesRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeEditDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeListDto;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.ExistingDiscountCodeException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;

@ExtendWith(MockitoExtension.class)
public class RestaurantsDiscountCodesServiceImplTest {
	@Mock
	private FoodOrderData foodOrderData;

	@Mock
	private DiscountCodesRepository discountCodesRepository;

	@Mock
	private RestaurantsRepository restaurantsRepository;

	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());

	@Mock
	private DiscountCodesService discountCodesService;

	@InjectMocks
	private RestaurantsDiscountCodesServiceImpl restaurantsDiscountCodesService;

	@Test
	public void getDiscountByCodeShould_throwNotFoundException_whenDiscountCodeDoesNotExist() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);
		given(this.discountCodesRepository.findByCodeAndRestaurantId("restaurantId", "code"))
				.willReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> this.restaurantsDiscountCodesService.getDiscountByCode("restaurantId", "code", "customerId"));
		assertEquals("Discount code not found", exception.getMessage());
	}

	@Test
	public void getDiscountByCodeShould_validateDiscountCode_whenDiscountCodeExists() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		given(this.discountCodesRepository.findByCodeAndRestaurantId("restaurantId", "code"))
				.willReturn(Optional.of(discountCode));

		this.restaurantsDiscountCodesService.getDiscountByCode("restaurantId", "code", "customerId");

		then(this.discountCodesService).should(times(1)).validateDiscountCode(discountCode, "customerId");
		then(this.discountCodesService).shouldHaveNoMoreInteractions();
	}

	@Test
	public void getDiscountByCodeShould_returnTheDiscountCode_whenDiscountCodeExistsAndIsValid() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("code");
		discountCode.setId("id");
		discountCode.setDiscountPercentage(15);
		given(this.discountCodesRepository.findByCodeAndRestaurantId("restaurantId", "code"))
				.willReturn(Optional.of(discountCode));

		DiscountCodeDto discountCodeResult = this.restaurantsDiscountCodesService.getDiscountByCode("restaurantId",
				"code", "customerId");

		assertEquals("id", discountCodeResult.getId());
		assertEquals("code", discountCodeResult.getCode());
		assertEquals(15, discountCodeResult.getDiscountPercentage());
	}

	@Test
	public void getDiscountCodesForRestaurantShould_ReturnDiscountCodesForRestaurant() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode1 = new DiscountCode();
		discountCode1.setCode("code1");
		discountCode1.setId("id1");
		discountCode1.setDiscountPercentage(10);
		discountCode1.setOrders(Set.of());

		DiscountCode discountCode2 = new DiscountCode();
		discountCode2.setCode("code2");
		discountCode2.setId("id2");
		discountCode2.setDiscountPercentage(15);
		discountCode2.setOrders(Set.of(new Order()));

		DiscountCode discountCode3 = new DiscountCode();
		discountCode3.setCode("code3");
		discountCode3.setId("id3");
		discountCode3.setDiscountPercentage(20);
		discountCode3.setOrders(Set.of(new Order(), new Order()));

		Collection<DiscountCode> categories = List.of(discountCode1, discountCode2, discountCode3);

		given(this.discountCodesRepository.findByRestaurant("restaurantId")).willReturn(categories);

		Collection<DiscountCodeListDto> expected = categories
				.stream()
				.map(dc -> {
					DiscountCodeListDto dto = this.mapper.map(dc, DiscountCodeListDto.class);
					dto.setTimesUsed(dc.getOrders().size());

					return dto;
				})
				.collect(Collectors.toList());

		Collection<DiscountCodeListDto> result = this.restaurantsDiscountCodesService
				.getDiscountCodesForRestaurant("restaurantId");

		assertEquals(expected, result);
	}

	@Test
	public void getDiscountCodesForRestaurantShould_ReturnAnEmptyCollection_WhenNoDiscountCodesAreFound() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);
		given(this.discountCodesRepository.findByRestaurant("restaurantId")).willReturn(List.of());

		Collection<DiscountCodeListDto> result = this.restaurantsDiscountCodesService
				.getDiscountCodesForRestaurant("restaurantId");

		assertEquals(result.size(), 0);
	}

	@Test
	public void addDiscountCodeToRestaurantShould_throwNotFoundException_whenRestaurantIsNotFound() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.empty());
		DiscountCodeCreateDto discountCode = new DiscountCodeCreateDto();

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsDiscountCodesService.addDiscountCodeToRestaurant("restaurantId", discountCode));

		assertEquals("Restaurant not found", exc.getMessage());
	}

	@Test
	public void addDiscountCodeToRestaurantShould_throwExistingDiscountCodeException_whenDiscountCodeExists() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Restaurant resturant = new Restaurant();
		resturant.setId("restaurantId");
		resturant.setName("restaurantName");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(resturant));

		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCodeEntity = new DiscountCode();
		discountCodeEntity.setCode("CODE");
		discountCodeEntity.setRestaurant(resturant);
		given(this.discountCodesRepository.findByCodeAndRestaurantIdWithDeleted("restaurantId", "CODE"))
				.willReturn(Optional.of(discountCodeEntity));

		DiscountCodeCreateDto discountCode = new DiscountCodeCreateDto();
		discountCode.setCode("CODE");

		ExistingDiscountCodeException exc = assertThrows(ExistingDiscountCodeException.class,
				() -> this.restaurantsDiscountCodesService.addDiscountCodeToRestaurant("restaurantId", discountCode));

		assertEquals("Discount code CODE already exists for restaurant restaurantName", exc.getMessage());
	}

	@Test
	public void addDiscountCodeToRestaurantShould_saveTheNewDiscountCode_whenDiscountCodeDoesNotExist() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Restaurant resturant = new Restaurant();
		resturant.setId("restaurantId");
		resturant.setName("restaurantName");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(resturant));

		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		given(this.discountCodesRepository.findByCodeAndRestaurantIdWithDeleted("restaurantId", "CODE"))
				.willReturn(Optional.empty());
		given(this.discountCodesRepository.save(any(DiscountCode.class))).willAnswer(answer -> answer.getArgument(0));

		DiscountCodeCreateDto discountCode = new DiscountCodeCreateDto();
		discountCode.setCode("CODE");
		discountCode.setDiscountPercentage(30);
		discountCode.setIsOncePerUser(true);
		discountCode.setIsSingleUse(true);
		discountCode.setValidFrom(LocalDate.of(2021, 5, 3));
		discountCode.setValidTo(LocalDate.of(2021, 12, 3));

		ArgumentCaptor<DiscountCode> discountCodeCaptor = ArgumentCaptor.forClass(DiscountCode.class);

		this.restaurantsDiscountCodesService.addDiscountCodeToRestaurant("restaurantId", discountCode);

		then(this.discountCodesRepository).should(times(1)).save(discountCodeCaptor.capture());
		then(this.discountCodesRepository).shouldHaveNoMoreInteractions();

		DiscountCode savedDiscountCode = discountCodeCaptor.getValue();

		assertEquals(discountCode.getCode(), savedDiscountCode.getCode());
		assertEquals(discountCode.getDiscountPercentage(), savedDiscountCode.getDiscountPercentage());
		assertEquals(discountCode.getIsOncePerUser(), savedDiscountCode.getIsOncePerUser());
		assertEquals(discountCode.getIsSingleUse(), savedDiscountCode.getIsSingleUse());
		assertEquals(discountCode.getValidFrom(), savedDiscountCode.getValidFrom());
		assertEquals(discountCode.getValidTo(), savedDiscountCode.getValidTo());
	}

	@Test
	public void addDiscountCodeToRestaurantShould_capitalizeTheProvidedCode() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Restaurant resturant = new Restaurant();
		resturant.setId("restaurantId");
		resturant.setName("restaurantName");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(resturant));

		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		given(this.discountCodesRepository.findByCodeAndRestaurantIdWithDeleted("restaurantId", "CODE"))
				.willReturn(Optional.empty());
		given(this.discountCodesRepository.save(any(DiscountCode.class))).willAnswer(answer -> answer.getArgument(0));

		DiscountCodeCreateDto discountCode = new DiscountCodeCreateDto();
		discountCode.setCode("code");
		discountCode.setDiscountPercentage(30);
		discountCode.setIsOncePerUser(true);
		discountCode.setIsSingleUse(true);
		discountCode.setValidFrom(LocalDate.of(2021, 5, 3));
		discountCode.setValidTo(LocalDate.of(2021, 12, 3));

		this.restaurantsDiscountCodesService.addDiscountCodeToRestaurant("restaurantId", discountCode);

		then(this.discountCodesRepository).should(times(1)).findByCodeAndRestaurantIdWithDeleted("restaurantId",
				"CODE");
		then(this.discountCodesRepository).should(times(1)).save(argThat(arg -> "CODE".equals(arg.getCode())));
		then(this.discountCodesRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	public void addDiscountCodeToRestaurantShould_returnTheSavedDiscountCode_whenDiscountCodeDoesNotExist() {
		given(this.foodOrderData.restaurants()).willReturn(this.restaurantsRepository);

		Restaurant resturant = new Restaurant();
		resturant.setId("restaurantId");
		resturant.setName("restaurantName");
		given(this.restaurantsRepository.findById("restaurantId")).willReturn(Optional.of(resturant));

		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		given(this.discountCodesRepository.findByCodeAndRestaurantIdWithDeleted("restaurantId", "CODE"))
				.willReturn(Optional.empty());
		given(this.discountCodesRepository.save(any(DiscountCode.class))).willAnswer(answer -> answer.getArgument(0));

		DiscountCodeCreateDto discountCode = new DiscountCodeCreateDto();
		discountCode.setCode("CODE");
		discountCode.setDiscountPercentage(30);
		discountCode.setIsOncePerUser(true);
		discountCode.setIsSingleUse(true);
		discountCode.setValidFrom(LocalDate.of(2021, 5, 3));
		discountCode.setValidTo(LocalDate.of(2021, 12, 3));

		DiscountCodeDto result = this.restaurantsDiscountCodesService.addDiscountCodeToRestaurant("restaurantId",
				discountCode);

		assertEquals(result.getCode(), discountCode.getCode());
		assertEquals(result.getDiscountPercentage(), discountCode.getDiscountPercentage());
	}

	@Test
	public void deleteDiscountCodeShould_throwNotFoundException_WhenDiscountCodeIsNotFound() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.empty());

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsDiscountCodesService.deleteDiscountCode("restaurantId", "discountCodeId"));
		assertEquals("Discount code not found", exc.getMessage());
	}

	@Test
	public void deleteDiscountCodeShould_throwBadRequestException_WhenDiscountCodeHasBeenUsedForOrders() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setOrders(Set.of(new Order()));
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));

		BadRequestException exc = assertThrows(BadRequestException.class,
				() -> this.restaurantsDiscountCodesService.deleteDiscountCode("restaurantId", "discountCodeId"));
		assertEquals("Discount code has orders associated with it and cannot be deleted.", exc.getMessage());
	}

	@Test
	public void deleteDiscountCodeShould_deleteTheDiscountCode_WhenDiscountCodeIsFoundAndHasNoOrders() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setOrders(Set.of());
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));

		this.restaurantsDiscountCodesService.deleteDiscountCode("restaurantId", "discountCodeId");

		then(this.discountCodesRepository).should(times(1)).delete(discountCode);
		then(this.discountCodesRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	public void deleteDiscountCodeShould_returnDeletedDiscountCodeData_WhenDiscountCodeIsFoundAndHasNoOrders() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setCode("CODE");
		discountCode.setDiscountPercentage(15);
		discountCode.setOrders(Set.of());
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));

		DiscountCodeDto result = this.restaurantsDiscountCodesService.deleteDiscountCode("restaurantId",
				"discountCodeId");

		assertEquals(discountCode.getId(), result.getId());
		assertEquals(discountCode.getCode(), result.getCode());
		assertEquals(discountCode.getDiscountPercentage(), result.getDiscountPercentage());
	}

	@Test
	public void editDiscountCodeShould_throwNotFoundException_whenDiscountCodeIsNotFound() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.empty());

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.restaurantsDiscountCodesService.editDiscountCode("restaurantId", "discountCodeId",
						discountCodeEditDto));
		assertEquals("Discount code not found", exc.getMessage());
	}

	@Test
	public void editDiscountCodeShould_throwBadRequestException_whenDiscountCodeHasBeenUsedAndDiscountPercentageIsChanged() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setDiscountPercentage(15);
		discountCode.setOrders(Set.of(new Order()));
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(20);
		BadRequestException exc = assertThrows(BadRequestException.class,
				() -> this.restaurantsDiscountCodesService.editDiscountCode("restaurantId", "discountCodeId",
						discountCodeEditDto));
		assertEquals("Discount percentage can be changed only to discount codes that have not been used yet.",
				exc.getMessage());
	}

	@Test
	public void editDiscountCodeShould_throwBadRequestException_whenValidFromHasBeenUsedAndDiscountPercentageIsChanged() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setValidFrom(LocalDate.of(2021, 5, 5));
		discountCode.setOrders(Set.of(new Order()));
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setValidFrom(LocalDate.of(2021, 5, 15));
		BadRequestException exc = assertThrows(BadRequestException.class,
				() -> this.restaurantsDiscountCodesService.editDiscountCode("restaurantId", "discountCodeId",
						discountCodeEditDto));
		assertEquals("Valid from can only be changed for orders that have not been used yet.", exc.getMessage());
	}
	
	@Test
	public void editDiscountCodeShould_throwBadRequestException_whenIsSingleUseIsEnabledButThereIsMoreThanOneOrder() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		Customer customer = new Customer();
		customer.setId("customerId");
		
		Order order = new Order();
		order.setCustomer(customer);
		Order order2 = new Order();
		order2.setCustomer(customer);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setValidFrom(LocalDate.of(2021, 5, 5));
		discountCode.setOrders(Set.of(order, order2));
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setIsSingleUse(false);
		discountCodeEditDto.setValidFrom(LocalDate.of(2021, 5, 5));
		BadRequestException exc = assertThrows(BadRequestException.class,
				() -> this.restaurantsDiscountCodesService.editDiscountCode("restaurantId", "discountCodeId",
						discountCodeEditDto));
		assertEquals("Cannot disable single use as the discount code has been used.", exc.getMessage());
	}
	
	@Test
	public void editDiscountCodeShould_throwBadRequestException_whenOncePerUserIsEnabledButThereIsMoreThanOneOrderForCustomer() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		Customer customer = new Customer();
		customer.setId("customerId");
		
		Order order = new Order();
		order.setCustomer(customer);
		Order order2 = new Order();
		order2.setCustomer(customer);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setValidFrom(LocalDate.of(2021, 5, 5));
		discountCode.setOrders(Set.of(order, order2));
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setIsSingleUse(true);
		discountCodeEditDto.setIsOncePerUser(true);
		discountCodeEditDto.setValidFrom(LocalDate.of(2021, 5, 5));
		BadRequestException exc = assertThrows(BadRequestException.class,
				() -> this.restaurantsDiscountCodesService.editDiscountCode("restaurantId", "discountCodeId",
						discountCodeEditDto));
		assertEquals("Cannot enable once per user as users have used the discount code more than once already", exc.getMessage());
	}

	@Test
	public void editDiscountCodeShould_saveTheDiscountCodeWithCorrectData_whenValidationsHavePassed() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setValidFrom(LocalDate.of(2021, 5, 5));
		discountCode.setValidTo(LocalDate.of(2021, 5, 30));
		discountCode.setDiscountPercentage(15);
		discountCode.setOrders(Set.of());
		discountCode.setIsSingleUse(false);
		discountCode.setIsOncePerUser(false);
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));
		given(this.discountCodesRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(30);
		discountCodeEditDto.setValidFrom(LocalDate.of(2021, 5, 1));
		discountCodeEditDto.setValidFrom(LocalDate.of(2021, 5, 15));
		discountCodeEditDto.setIsOncePerUser(true);
		discountCodeEditDto.setIsSingleUse(true);

		this.restaurantsDiscountCodesService.editDiscountCode("restaurantId", "discountCodeId", discountCodeEditDto);

		then(this.discountCodesRepository).should(times(1)).save(discountCode);
		then(this.discountCodesRepository).shouldHaveNoMoreInteractions();

		assertThat(discountCode)
				.usingRecursiveComparison()
				.ignoringFields("code", "isDeleted", "deletedOn", "restaurant", "orders", "id", "createdOn")
				.isEqualTo(discountCodeEditDto);
	}

	@Test
	public void editDiscountCodeShould_returnTheDiscountCodeWithCorrectData_whenValidationsHavePassed() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setValidFrom(LocalDate.of(2021, 5, 5));
		discountCode.setValidTo(LocalDate.of(2021, 5, 30));
		discountCode.setDiscountPercentage(15);
		discountCode.setOrders(Set.of());
		discountCode.setIsSingleUse(false);
		discountCode.setIsOncePerUser(false);
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));
		given(this.discountCodesRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(30);
		discountCodeEditDto.setValidFrom(LocalDate.of(2021, 5, 1));
		discountCodeEditDto.setValidTo(LocalDate.of(2021, 5, 15));
		discountCodeEditDto.setIsOncePerUser(true);
		discountCodeEditDto.setIsSingleUse(true);

		DiscountCodeListDto resultDiscountCodeDto = this.restaurantsDiscountCodesService
				.editDiscountCode("restaurantId", "discountCodeId", discountCodeEditDto);
		resultDiscountCodeDto.setTimesUsed(0);

		assertThat(resultDiscountCodeDto)
				.usingRecursiveComparison()
				.ignoringFields("code", "isDeleted", "deletedOn", "restaurant", "orders", "id", "createdOn",
						"timesUsed")
				.isEqualTo(discountCode);

		assertEquals(discountCode.getOrders().size(), resultDiscountCodeDto.getTimesUsed());
	}
	
	@Test
	public void editDiscountCodeShould_beSuccessful_whenValidationsHavePassedWithMultipleOrdersForDiscountCode() {
		given(this.foodOrderData.discountCodes()).willReturn(this.discountCodesRepository);

		Customer customer = new Customer();
		customer.setId("customerId");
		
		Order order = new Order();
		order.setCustomer(customer);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setId("discountCodeId");
		discountCode.setValidFrom(LocalDate.of(2021, 5, 5));
		discountCode.setValidTo(LocalDate.of(2021, 5, 30));
		discountCode.setDiscountPercentage(15);
		discountCode.setOrders(Set.of(order));
		discountCode.setIsSingleUse(false);
		discountCode.setIsOncePerUser(false);
		given(this.discountCodesRepository.findByIdAndRestaurant("discountCodeId", "restaurantId"))
				.willReturn(Optional.of(discountCode));
		given(this.discountCodesRepository.save(any())).willAnswer(answer -> answer.getArgument(0));

		DiscountCodeEditDto discountCodeEditDto = new DiscountCodeEditDto();
		discountCodeEditDto.setDiscountPercentage(15);
		discountCodeEditDto.setValidFrom(LocalDate.of(2021, 5, 5));
		discountCodeEditDto.setValidTo(LocalDate.of(2021, 6, 30));
		discountCodeEditDto.setIsOncePerUser(true);
		discountCodeEditDto.setIsSingleUse(true);

		DiscountCodeListDto resultDiscountCodeDto = this.restaurantsDiscountCodesService
				.editDiscountCode("restaurantId", "discountCodeId", discountCodeEditDto);
		resultDiscountCodeDto.setTimesUsed(1);

		assertThat(resultDiscountCodeDto)
				.usingRecursiveComparison()
				.ignoringFields("code", "isDeleted", "deletedOn", "restaurant", "orders", "id", "createdOn",
						"timesUsed")
				.isEqualTo(discountCode);

		assertEquals(discountCode.getOrders().size(), resultDiscountCodeDto.getTimesUsed());
	}
}
