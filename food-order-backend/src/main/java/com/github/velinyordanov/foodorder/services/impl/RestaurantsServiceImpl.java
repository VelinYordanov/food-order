package com.github.velinyordanov.foodorder.services.impl;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Authority;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.DiscountCode;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeEditDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeListDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.dto.GraphData;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderStatusDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.enums.UserType;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.exceptions.ExistingDiscountCodeException;
import com.github.velinyordanov.foodorder.exceptions.NonEmptyCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.DateService;
import com.github.velinyordanov.foodorder.services.DiscountCodesService;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;

@Service
@Transactional
public class RestaurantsServiceImpl implements RestaurantsService {
    private final FoodOrderData foodOrderData;
    private final JwtTokenService jwtTokenService;
    private final SimpMessagingTemplate messagingTemplate;
    private final DiscountCodesService discountCodesService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;
    private final DateService dateService;

    @Autowired
    public RestaurantsServiceImpl(
	    FoodOrderData foodOrderData,
	    JwtTokenService jwtTokenService,
	    SimpMessagingTemplate messagingTemplate,
	    AuthenticationManager authenticationManager,
	    DiscountCodesService discountCodesService,
	    PasswordEncoder passwordEncoder,
	    Mapper mapper,
	    DateService dateService) {
	this.foodOrderData = foodOrderData;
	this.authenticationManager = authenticationManager;
	this.messagingTemplate = messagingTemplate;
	this.jwtTokenService = jwtTokenService;
	this.passwordEncoder = passwordEncoder;
	this.discountCodesService = discountCodesService;
	this.mapper = mapper;
	this.dateService = dateService;
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
    public FoodDto addFoodToRestaurant(String restaurantId, FoodCreateDto foodCreateDto) {
	Optional<Restaurant> restaurantOptional = this.foodOrderData.restaurants().findById(restaurantId);
	if (restaurantOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found!", restaurantId));
	}

	Restaurant restaurant = restaurantOptional.get();

	Food food = this.mapper.map(foodCreateDto, Food.class);

	Collection<String> categoryIds =
		food.getCategories()
			.stream()
			.map(x -> x.getId())
			.collect(Collectors.toList());

	Set<Category> existingCategories = new HashSet<>();
	this.foodOrderData.categories()
		.findAllById(categoryIds)
		.forEach(existingCategories::add);

	existingCategories.forEach(category -> {
	    if (!restaurant.getId().equals(category.getRestaurant().getId())) {
		throw new RuntimeException("Category belongs to another restaurant");
	    }

	    food.removeCategory(category);
	    category.addFood(food);
	});

	Collection<Category> newCategories = food.getCategoriesWithDeleted()
		.stream()
		.filter(category -> !existingCategories.contains(category))
		.collect(Collectors.toList());

	newCategories.forEach(newCategory -> {
	    restaurant.getCategoriesWithDeleted()
		    .stream()
		    .filter(category -> category.getName().equals(newCategory.getName()))
		    .findFirst()
		    .ifPresentOrElse(category -> {
			if (category.getIsDeleted()) {
			    category.setIsDeleted(false);
			    food.removeCategory(newCategory);
			    category.addFood(food);
			} else {
			    throw new DuplicateCategoryException(
				    MessageFormat.format(
					    "Category with name: {0} already exists for this restaurant.",
					    newCategory.getName()));
			}
		    }, () -> {
			restaurant.addCategory(newCategory);
			newCategory.addFood(food);
		    });
	});

	return this.mapper.map(this.foodOrderData.foods().save(food), FoodDto.class);
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
    public FoodDto editFood(String restaurantId, String foodId, FoodCreateDto foodCreateDto) {
	Optional<Food> foodOptional = this.foodOrderData.foods().findById(foodId);
	if (foodOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Food with id {0} not found", foodId));
	}

	Food food = foodOptional.get();

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

	return this.mapper.map(this.foodOrderData.foods().save(food), FoodDto.class);
    }

    @Override
    public RestaurantDataDto editRestaurant(String restaurantId, RestaurantEditDto restaurantEditDto) {
	return this.foodOrderData.restaurants()
		.findById(restaurantId)
		.map(restaurant -> {
		    restaurant.setName(restaurantEditDto.getName());
		    restaurant.setDescription(restaurantEditDto.getDescription());

		    RestaurantDataDto restaurantDataDto =
			    this.mapper.map(this.foodOrderData.restaurants().save(restaurant), RestaurantDataDto.class);
		    restaurantDataDto.setFoods(
			    this.foodOrderData.foods()
				    .findByRestaurantId(restaurantId)
				    .stream()
				    .map(food -> this.mapper.map(food, FoodDto.class))
				    .collect(Collectors.toList()));

		    return restaurantDataDto;
		})
		.orElseThrow(() -> new NotFoundException(
			MessageFormat.format("Restaurant with id {0} not found!", restaurantId)));
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

    @Override
    public Page<OrderDto> getRestaurantOrders(String restaurantId, Pageable pageable) {
	return this.foodOrderData.orders()
		.findByRestaurantId(restaurantId, pageable)
		.map(order -> this.mapper.map(order, OrderDto.class));
    }

    @Override
    public OrderDto getRestaurantOrder(String restaurantId, String orderId) {
	Order order = this.foodOrderData.orders()
		.findById(orderId)
		.orElseThrow(() -> new NotFoundException("Order not found!"));

	if (!restaurantId.equals(order.getRestaurant().getId())) {
	    throw new NotFoundException("No such order found for restaurant.");
	}

	return this.mapper.map(order, OrderDto.class);
    }

    @Override
    public DiscountCodeDto addDiscountCodeToRestaurant(String restaurantId, DiscountCodeCreateDto discountCode) {
	if (discountCode.getValidFrom().isAfter(discountCode.getValidTo())) {
	    throw new BadRequestException("Valid from cannot be later than valid to.");
	}

	Restaurant restaurant = this.foodOrderData.restaurants()
		.findById(restaurantId)
		.orElseThrow(() -> new NotFoundException("Restaurant not found"));

	discountCode.setCode(discountCode.getCode().toUpperCase());
	Optional<DiscountCode> discountCodeOptional =
		this.foodOrderData.discountCodes()
			.findByCodeAndRestaurantIdWithDeleted(restaurantId, discountCode.getCode());
	if (discountCodeOptional.isPresent()) {
	    DiscountCode code = discountCodeOptional.get();
	    throw new ExistingDiscountCodeException(
		    MessageFormat.format("Discount code {0} already exists for restaurant {1}",
			    code.getCode(),
			    code.getRestaurant().getName()));
	}

	DiscountCode discountCodeToBeAdded = this.mapper.map(discountCode, DiscountCode.class);
	discountCodeToBeAdded.setRestaurant(restaurant);

	return this.mapper.map(this.foodOrderData.discountCodes().save(discountCodeToBeAdded), DiscountCodeDto.class);
    }

    @Override
    public DiscountCodeDto getDiscountByCode(String restaurantId, String code, String customerId) {
	DiscountCode discountCode = this.foodOrderData.discountCodes()
		.findByCodeAndRestaurantId(restaurantId, code)
		.orElseThrow(() -> new NotFoundException("Discount code not found"));

	this.discountCodesService.validateDiscountCode(discountCode, customerId);

	return this.mapper.map(discountCode, DiscountCodeDto.class);
    }

    @Override
    public OrderStatusDto updateRestaurantOrderStatus(
	    String restaurantId,
	    String orderId,
	    OrderStatusDto orderStatusDto) {
	Order order = this.foodOrderData.orders()
		.findById(orderId)
		.filter(restaurantOrder -> restaurantId.equals(restaurantOrder.getRestaurant().getId()))
		.orElseThrow(() -> new BadRequestException("No such order found for restaurant!"));

	order.setStatus(orderStatusDto.getStatus());

	OrderStatusDto result = new OrderStatusDto(this.foodOrderData.orders().save(order).getStatus());

	this.messagingTemplate.convertAndSend(
		MessageFormat.format(
			"/notifications/customers/{0}/orders/{1}",
			order.getCustomer().getId(),
			order.getId()),
		result);

	return result;
    }

    @Override
    public Collection<DiscountCodeListDto> getDiscountCodesForRestaurant(String restaurantId) {
	return this.foodOrderData.discountCodes()
		.findByRestaurant(restaurantId)
		.stream()
		.map(discountCode -> {
		    DiscountCodeListDto discountCodeListDto = this.mapper.map(discountCode, DiscountCodeListDto.class);
		    discountCodeListDto.setTimesUsed(discountCode.getOrders().size());
		    return discountCodeListDto;
		})
		.collect(Collectors.toList());
    }

    @Override
    public DiscountCodeDto deleteDiscountCode(String restaurantId, String discountCodeId) {
	DiscountCode code = this.foodOrderData.discountCodes()
		.findByIdAndRestaurant(discountCodeId, restaurantId)
		.orElseThrow(() -> new NotFoundException("Discount code not found"));

	if (!code.getOrders().isEmpty()) {
	    throw new BadRequestException("Discount code has orders associated with it and cannot be deleted.");
	}

	this.foodOrderData.discountCodes().delete(code);

	return this.mapper.map(code, DiscountCodeDto.class);
    }

    @Override
    public DiscountCodeListDto
	    editDiscountCode(String restaurantId, String discountCodeId, DiscountCodeEditDto discountCode) {
	if (discountCode.getValidFrom().isAfter(discountCode.getValidTo())) {
	    throw new BadRequestException("Valid from cannot be later than valid to.");
	}

	DiscountCode code = this.foodOrderData.discountCodes()
		.findByIdAndRestaurant(discountCodeId, restaurantId)
		.orElseThrow(() -> new NotFoundException("Discount code not found"));

	if (!code.getOrders().isEmpty()) {
	    if (discountCode.getDiscountPercentage() != code.getDiscountPercentage()) {
		throw new BadRequestException(
			"Discount percentage can be changed only to discount codes that have not been used yet.");
	    }

	    if (!discountCode.getValidFrom().isEqual(code.getValidFrom())) {
		throw new BadRequestException("Valid from can only be changed for orders that have not been used yet.");
	    }
	}

	this.mapper.map(discountCode, code);

	DiscountCode savedDiscountCode = this.foodOrderData.discountCodes().save(code);

	DiscountCodeListDto result =
		this.mapper.map(savedDiscountCode, DiscountCodeListDto.class);
	result.setTimesUsed(savedDiscountCode.getOrders().size());

	return result;
    }

    @Override
    public Collection<GraphData<LocalDate, Long>> getOrderMonthlyGraphData(String restaurantId, int year, int month) {
	Collection<GraphData<LocalDate, Long>> result = this.foodOrderData.orders()
		.getOrderMonthlyGraphData(restaurantId, month, year)
		.stream()
		.map(graphData -> new GraphData<LocalDate, Long>(
			graphData.getX().toLocalDate(),
			graphData.getY()))
		.collect(Collectors.toList());

	int daysForMonth = this.dateService.getNumberOfDaysForMonth(year, month);

	for (int i = 1; i <= daysForMonth; i++) {
	    LocalDate date = LocalDate.of(year, month, i);
	    if (result.stream()
		    .filter(graphData -> date.isEqual(graphData.getX()))
		    .findFirst()
		    .isEmpty()) {
		result.add(new GraphData<LocalDate, Long>(date, 0l));
	    }
	}

	return result.stream()
		.sorted(Comparator.comparing(GraphData::getX))
		.collect(Collectors.toList());
    }

    @Override
    public Collection<GraphData<String, Long>> getYearlyGraphData(String restaurantId, int year) {
	Collection<GraphData<Integer, Long>> result = this.foodOrderData.orders()
		.getYearlyGraphData(restaurantId, year);

	for (int i = 1; i <= 12; i++) {
	    int current = i;
	    if (result.stream().filter(graphData -> current == graphData.getX()).findAny().isEmpty()) {
		result.add(new GraphData<Integer, Long>(i, 0l));
	    }
	}

	return result
		.stream()
		.sorted(Comparator.comparing(GraphData::getX))
		.map(graphData -> new GraphData<String, Long>(this.dateService.getMonthName(graphData.getX()),
			graphData.getY()))
		.collect(Collectors.toList());
    }
}
