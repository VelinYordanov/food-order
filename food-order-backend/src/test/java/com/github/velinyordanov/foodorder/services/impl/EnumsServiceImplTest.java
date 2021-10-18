package com.github.velinyordanov.foodorder.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.velinyordanov.foodorder.dto.EnumDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.enums.UserType;

@ExtendWith(MockitoExtension.class)
public class EnumsServiceImplTest {
	private final EnumsServiceImpl enumService = new EnumsServiceImpl();

	@ParameterizedTest
	@MethodSource("getEnumOptionsMethodSource")
	public <T extends Enum<T>> void getEnumOptionsShould_returnAllEnumOptionsAsAnIdValuePairCollection(
			Class<T> enumClass, Collection<EnumDto> result) {
		assertEquals(result, this.enumService.getEnumOptions(enumClass));
	}

	private static Stream<Arguments> getEnumOptionsMethodSource() {
		return Stream.of(
				Arguments.of(AddressType.class,
						List.of(
								new EnumDto(0, "Unspecified"),
								new EnumDto(1, "ApartmentBuilding"),
								new EnumDto(2, "House"),
								new EnumDto(3, "OfficeBuilding"),
								new EnumDto(4, "Other"))),
				Arguments.of(City.class,
						List.of(
								new EnumDto(0, "Sofia"),
								new EnumDto(1, "Plovdiv"),
								new EnumDto(2, "Burgas"))),
				Arguments.of(UserType.class,
						List.of(
								new EnumDto(0, "Customer"),
								new EnumDto(1, "Restaurant"))));
	}
}
