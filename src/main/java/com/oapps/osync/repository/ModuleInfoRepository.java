package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.ModuleInfoEntity;

public interface ModuleInfoRepository extends CrudRepository<ModuleInfoEntity, Long> {

	ModuleInfoEntity findByModuleId(Long moduleId);
	
	ModuleInfoEntity findByServiceId(Long serviceId);
	
	ModuleInfoEntity save(ModuleInfoEntity moduleInfoObj);

}
