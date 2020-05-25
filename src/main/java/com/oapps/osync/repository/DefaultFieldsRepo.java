package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.DefaultFieldEntity;

@Repository
public interface DefaultFieldsRepo extends JpaRepository<DefaultFieldEntity, Long> {
	List<DefaultFieldEntity> findByServiceIdAndModuleId(Long serviceId, Long moduleId);
}
