package com.github.velinyordanov.foodorder.services.customers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.github.velinyordanov.foodorder.data.AddressesRepository;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Address;
import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.AddressCreateDto;
import com.github.velinyordanov.foodorder.dto.AddressDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.enums.City;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;

@ExtendWith(MockitoExtension.class)
public class CustomersAddressesServiceImplTest {
	@Mock
	private FoodOrderData foodOrderData;

	@Mock
	private CustomersRepository customersRepository;

	@Mock
	private AddressesRepository addressesRepository;

	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());

	@InjectMocks
	private CustomersAddressesServiceImpl customersAddressesServiceImpl;

	@Test
	public void addAddressToCustomerShould_throwNotFoundException_whenCustomerIsNotFound() {
		given(this.customersRepository.findById("customerId")).willReturn(Optional.empty());
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersAddressesServiceImpl.addAddressToCustomer("customerId", new AddressCreateDto()));
		
		assertEquals("Customer with id customerId not found", exc.getMessage());
	}

	@Test
	public void addAddressToCustomerShould_saveTheAddress_whenCustomerIsFound() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setAddresses(new HashSet<>());

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.save(any())).willAnswer(answer -> answer.getArgument(0));
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);

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

		ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);

		this.customersAddressesServiceImpl.addAddressToCustomer("customerId", addressCreateDto);

		then(this.addressesRepository).should(times(1)).save(captor.capture());
		then(this.addressesRepository).shouldHaveNoMoreInteractions();

		Address address = captor.getValue();
		assertThat(address)
				.usingRecursiveComparison()
				.ignoringFields("deletedOn", "createdOn", "isDeleted", "id", "customer")
				.isEqualTo(addressCreateDto);
		assertEquals(address.getCustomer(), customer);
		assertFalse(address.getIsDeleted());
	}

	@Test
	public void addAddressToCustomerShould_return_whenCustomerIsFound() {
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setAddresses(new HashSet<>());

		given(this.customersRepository.findById("customerId")).willReturn(Optional.of(customer));
		given(this.addressesRepository.save(any())).willAnswer(answer -> answer.getArgument(0));
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);

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

		AddressDto addressDto = this.customersAddressesServiceImpl.addAddressToCustomer("customerId", addressCreateDto);

		assertThat(addressDto)
				.usingRecursiveComparison()
				.ignoringFields("id")
				.isEqualTo(addressCreateDto);
	}

	@Test
	public void editAddressShould_throwNotFoundException_whenAddressIsNotFound() {
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId")).willReturn(Optional.empty());
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);

		NotFoundException exc = assertThrows(NotFoundException.class, () -> this.customersAddressesServiceImpl
				.editAddress("customerId", "addressId", new AddressCreateDto()));
		
		assertEquals("Address with id addressId not found", exc.getMessage());
	}

	@Test
	public void editAddressShould_saveTheAddressWithCorrectData_whenAddressIsFound() {
		Customer customer = new Customer();
		customer.setId("customerId");

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

		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.addressesRepository.save(address)).willReturn(address);

		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Plovdiv);
		addressCreateDto.setAddressType(AddressType.House);
		addressCreateDto.setApartmentBuildingNumber("5");
		addressCreateDto.setStreet("street3");
		addressCreateDto.setStreetNumber("3");
		addressCreateDto.setNeighborhood("neighborhood5");
		addressCreateDto.setApartmentNumber("15");
		addressCreateDto.setFloor((byte) 30);
		addressCreateDto.setEntrance("E");

		ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);

		this.customersAddressesServiceImpl.editAddress("customerId", "addressId", addressCreateDto);

		then(this.addressesRepository).should(times(1)).save(addressCaptor.capture());
		then(this.addressesRepository).shouldHaveNoMoreInteractions();

		Address capturedAddress = addressCaptor.getValue();
		assertThat(capturedAddress)
				.usingRecursiveComparison()
				.ignoringFields("deletedOn", "createdOn", "isDeleted", "id", "customer")
				.isEqualTo(addressCreateDto);
		assertEquals(capturedAddress.getId(), "addressId");
		assertEquals(capturedAddress.getCustomer(), customer);
		assertFalse(capturedAddress.getIsDeleted());
	}

	@Test
	public void editAddressShould_returnCorrectData_whenAddressIsFound() {
		Customer customer = new Customer();
		customer.setId("customerId");

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

		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId"))
				.willReturn(Optional.of(address));
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		given(this.addressesRepository.save(address)).willReturn(address);

		AddressCreateDto addressCreateDto = new AddressCreateDto();
		addressCreateDto.setCity(City.Plovdiv);
		addressCreateDto.setAddressType(AddressType.House);
		addressCreateDto.setApartmentBuildingNumber("5");
		addressCreateDto.setStreet("street3");
		addressCreateDto.setStreetNumber("3");
		addressCreateDto.setNeighborhood("neighborhood5");
		addressCreateDto.setApartmentNumber("15");
		addressCreateDto.setFloor((byte) 30);
		addressCreateDto.setEntrance("E");

		AddressDto result = this.customersAddressesServiceImpl.editAddress("customerId", "addressId", addressCreateDto);

		assertThat(result)
				.usingRecursiveComparison()
				.ignoringFields("id")
				.isEqualTo(addressCreateDto);
	}

	@Test
	public void getAddressesForCustomerShould_throwNotFoundException_whenCustomerIsNotFound() {
		given(this.customersRepository.existsById("customerId")).willReturn(false);
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);

		NotFoundException exc = assertThrows(NotFoundException.class,
				() -> this.customersAddressesServiceImpl.getAddressesForCustomer("customerId"));
		
		assertEquals("Customer with id customerId not found.", exc.getMessage());
	}

	@Test
	public void getAddressesForCustomerShould_returnTheAddressesOfTheCustomer_whenCustomerIsFound() {
		Customer customer = new Customer();
		customer.setId("customerId");

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

		Address address2 = new Address();
		address2.setCity(City.Plovdiv);
		address2.setAddressType(AddressType.House);
		address2.setApartmentBuildingNumber("5");
		address2.setStreet("street3");
		address2.setStreetNumber("3");
		address2.setNeighborhood("neighborhood5");
		address2.setApartmentNumber("15");
		address2.setFloor((byte) 30);
		address2.setEntrance("E");
		address2.setCustomer(customer);

		Address address3 = new Address();
		address3.setCity(City.Burgas);
		address3.setAddressType(AddressType.OfficeBuilding);
		address3.setApartmentBuildingNumber("9");
		address3.setStreet("street5");
		address3.setStreetNumber("9");
		address3.setNeighborhood("neighborhood3");
		address3.setApartmentNumber("21");
		address3.setFloor((byte) 23);
		address3.setEntrance("I");
		address3.setCustomer(customer);

		given(this.customersRepository.existsById("customerId")).willReturn(true);
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.addressesRepository.findByCustomerId("customerId")).willReturn(List.of(address, address2, address3));
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);

		Collection<AddressDto> result = this.customersAddressesServiceImpl.getAddressesForCustomer("customerId");

		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(List.of(address, address2, address3));
	}
	
	@Test
	public void getAddressesForCustomerShould_returnAnEmptyList_whenCustomerHasNoAddresses() {
		given(this.customersRepository.existsById("customerId")).willReturn(true);
		given(this.foodOrderData.customers()).willReturn(this.customersRepository);
		given(this.addressesRepository.findByCustomerId("customerId")).willReturn(List.of());
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);

		Collection<AddressDto> result = this.customersAddressesServiceImpl.getAddressesForCustomer("customerId");

		assertThat(result).isEmpty();
	}
	
	@Test
	public void deleteCustomerAddressShould_throwNotFoundException_whenAddressIsNotFound() {
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId")).willReturn(Optional.empty());
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		
		NotFoundException exc = assertThrows(NotFoundException.class, () -> this.customersAddressesServiceImpl.deleteCustomerAddress("customerId", "addressId"));
		
		assertEquals("No address with id addressId found", exc.getMessage());
	}
	
	@Test
	public void deleteCustomerAddressShould_setIsDeletedToTrueToRestaurant_whenAddressIsFound() {
		Address address = new Address();
		address.setIsDeleted(false);
		
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId")).willReturn(Optional.of(address));
		given(this.addressesRepository.save(address)).willReturn(address);
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		
		this.customersAddressesServiceImpl.deleteCustomerAddress("customerId", "addressId");
		
		then(this.addressesRepository).should(times(1)).save(address);
		then(this.addressesRepository).shouldHaveNoMoreInteractions();
		assertTrue(address.getIsDeleted());
	}
	
	@Test
	public void getCustomerAddressShould_throwNotFoundException_whenAddressIsNotFound() {
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId")).willReturn(Optional.empty());
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		
		NotFoundException exc = assertThrows(NotFoundException.class, () -> this.customersAddressesServiceImpl.getCustomerAddress("customerId", "addressId"));
		
		assertEquals("No address with id addressId found", exc.getMessage());
	}
	
	@Test
	public void getCustomerAddressShould_returnCorrectData_whenAddressIsFound() {
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
		
		given(this.addressesRepository.findByIdAndCustomerId("addressId", "customerId")).willReturn(Optional.of(address));
		given(this.foodOrderData.addresses()).willReturn(this.addressesRepository);
		
		AddressDto addressDto = this.customersAddressesServiceImpl.getCustomerAddress("customerId", "addressId");
		
		assertThat(addressDto).usingRecursiveComparison().isEqualTo(address);
	}
}
