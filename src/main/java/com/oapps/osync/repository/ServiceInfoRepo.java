package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.ServiceInfoEntity;

public interface ServiceInfoRepo extends JpaRepository<ServiceInfoEntity, Long> {
}
