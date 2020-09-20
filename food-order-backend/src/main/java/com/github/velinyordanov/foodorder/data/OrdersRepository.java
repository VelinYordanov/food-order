package com.github.velinyordanov.foodorder.data;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Order;

@Repository
public interface OrdersRepository extends BaseRepository<Order> {
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where customer.id = ?1 AND e.isDeleted = false")
    Collection<Order> findByCustomerId(String customerId);

    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where restaurant.id = ?1 AND e.isDeleted = false")
    Collection<Order> findByRestaurantId(String restaurantId);
}
