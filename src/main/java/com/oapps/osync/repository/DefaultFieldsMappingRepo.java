package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.DefaultFieldMapEntity;

@Repository
public interface DefaultFieldsMappingRepo extends JpaRepository<DefaultFieldMapEntity, Long> {

	@Query(value = "SELECT * FROM default_field_map dfm WHERE dfm.left_service_id=? AND dfm.left_module_id=? AND dfm.right_service_id=? AND dfm.right_module_id=?", nativeQuery = true)

	List<DefaultFieldMapEntity> findAllDefaultMappings(Long leftServiceId, Long leftModuleId, Long rightServiceId,
			Long rightModuleId);
}
