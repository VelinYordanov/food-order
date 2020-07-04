package com.github.velinyordanov.foodorder.data;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Category;

@Repository
public interface CategoriesRepository extends BaseRepository<Category> {
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where RestaurantId = ?1 and e.isDeleted = false")
    Collection<Category> findByRestaurantId(String restaurantId);
}
