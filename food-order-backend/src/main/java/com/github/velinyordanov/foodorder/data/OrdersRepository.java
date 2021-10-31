package com.github.velinyordanov.foodorder.data;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Order;
import com.github.velinyordanov.foodorder.dto.GraphData;

@Repository
public interface OrdersRepository extends BaseRepository<Order> {
	@Transactional(readOnly = true)
	@Query("select e from #{#entityName} e where e.customer.id = ?1 and e.isDeleted = false")
	Page<Order> findByCustomerId(String customerId, Pageable pageable);
	
	@Transactional(readOnly = true)
	@Query("select e from #{#entityName} e where e.id = ?1 and e.customer.id = ?2 and e.isDeleted = false")
	Optional<Order> findByIdAndCustomerId(String id, String customerId);

	@Transactional(readOnly = true)
	@Query("select e from #{#entityName} e where e.restaurant.id = ?1 and e.isDeleted = false")
	Page<Order> findByRestaurantId(String restaurantId, Pageable page);

	@Query("select new com.github.velinyordanov.foodorder.dto.GraphData(CAST(e.createdOn as date), count(e))"
			+ "from #{#entityName} e"
			+ " where e.restaurant.id = ?1 and year(e.createdOn) = ?3 and month(e.createdOn) = ?2 "
			+ "group by CAST(e.createdOn as date)")
	Collection<GraphData<Date, Long>> getOrderMonthlyGraphData(String restaurantId, int month, int year);

	@Query("select new com.github.velinyordanov.foodorder.dto.GraphData(month(e.createdOn), count(e)) "
			+ "from #{#entityName} e where e.restaurant.id = ?1 and year(e.createdOn) = ?2 "
			+ "group by month(e.createdOn)")
	Collection<GraphData<Integer, Long>> getYearlyGraphData(String restaurantId, int year);
}
