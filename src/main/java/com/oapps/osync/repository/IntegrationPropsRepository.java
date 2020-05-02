package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.IntegrationPropsEntity;

public interface IntegrationPropsRepository extends JpaRepository<IntegrationPropsEntity, Integer> {

	IntegrationPropsEntity findByOsyncId(Long osyncId);

	IntegrationPropsEntity findByOsyncIdAndAndIntegId(Long osyncId, Long integId);

}
