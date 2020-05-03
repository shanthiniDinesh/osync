package com.oapps.osync.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.AccountInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;


public interface AccountInfoRepository extends CrudRepository<AccountInfoEntity, Integer> {

	List<AccountInfoEntity> findByRemoteIdentifier(String remoteIdentifier);
	
	AccountInfoEntity findByServiceId(Long serviceId);

	AccountInfoEntity findByOsyncId(Long osyncId);
	
	AccountInfoEntity findTopByRemoteIdentifierAndEmail(String remoteIdentifier, String email);
	
	AccountInfoEntity save(	AccountInfoEntity accInfoObj);

}