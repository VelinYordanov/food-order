package com.github.velinyordanov.foodorder.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Order;

@Repository
public interface OrdersRepository extends BaseRepository<Order> {
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.customer.id = ?1 and e.isDeleted = false")
    Page<Order> findByCustomerId(String customerId, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.restaurant.id = ?1 and e.isDeleted = false")
    Page<Order> findByRestaurantId(String restaurantId, Pageable page);
}
