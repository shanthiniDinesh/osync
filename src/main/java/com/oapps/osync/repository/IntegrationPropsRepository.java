package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.IntegrationPropsEntity;

public interface IntegrationPropsRepository extends JpaRepository<IntegrationPropsEntity, Long> {

	IntegrationPropsEntity findByOsyncId(Long osyncId);

	IntegrationPropsEntity findByOsyncIdAndIntegId(Long osyncId, Long integId);

	IntegrationPropsEntity findTopByOsyncIdAndLeftServiceIdAndRightServiceId(Long osyncId, Long leftServiceId , Long rightServiceId);
	
	IntegrationPropsEntity save(IntegrationPropsEntity intInfoObj);		
	
}
