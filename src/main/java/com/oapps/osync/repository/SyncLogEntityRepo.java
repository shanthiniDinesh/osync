package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.SyncLogEntity;
@Repository
public interface SyncLogEntityRepo extends JpaRepository<SyncLogEntity, Long> {
}
