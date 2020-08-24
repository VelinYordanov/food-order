package com.github.velinyordanov.foodorder.data.impl;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.AddressesRepository;
import com.github.velinyordanov.foodorder.data.AuthoritiesRepository;
import com.github.velinyordanov.foodorder.data.CategoriesRepository;
import com.github.velinyordanov.foodorder.data.CustomersRepository;
import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.FoodsRepository;
import com.github.velinyordanov.foodorder.data.OrdersRepository;
import com.github.velinyordanov.foodorder.data.RestaurantsRepository;

@Service
public class FoodOrderDataImpl implements FoodOrderData {
    private final AuthoritiesRepository authoritiesRepository;
    private final CategoriesRepository categoriesRepository;
    private final CustomersRepository customersRepository;
    private final FoodsRepository foodsRepository;
    private final OrdersRepository ordersRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final AddressesRepository addressesRepository;

    public FoodOrderDataImpl(
	    AuthoritiesRepository authoritiesRepository,
	    CategoriesRepository categoriesRepository,
	    AddressesRepository addressesRepository,
	    CustomersRepository customersRepository,
	    FoodsRepository foodsRepository,
	    OrdersRepository ordersRepository,
	    RestaurantsRepository restaurantsRepository) {
	this.authoritiesRepository = authoritiesRepository;
	this.categoriesRepository = categoriesRepository;
	this.addressesRepository = addressesRepository;
	this.customersRepository = customersRepository;
	this.foodsRepository = foodsRepository;
	this.ordersRepository = ordersRepository;
	this.restaurantsRepository = restaurantsRepository;
    }

    @Override
    public AuthoritiesRepository authorities() {
	return this.authoritiesRepository;
    }

    @Override
    public CategoriesRepository categories() {
	return this.categoriesRepository;
    }

    @Override
    public CustomersRepository customers() {
	return this.customersRepository;
    }

    @Override
    public FoodsRepository foods() {
	return this.foodsRepository;
    }

    @Override
    public OrdersRepository orders() {
	return this.ordersRepository;
    }

    @Override
    public RestaurantsRepository restaurants() {
	return this.restaurantsRepository;
    }

    @Override
    public AddressesRepository addresses() {
	return this.addressesRepository;
    }
}
