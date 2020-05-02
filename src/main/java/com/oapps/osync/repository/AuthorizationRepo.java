package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oapps.osync.entity.AuthorizationEntity;

public interface AuthorizationRepo extends JpaRepository<AuthorizationEntity, Long> {

	AuthorizationEntity findByToken(String token);

}
