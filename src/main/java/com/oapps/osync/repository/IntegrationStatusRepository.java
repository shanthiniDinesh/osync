package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.IntegrationStatusEntity;

@Repository
public interface IntegrationStatusRepository extends JpaRepository<IntegrationStatusEntity, Long> {

	IntegrationStatusEntity findByOsyncIdAndAndIntegId(Long osyncId, Long integId);

}
