package com.github.velinyordanov.foodorder.data;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Food;

@Repository
public interface FoodsRepository extends BaseRepository<Food> {
	@Transactional(readOnly = true)
	@Query("select distinct e from #{#entityName} e INNER JOIN e.categories c WHERE c.restaurant.id = ?1 AND e.isDeleted = false")
	Collection<Food> findByRestaurantId(String restaurantId);
}
