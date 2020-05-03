package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.SyncLogEntity;

public interface SyncLogEntityRepo extends JpaRepository<SyncLogEntity, Long> {
}
