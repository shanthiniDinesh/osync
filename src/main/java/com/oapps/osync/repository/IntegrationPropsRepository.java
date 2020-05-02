package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.IntegrationPropsEntity;

public interface IntegrationPropsRepository extends JpaRepository<IntegrationPropsEntity, Long> {

	IntegrationPropsEntity findByOsyncId(Long osyncId);

	IntegrationPropsEntity findByOsyncIdAndAndIntegId(Long osyncId, Long integId);

}
