package com.oapps.osync.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.oapps.osync.entity.SyncLogEntity;
import com.oapps.osync.repository.SyncLogEntityRepo;
import com.oapps.osync.service.OsyncException;

import lombok.extern.java.Log;

@RestController
@Log


public class SyncUtil {
	
	
	@Autowired
	SyncLogEntityRepo  synlog;
	
	@GetMapping(path = "/api/v1/synclog")
	public @ResponseBody List<SyncLogEntity> getSyncLog(@RequestParam("integId") Long integId) throws OsyncException {
		log.info("Fetching Sync Log");
		return synlog.findAllByIntegId(integId);
	}

}
