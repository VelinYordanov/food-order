package com.github.velinyordanov.foodorder.data;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.velinyordanov.foodorder.data.entities.DiscountCode;

@Repository
public interface DiscountCodesRepository extends BaseRepository<DiscountCode> {
	@Query("select e from #{#entityName} e where e.restaurant.id = ?1 and e.isDeleted = false")
	Collection<DiscountCode> findByRestaurant(String restaurantId);

	@Query("select e from #{#entityName} e where e.restaurant.id = ?1 and e.code = ?2")
	Optional<DiscountCode> findByCodeAndRestaurantIdWithDeleted(String restaurantId, String code);

	@Query("select e from #{#entityName} e where e.restaurant.id = ?1 and e.code = ?2 and e.isDeleted = false")
	Optional<DiscountCode> findByCodeAndRestaurantId(String restaurantId, String code);

	@Query("select e from #{#entityName} e where e.id = ?1 and e.restaurant.id = ?2 and e.isDeleted = false")
	Optional<DiscountCode> findByIdAndRestaurant(String discountCodeId, String restaurantId);
}
