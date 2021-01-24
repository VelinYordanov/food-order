package com.github.velinyordanov.foodorder.controllers.restaurants;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsAuthenticationService;

@RestController
@RequestMapping("restaurants")
public class RestaurantsAuthenticationController {
    private final RestaurantsAuthenticationService restaurantsAuthenticationService;

    public RestaurantsAuthenticationController(RestaurantsAuthenticationService restaurantsAuthenticationService) {
	this.restaurantsAuthenticationService = restaurantsAuthenticationService;
    }

    @PostMapping()
    public JwtTokenDto register(@Valid @RequestBody RestaurantRegisterDto userDto) {
	return new JwtTokenDto(restaurantsAuthenticationService.register(userDto));
    }

    @PostMapping("tokens")
    public JwtTokenDto login(@Valid @RequestBody UserLoginDto user) {
	return new JwtTokenDto(restaurantsAuthenticationService.login(user));
    }
}
