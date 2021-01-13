package com.github.velinyordanov.foodorder.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;

@Repository
public interface RestaurantsRepository extends BaseRepository<Restaurant> {
    @Query("select e from #{#entityName} e where e.email = ?1 and e.isDeleted = false")
    Optional<Restaurant> findByEmail(String email);

    boolean existsByEmailOrName(String username, String name);

    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.isDeleted = false")
    List<RestaurantDto> getRestaurantsList();
}
