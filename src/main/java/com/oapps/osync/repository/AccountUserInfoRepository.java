package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.AccountUserInfoEntity;

public interface AccountUserInfoRepository extends CrudRepository<AccountUserInfoEntity, Integer> {

	AccountUserInfoEntity findByOsyncId(String osyncId);

}