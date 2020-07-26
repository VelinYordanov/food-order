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

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.exceptions.NonEmptyCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;

@Service
@Transactional
public class RestaurantsServiceImpl implements RestaurantsService {
    private final FoodOrderData foodOrderData;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    @Autowired
    public RestaurantsServiceImpl(
	    FoodOrderData foodOrderData,
	    JwtTokenService jwtTokenService,
	    AuthenticationManager authenticationManager,
	    PasswordEncoder passwordEncoder,
	    Mapper mapper) {
	this.foodOrderData = foodOrderData;
	this.authenticationManager = authenticationManager;
	this.jwtTokenService = jwtTokenService;
	this.passwordEncoder = passwordEncoder;
	this.mapper = mapper;
    }

    @Override
    public Collection<RestaurantDto> getAll() {
	return this.foodOrderData.restaurants().getRestaurantsList();
    }

    public Optional<Restaurant> getById(String id) {
	return this.foodOrderData.restaurants().findById(id);
    }

    public void createRestaurant(Restaurant restaurant) {
	this.foodOrderData.restaurants().save(restaurant);
    }

    @Override
    public Optional<Restaurant> findById(String id) {
	return this.foodOrderData.restaurants().findById(id);
    }

    @Override
    public String register(RestaurantRegisterDto user) {
	if (this.foodOrderData.restaurants().existsByUsernameOrName(user.getUsername(), user.getName())) {
	    throw new DuplicateUserException("Username or restaurant name already exists!");
	}

	Restaurant restaurant = this.mapper.map(user, Restaurant.class);
	restaurant.setPassword(this.passwordEncoder.encode(user.getPassword()));
	Optional<Authority> authorityOptional =
		this.foodOrderData.authorities().findFirstByAuthority("ROLE_RESTAURANT");
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

	Restaurant savedRestaurant = this.foodOrderData.restaurants().save(restaurant);
	return this.jwtTokenService.generateToken(savedRestaurant);
    }

