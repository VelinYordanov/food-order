package com.github.velinyordanov.foodorder.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.velinyordanov.foodorder.data.entities.Customer;

@Repository
public interface CustomersRepository extends BaseRepository<Customer> {
    @Query("select e from #{#entityName} e where e.email = ?1 and e.isDeleted = false")
    public Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
}
