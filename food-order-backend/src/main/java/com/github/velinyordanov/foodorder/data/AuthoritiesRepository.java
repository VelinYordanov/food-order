package com.github.velinyordanov.foodorder.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.velinyordanov.foodorder.data.entities.Authority;

@Repository
public interface AuthoritiesRepository extends BaseRepository<Authority> {
    @Query("select e from #{#entityName} e where e.authority = ?1 and e.isDeleted = false")
    Optional<Authority> findFirstByAuthority(String authority);
}
