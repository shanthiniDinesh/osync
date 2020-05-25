package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.oapps.osync.entity.AccountUserInfoEntity;

@Repository
public interface AccountUserInfoRepository extends CrudRepository<AccountUserInfoEntity, Integer> {

	AccountUserInfoEntity findByOsyncId(String osyncId);

}