package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.FieldMapEntity;

public interface FieldMapRepository extends JpaRepository<FieldMapEntity, Long> {

	List<FieldMapEntity> findAllByIntegId(Long integId);
}
