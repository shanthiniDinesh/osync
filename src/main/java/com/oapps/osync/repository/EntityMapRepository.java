package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.EntityMap;

public interface EntityMapRepository extends CrudRepository<EntityMap, Integer> {

	EntityMap findByAccountId(String accountId);

}
