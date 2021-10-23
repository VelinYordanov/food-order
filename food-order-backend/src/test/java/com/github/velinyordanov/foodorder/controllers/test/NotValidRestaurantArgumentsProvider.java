package com.github.velinyordanov.foodorder.controllers.test;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;

public class NotValidRestaurantArgumentsProvider implements ArgumentsProvider {
	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		Restaurant restaurantWithDifferentId = new Restaurant();
		restaurantWithDifferentId.setId("notRestaurantId");
		Authority restaurantAuthority = new Authority();
		restaurantAuthority.setAuthority("ROLE_RESTAURANT");
		restaurantWithDifferentId.setAuthorities(Set.of(restaurantAuthority));

		Restaurant restaurantWithCustomerAuthority = new Restaurant();
		restaurantWithCustomerAuthority.setId("restaurantId");
		Authority customersAuthority = new Authority();
		customersAuthority.setAuthority("ROLE_CUSTOMER");
		restaurantWithCustomerAuthority.setAuthorities(Set.of(customersAuthority));

		Restaurant restaurantWithUnknownAuthority = new Restaurant();
		restaurantWithUnknownAuthority.setId("restaurantId");
		Authority unknownAuthority = new Authority();
		unknownAuthority.setAuthority("ROLE_DOES_NOT_EXIST");
		restaurantWithUnknownAuthority.setAuthorities(Set.of(customersAuthority));

		Restaurant restaurantWithNoAuthorities = new Restaurant();
		restaurantWithUnknownAuthority.setId("restaurantId");
		restaurantWithNoAuthorities.setAuthorities(Set.of());
		
		Restaurant restaurantWithDifferentIdAndAuthority = new Restaurant();
		restaurantWithDifferentIdAndAuthority.setId("notRestaurantId");
		restaurantWithDifferentIdAndAuthority.setAuthorities(Set.of(customersAuthority));

		return Stream.of(
				Arguments.of(restaurantWithDifferentId),
				Arguments.of(restaurantWithCustomerAuthority),
				Arguments.of(restaurantWithUnknownAuthority),
				Arguments.of(restaurantWithNoAuthorities),
				Arguments.of(restaurantWithDifferentIdAndAuthority));
	}
}
