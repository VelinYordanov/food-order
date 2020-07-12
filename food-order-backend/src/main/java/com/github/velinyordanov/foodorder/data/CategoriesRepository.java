package com.github.velinyordanov.foodorder.data;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Category;

@Repository
public interface CategoriesRepository extends BaseRepository<Category> {
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.restaurant.id = ?1 and e.isDeleted = false")
    Collection<Category> findByRestaurantId(String restaurantId);

    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.name = ?1")
    Optional<Category> findByNameIncludingDeleted(String name);
}
