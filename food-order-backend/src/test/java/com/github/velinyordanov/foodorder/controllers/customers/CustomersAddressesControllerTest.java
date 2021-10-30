package com.github.velinyordanov.foodorder.controllers.customers;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.Collection;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.NotValidCustomerArgumentsProvider;
import com.github.velinyordanov.foodorder.controllers.test.ValidUserProvider;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.security.CustomerAuthenticationProvider;
import com.github.velinyordanov.foodorder.security.RestaurantAuthenticationProvider;
import com.github.velinyordanov.foodorder.services.AuthenticationService;
import com.github.velinyordanov.foodorder.services.customers.CustomersAddressesService;

@WebMvcTest(CustomersAddressesController.class)
public class CustomersAddressesControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CustomersAddressesService customersAddressesService;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private RestaurantAuthenticationProvider restaurantAuthenticationProvider;

	@MockBean
	private CustomerAuthenticationProvider customerAuthenticationProvider;

	private ValidUserProvider validUserProvider = new ValidUserProvider();

	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void addAddressToCustomerShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer)
			throws Exception {
		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Sofia);
		addressCreateDto.setAddressType(AddressType.ApartmentBuilding);
		addressCreateDto.setApartmentBuildingNumber("3");
		addressCreateDto.setStreet("street");
		addressCreateDto.setStreetNumber("5");
		addressCreateDto.setNeighborhood("neighborhood");
		addressCreateDto.setApartmentNumber("9");
		addressCreateDto.setFloor((byte) 15);
		addressCreateDto.setEntrance("C");

		this.mockMvc.perform(post("/customers/customerId/addresses")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(addressCreateDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersAddressesService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource("getNotValidAddressCreateDtoArguments")
	public void addAddressToCustomerShould_returnBadRequest_whenAddressCreateDtoIsNotValid(String name, Object value,
			String expectedError)
			throws Exception {
		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Sofia);
		addressCreateDto.setAddressType(AddressType.ApartmentBuilding);
		addressCreateDto.setApartmentBuildingNumber("3");
		addressCreateDto.setStreet("street");
		addressCreateDto.setStreetNumber("5");
		addressCreateDto.setNeighborhood("neighborhood");
		addressCreateDto.setApartmentNumber("9");
		addressCreateDto.setFloor((byte) 15);
		addressCreateDto.setEntrance("C");

		Field field = addressCreateDto.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(addressCreateDto, value);

		Customer customer = this.validUserProvider.getValidCustomer();
		this.mockMvc.perform(post("/customers/customerId/addresses")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(addressCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.customersAddressesService).shouldHaveNoInteractions();
	}

	@Test
	public void addAddressToCustomerShould_returnOkAndCorrectData_whenAddressCreateDtoIsValid()
			throws Exception {
		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Sofia);
		addressCreateDto.setAddressType(AddressType.ApartmentBuilding);
		addressCreateDto.setApartmentBuildingNumber("3");
		addressCreateDto.setStreet("street");
		addressCreateDto.setStreetNumber("5");
		addressCreateDto.setNeighborhood("neighborhood");
		addressCreateDto.setApartmentNumber("9");
		addressCreateDto.setFloor((byte) 15);
		addressCreateDto.setEntrance("C");

		AddressDto addressDto = new AddressDto();
		addressDto.setId("addressId");
		addressDto.setCity(City.Sofia);
		addressDto.setAddressType(AddressType.ApartmentBuilding);
		addressDto.setApartmentBuildingNumber("3");
		addressDto.setStreet("street");
		addressDto.setStreetNumber("5");
		addressDto.setNeighborhood("neighborhood");
		addressDto.setApartmentNumber("9");
		addressDto.setFloor((byte) 15);
		addressDto.setEntrance("C");

		String expectedAddress = this.objectMapper.writeValueAsString(addressDto);

		given(this.customersAddressesService.addAddressToCustomer("customerId", addressCreateDto))
				.willReturn(addressDto);

		Customer customer = this.validUserProvider.getValidCustomer();
		this.mockMvc.perform(post("/customers/customerId/addresses")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(addressCreateDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedAddress, result.getResponse().getContentAsString()));

		then(this.customersAddressesService).should(times(1)).addAddressToCustomer("customerId", addressCreateDto);
		then(this.customersAddressesService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void updateAddressShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer)
			throws Exception {
		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Sofia);
		addressCreateDto.setAddressType(AddressType.ApartmentBuilding);
		addressCreateDto.setApartmentBuildingNumber("3");
		addressCreateDto.setStreet("street");
		addressCreateDto.setStreetNumber("5");
		addressCreateDto.setNeighborhood("neighborhood");
		addressCreateDto.setApartmentNumber("9");
		addressCreateDto.setFloor((byte) 15);
		addressCreateDto.setEntrance("C");

		this.mockMvc.perform(put("/customers/customerId/addresses/addressId")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(addressCreateDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersAddressesService).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@MethodSource("getNotValidAddressCreateDtoArguments")
	public void updateAddressShould_returnBadRequest_whenAddressCreateDtoIsNotValid(String name, Object value,
			String expectedError)
			throws Exception {
		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Sofia);
		addressCreateDto.setAddressType(AddressType.ApartmentBuilding);
		addressCreateDto.setApartmentBuildingNumber("3");
		addressCreateDto.setStreet("street");
		addressCreateDto.setStreetNumber("5");
		addressCreateDto.setNeighborhood("neighborhood");
		addressCreateDto.setApartmentNumber("9");
		addressCreateDto.setFloor((byte) 15);
		addressCreateDto.setEntrance("C");

		Field field = addressCreateDto.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(addressCreateDto, value);

		Customer customer = this.validUserProvider.getValidCustomer();
		this.mockMvc.perform(put("/customers/customerId/addresses/addressId")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(addressCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title", is("Validation errors")))
				.andExpect(jsonPath("$.description", containsString(expectedError)));

		then(this.customersAddressesService).shouldHaveNoInteractions();
	}

	@Test
	public void updateAddressShould_returnOkAndCorrectData_whenAddressCreateDtoIsValid()
			throws Exception {
		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Sofia);
		addressCreateDto.setAddressType(AddressType.ApartmentBuilding);
		addressCreateDto.setApartmentBuildingNumber("3");
		addressCreateDto.setStreet("street");
		addressCreateDto.setStreetNumber("5");
		addressCreateDto.setNeighborhood("neighborhood");
		addressCreateDto.setApartmentNumber("9");
		addressCreateDto.setFloor((byte) 15);
		addressCreateDto.setEntrance("C");

		AddressDto addressDto = new AddressDto();
		addressDto.setId("addressId");
		addressDto.setCity(City.Sofia);
		addressDto.setAddressType(AddressType.ApartmentBuilding);
		addressDto.setApartmentBuildingNumber("3");
		addressDto.setStreet("street");
		addressDto.setStreetNumber("5");
		addressDto.setNeighborhood("neighborhood");
		addressDto.setApartmentNumber("9");
		addressDto.setFloor((byte) 15);
		addressDto.setEntrance("C");

		String expectedAddress = this.objectMapper.writeValueAsString(addressDto);

		given(this.customersAddressesService.editAddress("customerId", "addressId", addressCreateDto))
				.willReturn(addressDto);

		Customer customer = this.validUserProvider.getValidCustomer();
		this.mockMvc.perform(put("/customers/customerId/addresses/addressId")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(addressCreateDto)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedAddress, result.getResponse().getContentAsString()));

		then(this.customersAddressesService).should(times(1)).editAddress("customerId", "addressId", addressCreateDto);
		then(this.customersAddressesService).shouldHaveNoMoreInteractions();
	}

	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void getAddressesForCustomerShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer)
			throws Exception {
		this.mockMvc.perform(get("/customers/customerId/addresses")
				.with(user(customer)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersAddressesService).shouldHaveNoInteractions();
	}

	@Test
	public void getAddressesForCustomerShould_returnOkWithCorrectData_whenUserIsAuthorized()
			throws Exception {
		Customer customer = this.validUserProvider.getValidCustomer();

		AddressDto addressDto = new AddressDto();
		addressDto.setId("addressId");
		addressDto.setCity(City.Sofia);
		addressDto.setAddressType(AddressType.ApartmentBuilding);
		addressDto.setApartmentBuildingNumber("3");
		addressDto.setStreet("street");
		addressDto.setStreetNumber("5");
		addressDto.setNeighborhood("neighborhood");
		addressDto.setApartmentNumber("9");
		addressDto.setFloor((byte) 15);
		addressDto.setEntrance("C");

		AddressDto addressDto2 = new AddressDto();
		addressDto2.setId("addressId2");
		addressDto2.setCity(City.Burgas);
		addressDto2.setAddressType(AddressType.OfficeBuilding);
		addressDto2.setApartmentBuildingNumber("5");
		addressDto2.setStreet("street2");
		addressDto2.setStreetNumber("3");
		addressDto2.setNeighborhood("neighborhood2");
		addressDto2.setApartmentNumber("5");
		addressDto2.setFloor((byte) 15);
		addressDto2.setEntrance("E");

		Collection<AddressDto> addressesToReturn = List.of(addressDto, addressDto2);
		String expectedResponse = this.objectMapper.writeValueAsString(addressesToReturn);

		given(this.customersAddressesService.getAddressesForCustomer("customerId")).willReturn(addressesToReturn);
		this.mockMvc.perform(get("/customers/customerId/addresses")
				.with(user(customer)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}
	
	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void deleteCustomerAddressShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer)
			throws Exception {
		this.mockMvc.perform(delete("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersAddressesService).shouldHaveNoInteractions();
	}
	
	@Test
	public void deleteCustomerAddressShould_returnOk_whenUserIsAuthorized()
			throws Exception {
		Customer customer = this.validUserProvider.getValidCustomer();
		
		this.mockMvc.perform(delete("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResponse().getContentAsString().isEmpty()));

		then(this.customersAddressesService).should(times(1)).deleteCustomerAddress("customerId", "addressId");
		then(this.customersAddressesService).shouldHaveNoMoreInteractions();
	}
	
	@ParameterizedTest
	@ArgumentsSource(NotValidCustomerArgumentsProvider.class)
	public void getCustomerAddressShould_returnUnauthorized_whenUserIsNotAuthorized(Customer customer)
			throws Exception {
		this.mockMvc.perform(get("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));

		then(this.customersAddressesService).shouldHaveNoInteractions();
	}
	
	@Test
	public void getCustomerAddressShould_returnOkWithCorrectData_whenUserIsAuthorized()
			throws Exception {
		Customer customer = this.validUserProvider.getValidCustomer();

		AddressDto addressDto = new AddressDto();
		addressDto.setId("addressId");
		addressDto.setCity(City.Sofia);
		addressDto.setAddressType(AddressType.ApartmentBuilding);
		addressDto.setApartmentBuildingNumber("3");
		addressDto.setStreet("street");
		addressDto.setStreetNumber("5");
		addressDto.setNeighborhood("neighborhood");
		addressDto.setApartmentNumber("9");
		addressDto.setFloor((byte) 15);
		addressDto.setEntrance("C");
		String expectedResponse = this.objectMapper.writeValueAsString(addressDto);
		
		given(this.customersAddressesService.getCustomerAddress("customerId", "addressId")).willReturn(addressDto);
		
		this.mockMvc.perform(get("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isOk())
				.andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
	}

	private static Stream<Arguments> getNotValidAddressCreateDtoArguments() {
		return Stream.of(
				Arguments.of("city", null, EMPTY_CITY),
				Arguments.of("addressType", null, EMPTY_ADDRESS_TYPE),
				Arguments.of("neighborhood", "a", NEIGHBORHOOD_OUT_OF_BOUNDS),
				Arguments.of("neighborhood",
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						NEIGHBORHOOD_OUT_OF_BOUNDS),
				Arguments.of("street", "a", STREET_OUT_OF_BOUNDS),
				Arguments.of("street",
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						STREET_OUT_OF_BOUNDS),
				Arguments.of("streetNumber", "", STREET_NUMBER_OUT_OF_BOUNDS),
				Arguments.of("streetNumber", "aaaaaaaaaaa", STREET_NUMBER_OUT_OF_BOUNDS),
				Arguments.of("apartmentBuildingNumber", "", APARTMENT_BUILDING_NUMBER_OUT_OF_BOUNDS),
				Arguments.of("apartmentBuildingNumber", "aaaaaaaaaaa", APARTMENT_BUILDING_NUMBER_OUT_OF_BOUNDS),
				Arguments.of("entrance", "", ENTRANCE_OUT_OF_BOUNDS),
				Arguments.of("entrance", "aaaaaaaaaaa", ENTRANCE_OUT_OF_BOUNDS),
				Arguments.of("floor", (byte) -5, FLOOR_NEGATIVE),
				Arguments.of("apartmentNumber", "", APARTMENT_NUMBER_OUT_OF_BOUNDS),
				Arguments.of("apartmentNumber", "aaaaaaaaaaa", APARTMENT_NUMBER_OUT_OF_BOUNDS));
	}
}
