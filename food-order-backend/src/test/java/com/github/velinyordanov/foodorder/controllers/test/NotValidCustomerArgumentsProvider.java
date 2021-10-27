package com.github.velinyordanov.foodorder.controllers.test;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Customer;

public class NotValidCustomerArgumentsProvider implements ArgumentsProvider {
	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		Customer customerWithDifferentId = new Customer();
		customerWithDifferentId.setId("notCustomerId");
		Authority authority = new Authority();
		authority.setAuthority("ROLE_CUSTOMER");
		customerWithDifferentId.setAuthorities(Set.of(authority));
		
		Customer customerWithRestaurantAuthority = new Customer();
		customerWithRestaurantAuthority.setId("customerId");
		Authority restaurantAuthority = new Authority();
		authority.setAuthority("ROLE_RESTAURANT");
		customerWithRestaurantAuthority.setAuthorities(Set.of(authority));

		Customer customerWithUnknownAuthority = new Customer();
		customerWithUnknownAuthority.setId("customerId");
		Authority unknownAuthority = new Authority();
		unknownAuthority.setAuthority("ROLE_DOES_NOT_EXIST");
		customerWithUnknownAuthority.setAuthorities(Set.of(unknownAuthority));

		Customer customerWithNoAuthorities = new Customer();
		customerWithNoAuthorities.setId("restaurantId");
		customerWithNoAuthorities.setAuthorities(Set.of());
		
		Customer customerWithDifferentIdAndAuthority = new Customer();
		customerWithDifferentIdAndAuthority.setId("notCustomerId");
		customerWithDifferentIdAndAuthority.setAuthorities(Set.of(restaurantAuthority));

		return Stream.of(
				Arguments.of(customerWithDifferentId),
				Arguments.of(customerWithRestaurantAuthority),
				Arguments.of(customerWithUnknownAuthority),
				Arguments.of(customerWithNoAuthorities),
				Arguments.of(customerWithDifferentIdAndAuthority));
	}
}
