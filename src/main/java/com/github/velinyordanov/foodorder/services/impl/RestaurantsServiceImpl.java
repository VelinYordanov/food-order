package com.github.velinyordanov.foodorder.services.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.AuthoritiesRepository;
import com.github.velinyordanov.foodorder.data.CategoriesRepository;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;

@Service
public class RestaurantsServiceImpl implements RestaurantsService {
    private final RestaurantsRepository restaurantsRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final CategoriesRepository categoriesRepository;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    @Autowired
    public RestaurantsServiceImpl(
	    RestaurantsRepository restaurantsRepository,
	    AuthoritiesRepository authoritiesRepository,
	    CategoriesRepository categoriesRepository,
	    JwtTokenService jwtTokenService,
	    AuthenticationManager authenticationManager,
	    PasswordEncoder passwordEncoder,
	    Mapper mapper) {
	this.restaurantsRepository = restaurantsRepository;
	this.authoritiesRepository = authoritiesRepository;
	this.categoriesRepository = categoriesRepository;
	this.authenticationManager = authenticationManager;
	this.jwtTokenService = jwtTokenService;
	this.passwordEncoder = passwordEncoder;
	this.mapper = mapper;
    }

    @Override
    public Collection<RestaurantDto> getAll() {
	return this.restaurantsRepository.getRestaurantsList();
    }

    public Optional<Restaurant> getById(String id) {
	return this.restaurantsRepository.findById(id);
    }

    public void createRestaurant(Restaurant restaurant) {
	this.restaurantsRepository.save(restaurant);
    }

    @Override
    public Optional<Restaurant> findById(String id) {
	return this.restaurantsRepository.findById(id);
    }

    @Override
    @Transactional
    public String register(RestaurantRegisterDto user) {
	if (this.restaurantsRepository.existsByUsernameOrName(user.getUsername(), user.getName())) {
	    throw new DuplicateUserException("Username or restaurant name already exists!");
	}

	Restaurant restaurant = this.mapper.map(user, Restaurant.class);
	restaurant.setPassword(this.passwordEncoder.encode(user.getPassword()));
	Optional<Authority> authorityOptional = this.authoritiesRepository.findFirstByAuthority("ROLE_RESTAURANT");
	Authority authority = null;
	if (authorityOptional.isPresent()) {
	    authority = authorityOptional.get();
	} else {
	    authority = new Authority();
	    authority.setAuthority("ROLE_RESTAURANT");
	}

	Set<Authority> authorities = new HashSet<>();
	authorities.add(authority);
	restaurant.setAuthorities(authorities);
	authority.getRestaurants().add(restaurant);

	Restaurant savedRestaurant = this.restaurantsRepository.save(restaurant);
	return this.jwtTokenService.generateToken(savedRestaurant);
    }

    @Override
    public void addFoodsToRestaurant(String restaurantId, FoodCreateDto foodCreateDto) {
	this.restaurantsRepository.findById(restaurantId).ifPresent(restaurant -> {
	    Food food = this.mapper.map(foodCreateDto, Food.class);

	    Collection<String> categoryIds =
		    food.getCategories()
			    .stream()
			    .map(x -> x.getId())
			    .collect(Collectors.toList());

	    Set<Category> categories = new HashSet<>();
	    this.categoriesRepository
		    .findAllById(categoryIds)
		    .forEach(categories::add);

	    categories.forEach(category -> {
		if (!restaurant.getId().equals(category.getRestaurant().getId())) {
		    throw new RuntimeException("Category belongs to another restaurant");
		}

		category.getFoods().add(food);
	    });

	    food.getCategories().removeAll(categories);
	    Set<Category> restaurantCategories = restaurant.getCategories();
	    food.getCategories().forEach(category -> {
		if (restaurantCategories.stream().anyMatch(x -> x.getName().equals(category.getName()))) {
		    throw new DuplicateCategoryException(
			    MessageFormat.format(
				    "Category with name: {0} already exists for this restaurant.",
				    category.getName()));
		}

		restaurant.getCategories().add(category);
		category.setRestaurant(restaurant);
		category.getFoods().add(food);
	    });

	    food.setCategories(null);
	    this.restaurantsRepository.save(restaurant);
	});
    }

    @Override
    public String login(UserDto user) {
	UsernamePasswordAuthenticationToken token =
		new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
	token.setDetails(UserType.Restaurant);

	Authentication authentication = this.authenticationManager.authenticate(token);
	return this.jwtTokenService.generateToken((Restaurant) authentication.getPrincipal());
    }
}
