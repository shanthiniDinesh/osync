package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.ModuleInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;

public interface ModuleInfoRepository extends CrudRepository<ModuleInfoEntity, Integer> {

	ModuleInfoEntity findByModuleId(Long moduleId);
	
	ModuleInfoEntity findByServiceId(Long serviceId);
	
	ModuleInfoEntity save(ModuleInfoEntity moduleInfoObj);

}
