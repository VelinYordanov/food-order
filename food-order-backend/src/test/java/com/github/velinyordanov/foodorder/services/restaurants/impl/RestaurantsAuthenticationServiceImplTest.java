package com.github.velinyordanov.foodorder.services.restaurants.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.velinyordanov.foodorder.data.AuthoritiesRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.mapping.impl.MapperImpl;
import com.github.velinyordanov.foodorder.services.JwtTokenService;

@ExtendWith(MockitoExtension.class)
public class RestaurantsAuthenticationServiceImplTest {
	@Mock
	private FoodOrderData foodOrderData;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Spy
	private MapperImpl mapper = new MapperImpl(new ModelMapper());
	
	@Mock
	private JwtTokenService jwtTokenService;
	
	@Mock
	private AuthoritiesRepository authoritiesRepository;
	
	@Mock
	private RestaurantsRepository restaurantsRepository;
	
	@InjectMocks
	private RestaurantsAuthenticationServiceImpl restaurantsAuthenticationService;
	
	@Test
	public void findByIdshould_ReturnFindByIdFromRestaurantsRepository() {
		Restaurant restaurant = new Restaurant();
		given(this.foodOrderData.restaurants()).willReturn(restaurantsRepository);
		given(this.foodOrderData.restaurants().findById("5")).willReturn(Optional.of(restaurant));
		
		Restaurant restaurantResult = this.restaurantsAuthenticationService.findById("5").get();
		
		then(this.foodOrderData.restaurants()).should().findById("5");
		assertEquals(restaurant, restaurantResult);
		
	}
	
	@Test
	public void registerShould_throwADuplicateExceptionWhenUserAlreadyExists() {
		RestaurantRegisterDto restaurantRegisterDto = new RestaurantRegisterDto();
		restaurantRegisterDto.setEmail("email@asd.com");
		restaurantRegisterDto.setName("name");
		
		given(this.foodOrderData.restaurants()).willReturn(restaurantsRepository);
		given(this.foodOrderData.restaurants().existsByEmailOrName(restaurantRegisterDto.getEmail(), restaurantRegisterDto.getName())).willReturn(true);
		
		assertThrows(DuplicateUserException.class, () -> this.restaurantsAuthenticationService.register(restaurantRegisterDto));
	}
	
	@Test
	public void registerShould_saveTheCorrectRestaurantIfHeDoesntExist() {
		String email = "email@asd.com";
		String name = "name";
		String password = "password";
		String description = "description";
		String encodedPassword = "encodedPassword";
		
		RestaurantRegisterDto restaurantRegisterDto = new RestaurantRegisterDto();
		restaurantRegisterDto.setEmail(email);
		restaurantRegisterDto.setName(name);
		restaurantRegisterDto.setPassword(password);
		restaurantRegisterDto.setDescription(description);
		
		Authority authority = new Authority();
		authority.setAuthority("ROLE_RESTAURANT");
		
		given(this.foodOrderData.restaurants()).willReturn(restaurantsRepository);
		given(this.foodOrderData.authorities()).willReturn(authoritiesRepository);
		given(this.authoritiesRepository.findFirstByAuthority("ROLE_RESTAURANT")).willReturn(Optional.of(authority));
		given(this.passwordEncoder.encode(restaurantRegisterDto.getPassword())).willReturn(encodedPassword);
		given(this.foodOrderData.restaurants().existsByEmailOrName(restaurantRegisterDto.getEmail(), restaurantRegisterDto.getName())).willReturn(false);
		
		this.restaurantsAuthenticationService.register(restaurantRegisterDto);
		
		ArgumentCaptor<Restaurant> argumentCaptor = ArgumentCaptor.forClass(Restaurant.class);
		then(this.foodOrderData.restaurants()).should().save(argumentCaptor.capture());
		
		Restaurant result = argumentCaptor.getValue();
		assertEquals(name, result.getName());
		assertEquals(encodedPassword, result.getPassword());
		assertEquals(description, result.getDescription());
		assertEquals(email, result.getEmail());
	}
	
