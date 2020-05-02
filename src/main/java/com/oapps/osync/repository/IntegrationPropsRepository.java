package com.oapps.osync.repository;

import org.springframework.data.repository.CrudRepository;

import com.oapps.osync.entity.IntegrationPropsEntity;

public interface IntegrationPropsRepository extends CrudRepository<IntegrationPropsEntity, Integer> {

	IntegrationPropsEntity findByAccountId(String accountId);

}
