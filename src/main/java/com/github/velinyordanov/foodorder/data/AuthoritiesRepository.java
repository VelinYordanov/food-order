package com.github.velinyordanov.foodorder.data;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.github.velinyordanov.foodorder.data.entities.Authority;

@Repository
public interface AuthoritiesRepository extends BaseRepository<Authority> {
    Optional<Authority> findFirstByAuthority(String authority);
}
