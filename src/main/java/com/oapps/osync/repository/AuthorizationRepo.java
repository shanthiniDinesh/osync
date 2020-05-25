package com.oapps.osync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.AuthorizationEntity;

@Repository
public interface AuthorizationRepo extends JpaRepository<AuthorizationEntity, Long> {

	AuthorizationEntity findByToken(String token);

}
