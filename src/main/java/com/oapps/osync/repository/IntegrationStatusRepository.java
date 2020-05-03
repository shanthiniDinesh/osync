package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.IntegrationStatusEntity;

public interface IntegrationStatusRepository extends JpaRepository<IntegrationStatusEntity, Long> {

	IntegrationStatusEntity findByOsyncIdAndAndIntegId(Long osyncId, Long integId);

}
