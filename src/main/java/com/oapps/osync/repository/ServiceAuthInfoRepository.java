package com.oapps.osync.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.ServiceAuthInfoEntity;

public interface ServiceAuthInfoRepository extends CrudRepository<ServiceAuthInfoEntity, Integer> {

	ServiceAuthInfoEntity findByServiceId(Long serviceId);
	
	ServiceAuthInfoEntity findTopByOsyncIdAndServiceId(Long osyncId, Long serviceId);
	
	ServiceAuthInfoEntity findTopByIntegIdAndServiceId(Long integId, Long serviceId);
	
	ServiceAuthInfoEntity findByOsyncUserId(Long osyncUserId);
	
	ServiceAuthInfoEntity save(ServiceAuthInfoEntity authInfoObj);
	
	ServiceAuthInfoEntity findByOsyncIdAndServiceIdAndIntegId(Long osyncId, Long serviceId, Long integId);
	
}