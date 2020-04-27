package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.FieldMap;

public interface FieldMapRepository extends CrudRepository<FieldMap, Integer> {
	
	List<FieldMap> findByAccountId(String accountId);
}
