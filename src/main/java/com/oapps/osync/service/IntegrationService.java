package com.oapps.osync.service;

import com.oapps.osync.entity.SyncLogEntity;
import com.oapps.osync.entity.ModuleInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;

public interface IntegrationService {
	
	public SyncLogEntity sync2(Long osyncId, Long integId) throws OsyncException;
	
	public ModuleInfoEntity getModuleInfo(Long leftModuleId);

	public ServiceInfoEntity getServiceInfo(Long leftServiceId);
}
