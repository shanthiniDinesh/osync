package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.FieldMapEntity;

public interface FieldMapRepository extends JpaRepository<FieldMapEntity, Integer> {

	List<FieldMapEntity> findByOsyncIdAndIntegId(Long osyncId, Long integId);
}
