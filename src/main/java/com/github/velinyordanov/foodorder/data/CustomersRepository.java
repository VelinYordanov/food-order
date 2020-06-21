package com.github.velinyordanov.foodorder.data;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.github.velinyordanov.foodorder.data.entities.Customer;

@Repository
public interface CustomersRepository extends BaseRepository<Customer> {
    public Optional<Customer> findByUsername(String username);

    boolean existsByUsername(String username);
}
