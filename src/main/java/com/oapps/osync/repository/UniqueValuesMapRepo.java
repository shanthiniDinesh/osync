package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.UniqueValuesMap;

public interface UniqueValuesMapRepo extends CrudRepository<UniqueValuesMap, Integer> {

	UniqueValuesMap findByAccountIdAndLeftUniqueValue(String accountId, String leftUniqueValue);
	
	UniqueValuesMap findByAccountIdAndRightUniqueValue(String accountId, String rightUniqueValue);
	
	List<UniqueValuesMap> findByAccountIdAndLeftUniqueValueIn(String accountId, List<String> uniqueIns);
	
	List<UniqueValuesMap> findByAccountIdAndRightUniqueValueIn(String accountId, List<String> uids);
}