    @Override
    public void addFoodsToRestaurant(String restaurantId, FoodCreateDto foodCreateDto) {
	this.foodOrderData.restaurants().findById(restaurantId).ifPresentOrElse((restaurant -> {
	    Food food = this.mapper.map(foodCreateDto, Food.class);

	    Collection<String> categoryIds =
		    food.getCategories()
			    .stream()
			    .map(x -> x.getId())
			    .collect(Collectors.toList());

	    Set<Category> categories = new HashSet<>();
	    this.foodOrderData.categories()
		    .findAllById(categoryIds)
		    .forEach(categories::add);

	    categories.forEach(category -> {
		if (!restaurant.getId().equals(category.getRestaurant().getId())) {
		    throw new RuntimeException("Category belongs to another restaurant");
		}

		category.addFood(food);
	    });

	    food.getCategories().removeAll(categories);
	    Collection<Category> restaurantCategories =
		    this.foodOrderData.categories().findByRestaurantId(restaurantId);
	    food.getCategories().forEach(category -> {
		if (restaurantCategories.stream().anyMatch(x -> x.getName().equals(category.getName()))) {
		    throw new DuplicateCategoryException(
			    MessageFormat.format(
				    "Category with name: {0} already exists for this restaurant.",
				    category.getName()));
		}

		restaurant.addCategory(category);
		category.addFood(food);
	    });

	    food.setCategories(null);
	    this.foodOrderData.restaurants().save(restaurant);
	}), () -> {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found!", restaurantId));
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

    @Override
    public void editFood(String restaurantId, String foodId, FoodCreateDto foodCreateDto) {
	this.foodOrderData.foods().findById(foodId).ifPresentOrElse((food -> {
	    food.setName(foodCreateDto.getName());
	    food.setPrice(foodCreateDto.getPrice());
	    food.setDescription(foodCreateDto.getDescription());

	    Collection<Category> categories = this.foodOrderData.categories().findByRestaurantId(restaurantId);
	    Collection<String> selectedCategoryIds =
		    foodCreateDto.getCategories().stream().map(x -> x.getId()).collect(Collectors.toList());
	    categories.forEach(category -> {
		if (selectedCategoryIds.contains(category.getId())) {
		    category.addFood(food);
		} else {
		    category.removeFood(food);
		}
	    });

	    this.foodOrderData.foods().save(food);
	}), () -> {
	    throw new NotFoundException(MessageFormat.format("Food with id {0} not found", foodId));
	});
    }

    @Override
    public void editRestaurant(String restaurantId, RestaurantEditDto restaurantEditDto) {
	this.foodOrderData.restaurants().findById(restaurantId).ifPresentOrElse((restaurant -> {
	    restaurant.setName(restaurantEditDto.getName());
	    restaurant.setDescription(restaurantEditDto.getDescription());

	    this.foodOrderData.restaurants().save(restaurant);
	}), () -> {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found!", restaurantId));
	});
    }

    @Override
    public void deleteCategory(String restaurantId, String categoryId) {
	this.foodOrderData.restaurants().findById(restaurantId).ifPresentOrElse(restaurant -> {
	    this.foodOrderData.categories()
		    .findByRestaurantId(restaurantId)
		    .stream()
		    .filter(category -> categoryId.equals(category.getId()))
		    .findFirst()
		    .ifPresentOrElse(category -> {
			if (category.getFoods().isEmpty()) {
			    this.foodOrderData.categories().delete(category);
			} else {
			    throw new NonEmptyCategoryException(MessageFormat
				    .format("Category {0} has foods associated with it!", category.getName()));
			}
		    }, () -> {
			throw new NotFoundException(
				MessageFormat.format("Cateogry with id {0} not found!", categoryId));
		    });
	}, () -> {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found!", restaurantId));
	});
    }

    @Override
    public void deleteFood(String restaurantId, String foodId) {
	this.foodOrderData.restaurants().findById(restaurantId).ifPresentOrElse(restaurant -> {
	    this.foodOrderData.foods()
		    .findById(foodId)
		    .ifPresentOrElse(food -> {
			if (food.getCategories()
				.stream()
				.allMatch(category -> restaurantId.equals(category.getRestaurant().getId()))) {
			    this.foodOrderData.foods().delete(food);
			} else {
			    throw new NotFoundException("Could not find food for restaurant");
			}
		    }, () -> {
			throw new NotFoundException(
				MessageFormat.format("Could not find food with id {0}", foodId));
		    });
	}, () -> {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
	});
    }

    @Override
    public Optional<CategoryDto> addCategoryForRestaurant(String restaurantId, CategoryCreateDto categoryCreateDto) {
	Optional<Restaurant> restaurantOptional = this.foodOrderData
		.restaurants()
		.findById(restaurantId);

	if (restaurantOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
	}

	Optional<Category> categoryOptional =
		restaurantOptional
			.flatMap(restaurant -> this.foodOrderData
				.categories()
				.findByNameIncludingDeleted(categoryCreateDto.getName()));

	if (categoryOptional.isEmpty()) {
	    Category category = this.mapper.map(categoryCreateDto, Category.class);
	    category.setRestaurant(restaurantOptional.get());
	    return Optional.of(this.mapper.map(this.foodOrderData.categories().save(category), CategoryDto.class));
	} else {
	    Category category = categoryOptional.get();
	    if (category.getIsDeleted()) {
		category.setIsDeleted(false);
		return Optional.of(this.mapper.map(this.foodOrderData.categories().save(category), CategoryDto.class));
	    } else {
		throw new DuplicateCategoryException(MessageFormat
			.format("Category {0} already exists", categoryCreateDto.getName()));
	    }
	}
    }

    @Override
    public RestaurantDataDto getRestaurantData(String restaurantId) {
	Optional<Restaurant> restaurantOptional =
		this.foodOrderData.restaurants()
			.findById(restaurantId);

	if (restaurantOptional.isPresent()) {
	    Restaurant restaurant = restaurantOptional.get();
	    RestaurantDataDto restaurantData = this.mapper.map(restaurant, RestaurantDataDto.class);

	    restaurantData.setFoods(
		    this.foodOrderData.foods()
			    .findByRestaurantId(restaurantId)
			    .stream()
			    .map(food -> this.mapper.map(food, FoodDto.class))
			    .collect(Collectors.toList()));

	    return restaurantData;
	} else {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
	}
    }

    @Override
    public Collection<CategoryDto> getCategoriesForRestaurant(String restaurantId) {
	Optional<Restaurant> restaurantOptional =
		this.foodOrderData.restaurants()
			.findById(restaurantId);

	if (restaurantOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
	}

	return restaurantOptional
		.map(restaurant -> this.foodOrderData.categories()
			.findByRestaurantId(restaurant.getId())
			.stream()
			.map(category -> this.mapper.map(category, CategoryDto.class))
			.collect(Collectors.toList()))
		.orElseThrow(() -> new IllegalStateException(
			"An error occurred while loading categories. Try again later."));
    }
}
