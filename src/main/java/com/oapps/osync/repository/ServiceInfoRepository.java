package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.ServiceInfoEntity;

@Repository
public interface ServiceInfoRepository extends JpaRepository<ServiceInfoEntity, Long> {

	ServiceInfoEntity findByServiceId(Long serviceId);
	
	ServiceInfoEntity findByName(String name);

	ServiceInfoEntity save(ServiceInfoEntity authInfoObj);
	
}

