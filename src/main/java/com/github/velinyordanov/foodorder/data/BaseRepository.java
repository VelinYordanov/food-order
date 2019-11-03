package com.github.velinyordanov.foodorder.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import com.github.velinyordanov.foodorder.entities.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends CrudRepository<T, String> {
    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.isDeleted = false")
    List<T> findAll();

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id in ?1 and e.isDeleted = false")
    Iterable<T> findAllById(Iterable<String> ids);

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id = ?1 and e.isDeleted = false")
    Optional<T> findById(String id);

    // Look up deleted entities
    @Query("select e from #{#entityName} e where e.isDeleted = true")
    @Transactional(readOnly = true)
    List<T> findDeleted();

    @Override
    @Transactional(readOnly = true)
    @Query("select count(e) from #{#entityName} e where e.isDeleted = false")
    long count();

    @Override
    @Transactional(readOnly = true)
    default boolean existsById(String id) {
	return findById(id) != null;
    }

    @Override
    @Query("update #{#entityName} e set e.isDeleted=true where e.id = ?1")
    @Transactional
    @Modifying
    void deleteById(String id);

    @Override
    @Transactional
    default void delete(T entity) {
	deleteById(entity.getId());
    }

    @Override
    @Transactional
    default void deleteAll(Iterable<? extends T> entities) {
	entities.forEach(entitiy -> deleteById(entitiy.getId()));
    }

    @Override
    @Query("update #{#entityName} e set e.isDeleted=true")
    @Transactional
    @Modifying
    void deleteAll();
}