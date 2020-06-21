package com.github.velinyordanov.foodorder.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodsRepository;
import com.github.velinyordanov.foodorder.data.entities.Food;

@Service
public class FoodsServiceImpl {
    private final FoodsRepository foodsRepository;

    @Autowired
    public FoodsServiceImpl(FoodsRepository foodsRepository) {
	this.foodsRepository = foodsRepository;
    }

    public Optional<Food> getById(String id) {
	return this.foodsRepository.findById(id);
    }

    public void createFood(Food food) {
	this.foodsRepository.save(food);
    }
}
