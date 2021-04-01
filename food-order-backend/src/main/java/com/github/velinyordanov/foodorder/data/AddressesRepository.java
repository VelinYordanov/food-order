package com.github.velinyordanov.foodorder.data;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.data.entities.Address;

public interface AddressesRepository extends BaseRepository<Address> {
	@Transactional(readOnly = true)
	@Query("select e from #{#entityName} e where e.customer.id = ?1 and e.isDeleted = false")
	Collection<Address> findByCustomerId(String customerId);
}
