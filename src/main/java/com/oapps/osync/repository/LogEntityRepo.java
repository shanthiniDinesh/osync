package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.LogEntity;

public interface LogEntityRepo extends JpaRepository<LogEntity, Long> {
}
