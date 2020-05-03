package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.ServiceInfoEntity;


public interface ServiceInfoRepository extends CrudRepository<ServiceInfoEntity, Integer> {

	ServiceInfoEntity findByServiceId(Long serviceId);
	
	ServiceInfoEntity findByName(String name);

	ServiceInfoEntity save(ServiceInfoEntity authInfoObj);
	
}

