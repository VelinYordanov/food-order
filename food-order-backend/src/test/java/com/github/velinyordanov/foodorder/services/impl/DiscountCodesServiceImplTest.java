package com.github.velinyordanov.foodorder.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.services.DateService;

@ExtendWith(MockitoExtension.class)
public class DiscountCodesServiceImplTest {
	@Mock
	private DateService dateService;
	
	@InjectMocks
	private DiscountCodesServiceImpl discountCodesServiceImpl;
	
	@Test
	public void validateDiscountCodeShould_throwBadRequestException_whenDiscountCodeIsNotYetAvailable() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(true);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		
		BadRequestException exc = assertThrows(BadRequestException.class, () -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
		
		assertEquals("Discount code discountCode is not available yet. Try again later.", exc.getMessage());
	}
	
	@Test
	public void validateDiscountCodeShould_throwBadRequestException_whenDiscountCodeIsExpired() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(false);
		given(this.dateService.isInThePast(LocalDate.of(2021, Month.FEBRUARY, 15))).willReturn(true);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCode.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		BadRequestException exc = assertThrows(BadRequestException.class, () -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
		
		assertEquals("Discount code discountCode is expired.", exc.getMessage());
	}
	
	@Test
	public void validateDiscountCodeShould_throwBadRequestException_whenDiscountCodeIsSingleUseAndWasUsed() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(false);
		given(this.dateService.isInThePast(LocalDate.of(2021, Month.FEBRUARY, 15))).willReturn(false);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setIsSingleUse(true);
		discountCode.setOrders(Set.of(new Order()));
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCode.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		BadRequestException exc = assertThrows(BadRequestException.class, () -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
		
		assertEquals("Discount code discountCode was already used.", exc.getMessage());
	}
	
	@Test
	public void validateDiscountCodeShould_notThrow_whenDiscountCodeIsSingleUseAndHasNoOrders() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(false);
		given(this.dateService.isInThePast(LocalDate.of(2021, Month.FEBRUARY, 15))).willReturn(false);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setIsSingleUse(true);
		discountCode.setOrders(Set.of());
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCode.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		assertDoesNotThrow(() -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
	}
	
	@Test
	public void validateDiscountCodeShould_notThrow_whenDiscountCodeHasNoOrders() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(false);
		given(this.dateService.isInThePast(LocalDate.of(2021, Month.FEBRUARY, 15))).willReturn(false);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setOrders(Set.of());
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCode.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		assertDoesNotThrow(() -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
	}
	
	@Test
	public void validateDiscountCodeShould_notThrow_whenDiscountCodeHasOrders() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(false);
		given(this.dateService.isInThePast(LocalDate.of(2021, Month.FEBRUARY, 15))).willReturn(false);
		
		Customer customer = new Customer();
		customer.setId("customerId");
		
		Order order = new Order();
		order.setCustomer(customer);
		
		Customer customer2 = new Customer();
		customer2.setId("customer2Id");
		
		Order order2 = new Order();
		order2.setCustomer(customer2);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setOrders(Set.of(order, order2));
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCode.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		assertDoesNotThrow(() -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
	}
	
	@Test
	public void validateDiscountCodeShould_throwBadRequestException_whenDiscountCodeIsOncePerUserAndUserHasAlreadyUsedIt() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(false);
		given(this.dateService.isInThePast(LocalDate.of(2021, Month.FEBRUARY, 15))).willReturn(false);
		
		Customer customer = new Customer();
		customer.setId("customerId");
		
		Order order = new Order();
		order.setCustomer(customer);
		
		Customer customer2 = new Customer();
		customer2.setId("customer2Id");
		
		Order order2 = new Order();
		order2.setCustomer(customer2);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setIsOncePerUser(true);
		discountCode.setOrders(Set.of(order, order2));
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCode.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		BadRequestException exc = assertThrows(BadRequestException.class, () -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
		
		assertEquals("You have already used code discountCode.", exc.getMessage());
	}
	
	@Test
	public void validateDiscountCodeShould_notThrow_whenDiscountCodeIsOncePerUserAndUserHasNotUsedIt() {
		given(this.dateService.isInTheFuture(LocalDate.of(2021, Month.JANUARY, 15))).willReturn(false);
		given(this.dateService.isInThePast(LocalDate.of(2021, Month.FEBRUARY, 15))).willReturn(false);
		
		Customer customer = new Customer();
		customer.setId("notCustomerId");
		
		Order order = new Order();
		order.setCustomer(customer);
		
		Customer customer2 = new Customer();
		customer2.setId("customer2Id");
		
		Order order2 = new Order();
		order2.setCustomer(customer2);
		
		DiscountCode discountCode = new DiscountCode();
		discountCode.setCode("discountCode");
		discountCode.setIsOncePerUser(true);
		discountCode.setOrders(Set.of(order, order2));
		discountCode.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCode.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		assertDoesNotThrow(() -> this.discountCodesServiceImpl.validateDiscountCode(discountCode, "customerId"));
	}
}
