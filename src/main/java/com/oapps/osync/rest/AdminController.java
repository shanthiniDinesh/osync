package com.oapps.osync.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oapps.osync.entity.ModuleInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;
import com.oapps.osync.repository.ModuleInfoRepository;
import com.oapps.osync.repository.ServiceInfoRepository;

import lombok.extern.java.Log;

@RestController
@Log
public class AdminController {
	@Autowired
	ServiceInfoRepository serviceRepo;

	@Autowired
	ModuleInfoRepository moduleRepo;
	

	@PostMapping(path = "/api/v1/service" , consumes = "application/json", produces = "application/json")
	public ServiceInfoEntity addService(@RequestBody ServiceInfoEntity serviceInfoObj) {
		return serviceRepo.save(serviceInfoObj);

	}
	
	@GetMapping(path = "/api/v1/service" , produces = "application/json")
	public ServiceInfoEntity getService(@RequestParam String serviceId) {
		return serviceRepo.findByServiceId(Long.valueOf(serviceId));
	}
	

	@PostMapping(path = "/api/v1/module" , consumes = "application/json", produces = "application/json")
	public ModuleInfoEntity addModule(@RequestBody ModuleInfoEntity moduleObj) {
		return moduleRepo.save(moduleObj);
	}
	
	@GetMapping(path = "/api/v1/module" , produces = "application/json")
	public ModuleInfoEntity getModule(@RequestParam String moduleId) {
		return moduleRepo.findByModuleId(Long.valueOf(moduleId));
	}
}
