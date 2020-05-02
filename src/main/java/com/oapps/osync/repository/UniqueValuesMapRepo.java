package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.UniqueValuesMapEntity;

public interface UniqueValuesMapRepo extends JpaRepository<UniqueValuesMapEntity, Long> {

	UniqueValuesMapEntity findByIntegIdAndLeftUniqueValue(Long integId, String leftUniqueValue);

	UniqueValuesMapEntity findByIntegIdAndRightUniqueValue(Long integId, String rightUniqueValue);

	List<UniqueValuesMapEntity> findByIntegIdAndLeftUniqueValueIn(Long integId, List<String> uniqueIns);

	List<UniqueValuesMapEntity> findByIntegIdAndRightUniqueValueIn(Long integId, List<String> uids);
}
