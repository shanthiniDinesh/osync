package com.oapps.osync.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.oapps.osync.ControllerRepo;
import com.oapps.osync.CurrentContext;
import com.oapps.osync.entity.DefaultFieldEntity;
import com.oapps.osync.entity.DefaultFieldMapEntity;
import com.oapps.osync.entity.FieldMapEntity;
import com.oapps.osync.entity.IntegrationPropsEntity;
import com.oapps.osync.fields.Fields;
import com.oapps.osync.repository.DefaultFieldsMappingRepo;
import com.oapps.osync.repository.DefaultFieldsRepo;
import com.oapps.osync.repository.FieldMapRepository;
import com.oapps.osync.repository.IntegrationPropsRepository;

import lombok.extern.java.Log;

@RestController
@Log
public class IntegrationController {
	@Autowired
	IntegrationPropsRepository intPropsRepo;

	@Autowired
	FieldMapRepository fieldMapRepo;

	@Autowired
	DefaultFieldsRepo defaultFieldsRepo;

	@Autowired
	DefaultFieldsMappingRepo defaultFieldsMapRepo;

	@GetMapping(path = "/api/v1/default-fields")
	public @ResponseBody List<DefaultFieldEntity> getDefaultFields(@RequestParam("service_id") Long serviceId,
			@RequestParam("module_id") Long moduleId) {
		return defaultFieldsRepo.findByServiceIdAndModuleId(serviceId, moduleId);
	}

	@GetMapping(path = "/api/v1/all-fields")
	public @ResponseBody Fields getAllFields(@RequestParam("service_id") Long serviceId,
			@RequestParam("module_id") Long moduleId) {
		if (serviceId % 2 == 1) {
			return ControllerRepo.getInstance("zohocrm-contacts").getFields("12");
		} else {
			return ControllerRepo.getInstance("zohocrm-contacts").getFields("12");
		}
	}

	@GetMapping(path = "/api/v1/default-fields-map")
	public @ResponseBody List<DefaultFieldMapEntity> getDefaultFieldsMap(
			@RequestParam("left_service_id") Long leftServiceId, @RequestParam("left_module_id") Long leftModuleId,
			@RequestParam("right_service_id") Long rightServiceId,
			@RequestParam("right_module_id") Long rightModuleId) {
		return defaultFieldsMapRepo.findAllDefaultMappings(leftServiceId, leftModuleId, rightServiceId, rightModuleId);
	}

	@GetMapping(path = "/api/v1/integration/{integ_id}/fields")
	public @ResponseBody List<FieldMapEntity> getAllFields(@PathVariable("integ_id") Long integId) {
		return fieldMapRepo.findAllByIntegId(integId);
	}

	@GetMapping(path = "/api/v1/integration/{integ_id}")
	public @ResponseBody Optional<IntegrationPropsEntity> getIntegration(@PathVariable("integ_id") Long integId) {
		Optional<IntegrationPropsEntity> findById = intPropsRepo.findById(integId);
		if (findById.isPresent()) {
			findById.get().setFields(getAllFields(integId));
		}
		return findById;
	}

	@PostMapping(path = "/api/v1/integration")
	public @ResponseBody IntegrationPropsEntity getIntegration(@RequestBody IntegrationPropsEntity entity) {
		entity.setOsyncId(CurrentContext.getCurrentOsyncId());
		return intPropsRepo.save(entity);
	}

	@PostMapping(path = "/api/v1/integration/{integ_id}/fields")
	public @ResponseBody List<FieldMapEntity> createFieldsMapping(@PathVariable("integ_id") Long integId,
			@RequestBody List<FieldMapEntity> fieldMapList) {
		List<FieldMapEntity> fieldMaps = fieldMapRepo.findAll();
		for (FieldMapEntity fieldMapEntity : fieldMaps) {
			fieldMapRepo.delete(fieldMapEntity);
		}
		for (FieldMapEntity fieldMapEntity : fieldMapList) {
			fieldMapEntity.setIntegId(integId);
			fieldMapEntity.setOsyncId(CurrentContext.getCurrentOsyncId());
			fieldMapRepo.save(fieldMapEntity);
		}
		return getAllFields(integId);
	}
}
