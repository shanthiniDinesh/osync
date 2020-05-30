package com.oapps.osync.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.SyncLogEntity;
@Repository
public interface SyncLogEntityRepo extends JpaRepository<SyncLogEntity, Long> {
	
	List<SyncLogEntity> findAllByIntegId(Long integId);
	
}