	@Test
	public void registerShould_setTheCorrectAuthoritiesToTheRestaurantIfHeDoesntExist() {
		String email = "email@asd.com";
		String name = "name";
		String password = "password";
		String description = "description";
		String encodedPassword = "encodedPassword";
		
		RestaurantRegisterDto restaurantRegisterDto = new RestaurantRegisterDto();
		restaurantRegisterDto.setEmail(email);
		restaurantRegisterDto.setName(name);
		restaurantRegisterDto.setPassword(password);
		restaurantRegisterDto.setDescription(description);
		
		Authority authority = new Authority();
		authority.setAuthority("ROLE_RESTAURANT");
		
		given(this.foodOrderData.restaurants()).willReturn(restaurantsRepository);
		given(this.foodOrderData.authorities()).willReturn(authoritiesRepository);
		given(this.authoritiesRepository.findFirstByAuthority("ROLE_RESTAURANT")).willReturn(Optional.of(authority));
		given(this.passwordEncoder.encode(restaurantRegisterDto.getPassword())).willReturn(encodedPassword);
		given(this.foodOrderData.restaurants().existsByEmailOrName(restaurantRegisterDto.getEmail(), restaurantRegisterDto.getName())).willReturn(false);
		
		this.restaurantsAuthenticationService.register(restaurantRegisterDto);
		
		ArgumentCaptor<Restaurant> argumentCaptor = ArgumentCaptor.forClass(Restaurant.class);
		then(this.foodOrderData.restaurants()).should().save(argumentCaptor.capture());
		
		Restaurant result = argumentCaptor.getValue();
		assertTrue(authority.getRestaurants().stream().anyMatch(r -> result.equals(r)));
		assertTrue(result.getAuthorities().stream().anyMatch(a -> authority.equals(a)));
	}
	
	@Test
	public void registerShould_ReturnATokenWithTheSavedRestaurantIfUserDoesntExist() {
		String email = "email@asd.com";
		String name = "name";
		String password = "password";
		String description = "description";
		String encodedPassword = "encodedPassword";
		
		RestaurantRegisterDto restaurantRegisterDto = new RestaurantRegisterDto();
		restaurantRegisterDto.setEmail(email);
		restaurantRegisterDto.setName(name);
		restaurantRegisterDto.setPassword(password);
		restaurantRegisterDto.setDescription(description);
		
		Restaurant restaurant = new Restaurant();
		
		Authority authority = new Authority();
		authority.setAuthority("ROLE_RESTAURANT");
		
		given(this.foodOrderData.restaurants()).willReturn(restaurantsRepository);
		given(this.foodOrderData.authorities()).willReturn(authoritiesRepository);
		given(this.authoritiesRepository.findFirstByAuthority("ROLE_RESTAURANT")).willReturn(Optional.of(authority));
		given(this.passwordEncoder.encode(restaurantRegisterDto.getPassword())).willReturn(encodedPassword);
		given(this.foodOrderData.restaurants().existsByEmailOrName(restaurantRegisterDto.getEmail(), restaurantRegisterDto.getName())).willReturn(false);
		given(this.foodOrderData.restaurants().save(any(Restaurant.class))).willReturn(restaurant);
		
		this.restaurantsAuthenticationService.register(restaurantRegisterDto);
				
		then(this.jwtTokenService).should().generateToken(restaurant);
	}
	
	@Test
	public void loginShould_callAuthenticateWithTheCorrectToken() {
		String email = "email@asd.com";
		String password = "password";
		
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail(email);
		userLoginDto.setPassword(password);
		
		given(this.authenticationManager.authenticate(any(Authentication.class))).willReturn(mock(Authentication.class));
		
		this.restaurantsAuthenticationService.login(userLoginDto);
		
		ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
		then(this.authenticationManager).should().authenticate(authenticationCaptor.capture());
		Authentication result = authenticationCaptor.getValue();
		
		assertEquals(email, result.getPrincipal());
		assertEquals(password, result.getCredentials());
		assertEquals(UserType.Restaurant, result.getDetails());
	}
	
	@Test
	public void loginShould_generateTokenWithTheCorrectPrincipal() {
		String email = "email@asd.com";
		String password = "password";
		
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail(email);
		userLoginDto.setPassword(password);
		
		Authentication mockAuthentication = mock(Authentication.class);
		Restaurant principal = new Restaurant();
		
		given(mockAuthentication.getPrincipal()).willReturn(principal);
		given(this.authenticationManager.authenticate(any(Authentication.class))).willReturn(mockAuthentication);
		
		this.restaurantsAuthenticationService.login(userLoginDto);
		
		then(this.jwtTokenService).should().generateToken(principal);
	}
}
