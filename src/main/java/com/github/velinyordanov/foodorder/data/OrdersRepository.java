package com.github.velinyordanov.foodorder.data;

import org.springframework.stereotype.Repository;

import com.github.velinyordanov.foodorder.data.entities.Order;

@Repository
public interface OrdersRepository extends BaseRepository<Order> {

}
