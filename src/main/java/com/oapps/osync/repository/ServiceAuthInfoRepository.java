package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oapps.osync.entity.ServiceAuthInfoEntity;

@Repository
public interface ServiceAuthInfoRepository extends CrudRepository<ServiceAuthInfoEntity, Long> {

	ServiceAuthInfoEntity findByServiceId(Long serviceId);
	
	ServiceAuthInfoEntity findTopByOsyncIdAndServiceId(Long osyncId, Long serviceId);
	
	ServiceAuthInfoEntity findTopByIntegIdAndServiceId(Long osyncId, Long serviceId);
	
	ServiceAuthInfoEntity findByOsyncUserId(Long osyncUserId);
	
	@Transactional
	ServiceAuthInfoEntity save(ServiceAuthInfoEntity authInfoObj);
	
	ServiceAuthInfoEntity findByOsyncIdAndServiceIdAndIntegId(Long osyncId, Long serviceId, Long integId);
	
}