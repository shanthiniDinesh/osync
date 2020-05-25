package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.ModuleInfoEntity;

@Repository
public interface ModuleInfoRepository extends CrudRepository<ModuleInfoEntity, Long> {

	ModuleInfoEntity findByModuleId(Long moduleId);
	
	ModuleInfoEntity findByServiceId(Long serviceId);
	
	ModuleInfoEntity save(ModuleInfoEntity moduleInfoObj);
	
	List<ModuleInfoEntity> findAllByServiceId(Long serviceId);

}
