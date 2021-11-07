package com.github.velinyordanov.foodorder.controllers.customers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.velinyordanov.foodorder.controllers.test.ValidUserProvider;
import com.github.velinyordanov.foodorder.data.AddressesRepository;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomersAddressesControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AddressesRepository addressesRepository;

	@Autowired
	private CustomersRepository customersRepository;

	private final ValidUserProvider validUserProvider = new ValidUserProvider();

	private Customer customer;

	@BeforeEach
	public void beforeEach() {
		Customer customer = this.validUserProvider.getValidCustomer();
		customer.setEmail("validEmail@asd.asd");
		customer.setPassword(this.passwordEncoder.encode("Password_123"));
		this.customer = this.customersRepository.save(customer);
	}

	@Test
	public void addAddressToCustomerShould_addAddressToTheDatabase_whenDataIsValid()
			throws JsonProcessingException, Exception {
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

		MvcResult mvcResult = this.mockMvc.perform(post("/customers/customerId/addresses")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(addressCreateDto)))
				.andExpect(status().isOk())
				.andReturn();

		Optional<Address> savedAddressOptional = this.addressesRepository.findByCustomerId("customerId").stream()
				.findFirst();
		assertTrue(savedAddressOptional.isPresent());
		assertThat(savedAddressOptional.get())
				.usingRecursiveComparison()
				.ignoringFields("deletedOn", "createdOn", "isDeleted", "id", "customer")
				.isEqualTo(addressCreateDto);
		assertEquals(savedAddressOptional.get().getCustomer(), this.customer);

		AddressDto result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AddressDto.class);
		assertThat(result)
				.usingRecursiveComparison()
				.ignoringFields("id")
				.isEqualTo(addressCreateDto);
	}

	@Test
	public void updateAddressShould_updateTheAddressInTheDatabase_whenDataIsValid()
			throws JsonProcessingException, Exception {
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
		this.addressesRepository.save(address);

		AddressCreateDto updatedAddressCreateDto = new AddressCreateDto();
		updatedAddressCreateDto.setCity(City.Burgas);
		updatedAddressCreateDto.setAddressType(AddressType.OfficeBuilding);
		updatedAddressCreateDto.setApartmentBuildingNumber("5");
		updatedAddressCreateDto.setStreet("someStreet");
		updatedAddressCreateDto.setStreetNumber("9");
		updatedAddressCreateDto.setNeighborhood("neighborhood3");
		updatedAddressCreateDto.setApartmentNumber("3");
		updatedAddressCreateDto.setFloor((byte) 3);
		updatedAddressCreateDto.setEntrance("E");

		MvcResult mvcResult = this.mockMvc.perform(put("/customers/customerId/addresses/addressId")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(updatedAddressCreateDto)))
				.andExpect(status().isOk())
				.andReturn();

		Optional<Address> updatedAddressOptional = this.addressesRepository.findById("addressId");
		assertTrue(updatedAddressOptional.isPresent());
		assertThat(updatedAddressOptional.get())
				.usingRecursiveComparison()
				.ignoringFields("deletedOn", "createdOn", "isDeleted", "id", "customer")
				.isEqualTo(updatedAddressCreateDto);

		assertEquals(updatedAddressOptional.get().getCustomer(), this.customer);

		AddressDto result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AddressDto.class);
		assertThat(result)
				.usingRecursiveComparison()
				.ignoringFields("id")
				.isEqualTo(updatedAddressCreateDto);

	}

	@Test
	public void updateAddressShould_returnNotFound_whenAddressIsNotFound()
			throws JsonProcessingException, Exception {
		AddressCreateDto updatedAddressCreateDto = new AddressCreateDto();
		updatedAddressCreateDto.setCity(City.Burgas);
		updatedAddressCreateDto.setAddressType(AddressType.OfficeBuilding);
		updatedAddressCreateDto.setApartmentBuildingNumber("5");
		updatedAddressCreateDto.setStreet("someStreet");
		updatedAddressCreateDto.setStreetNumber("9");
		updatedAddressCreateDto.setNeighborhood("neighborhood3");
		updatedAddressCreateDto.setApartmentNumber("3");
		updatedAddressCreateDto.setFloor((byte) 3);
		updatedAddressCreateDto.setEntrance("E");

		this.mockMvc.perform(put("/customers/customerId/addresses/addressId")
				.with(user(customer))
				.contentType("application/json")
				.content(this.objectMapper.writeValueAsString(updatedAddressCreateDto)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getAddressesForCustomerShould_returnTheAddressesOfTheCustomer_whenCustomerIsFound() throws Exception {
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

		Address address2 = new Address();
		address2.setId("addressId2");
		address2.setCity(City.Sofia);
		address2.setAddressType(AddressType.ApartmentBuilding);
		address2.setApartmentBuildingNumber("3");
		address2.setStreet("street");
		address2.setStreetNumber("5");
		address2.setNeighborhood("neighborhood");
		address2.setApartmentNumber("9");
		address2.setFloor((byte) 15);
		address2.setEntrance("C");
		address2.setCustomer(customer);

		this.addressesRepository.save(address);
		this.addressesRepository.save(address2);

		MvcResult mvcResult = this.mockMvc.perform(get("/customers/customerId/addresses")
				.with(user(customer)))
				.andExpect(status().isOk())
				.andReturn();

		Collection<AddressDto> result = Arrays
				.asList(this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
						AddressDto[].class));

		assertThat(result)
				.hasSize(2)
				.usingRecursiveComparison()
				.ignoringFields("deletedOn", "createdOn", "isDeleted", "id", "customer")
				.isEqualTo(List.of(address, address2));
	}

	@Test
	public void deleteShould_setIsDeletedToTrueToAddress_whenCustomerAddressIsFound() throws Exception {
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
		this.addressesRepository.save(address);

		this.mockMvc.perform(delete("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isOk());

		assertTrue(
				this.addressesRepository.findDeleted().stream().filter(a -> a.equals(address)).findAny().isPresent());
	}

	@Test
	public void deleteShould_returnNotFound_whenAddressIsNotFound() throws Exception {
		this.mockMvc.perform(delete("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getCustomerAddressShould_returnAddress_whenAddressIsFound() throws Exception {
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
		this.addressesRepository.save(address);

		MvcResult mvcResult = this.mockMvc.perform(get("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isOk())
				.andReturn();

		AddressDto result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AddressDto.class);

		assertThat(result)
				.usingRecursiveComparison()
				.ignoringFields("id")
				.isEqualTo(address);
	}

	@Test
	public void getCustomerAddressShould_returnNotFound_whenAddressIsNotFound() throws Exception {
		this.mockMvc.perform(get("/customers/customerId/addresses/addressId")
				.with(user(customer)))
				.andExpect(status().isNotFound());
	}
}
