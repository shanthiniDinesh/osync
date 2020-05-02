package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.ModuleInfoEntity;

public interface ModuleInfoRepo extends JpaRepository<ModuleInfoEntity, Long> {
}
