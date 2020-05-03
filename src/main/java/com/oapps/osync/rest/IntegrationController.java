package com.oapps.osync.rest;

import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oapps.osync.controller.ControllerRepo;
import com.oapps.osync.entity.AccountInfoEntity;
import com.oapps.osync.entity.DefaultFieldEntity;
import com.oapps.osync.entity.DefaultFieldMapEntity;
import com.oapps.osync.entity.FieldMapEntity;
import com.oapps.osync.entity.IntegrationPropsEntity;
import com.oapps.osync.entity.ModuleInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;
import com.oapps.osync.entity.SyncLogEntity;
import com.oapps.osync.fields.Fields;
import com.oapps.osync.repository.AccountInfoRepository;
import com.oapps.osync.repository.DefaultFieldsMappingRepo;
import com.oapps.osync.repository.DefaultFieldsRepo;
import com.oapps.osync.repository.FieldMapRepository;
import com.oapps.osync.repository.IntegrationPropsRepository;
import com.oapps.osync.repository.ModuleInfoRepository;
import com.oapps.osync.repository.ServiceInfoRepository;
import com.oapps.osync.security.CurrentContext;
import com.oapps.osync.service.IntegrationService;
import com.oapps.osync.service.OsyncException;
import com.oapps.osync.util.IntegrationResponse;
import com.oapps.osync.util.OSyncAuthorizerUtil;

import lombok.extern.java.Log;

@RestController
@Log
public class IntegrationController {
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	AccountInfoRepository accountRepo;

	@Autowired
	ServiceInfoRepository serviceRepo; 

	@Autowired
	IntegrationPropsRepository intPropsRepo;

	@Autowired
	FieldMapRepository fieldMapRepo;
	
	@Autowired
	ModuleInfoRepository moduleMapRepo;

	@Autowired
	DefaultFieldsRepo defaultFieldsRepo;

	@Autowired
	DefaultFieldsMappingRepo defaultFieldsMapRepo;

	@Autowired
	IntegrationService intService;

	@GetMapping(path = "/testsync")
	public @ResponseBody SyncLogEntity testSync(@RequestParam("integ_id") Long integId) throws OsyncException {
		log.info("Test sync started...");
		return intService.sync(integId);
	}

	@GetMapping(path = "/api/v1/default-fields")
	public @ResponseBody List<DefaultFieldEntity> getDefaultFields(@RequestParam("service_id") Long serviceId,
			@RequestParam("module_id") Long moduleId) {
		return defaultFieldsRepo.findByServiceIdAndModuleId(serviceId, moduleId);
	}

	@GetMapping(path = "/api/v1/all-fields")
	public @ResponseBody Fields getAllFields(@RequestParam("service_id") Long serviceId,
			@RequestParam("module_id") Long moduleId) {
		ServiceInfoEntity serviceInfo = intService.getServiceInfo(serviceId);
		ModuleInfoEntity moduleInfo = intService.getModuleInfo(moduleId);
		if (serviceInfo == null || moduleInfo == null) {
			return Fields.of();
		}
		return ControllerRepo.getInstance(serviceInfo.getName(), moduleInfo.getName()).getFields();
	}
	
	@GetMapping(path = "/api/v1/all-modules")
	public @ResponseBody List<ModuleInfoEntity> getAllModules(@RequestParam("service_id") Long serviceId) {
		return moduleMapRepo.findAllByServiceId(serviceId);
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
	
	@GetMapping(path = "/api/v1/integration/{integ_id}/modules")
//	@JsonView(OsyncResponseViews.Public.class)
	public @ResponseBody IntegrationResponse getModules(@PathVariable("integ_id") Long integId) {
		IntegrationResponse integResponse = new IntegrationResponse();
		Optional<IntegrationPropsEntity> findById = intPropsRepo.findById(integId);
		if (findById.isPresent()) {
			Long leftServiceId = findById.get().getLeftServiceId();
			Long rightServiceId = findById.get().getRightServiceId();
			
			IntegrationResponse.ServiceDetails leftServiceDetails = integResponse.new ServiceDetails();
			leftServiceDetails.setModules(getAllModules(leftServiceId));
			leftServiceDetails.setServiceId(leftServiceId+"");
			leftServiceDetails.setModuleId(findById.get().getLeftModuleId());
			leftServiceDetails.setServiceName(serviceRepo.findByServiceId(leftServiceId).getName());
			
			IntegrationResponse.ServiceDetails rightServiceDetails = integResponse.new ServiceDetails();
			rightServiceDetails.setModules(getAllModules(rightServiceId));
			rightServiceDetails.setServiceId(rightServiceId+"");
			rightServiceDetails.setModuleId(findById.get().getRightModuleId());
			rightServiceDetails.setServiceName(serviceRepo.findByServiceId(rightServiceId).getName());
			
			integResponse.setLeftDetails(leftServiceDetails);
			integResponse.setRightDetails(rightServiceDetails);
		}
		return integResponse;
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

	@PostMapping(path = "/api/v1/integrate" , consumes = "application/json", produces = "application/json")
	public IntegrationResponse integrate(@RequestBody String payload) {
		
		IntegrationResponse integResponse = new IntegrationResponse();
		
		AccountInfoEntity accInfoObject;
		IntegrationPropsEntity integInfoObj;
		try {
			JSONObject payloadJson = new JSONObject(payload);
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			accInfoObject = mapper.readValue(payload, AccountInfoEntity.class);
			
			AccountInfoEntity findByRemoteIdentifier = accountRepo.findTopByRemoteIdentifierAndEmail(accInfoObject.getRemoteIdentifier(), accInfoObject.getEmail());
			
			if (findByRemoteIdentifier == null) {
				accInfoObject = accountRepo.save(accInfoObject);
			} else {
				accInfoObject = findByRemoteIdentifier;
			}
			
			
			payloadJson.put("osyncId", accInfoObject.getOsyncId());
			payload =payloadJson.toString();
			integInfoObj = mapper.readValue(payload, IntegrationPropsEntity.class);
			
			IntegrationPropsEntity findByServiceIds = intPropsRepo.findTopByOsyncIdAndLeftServiceIdAndRightServiceId(accInfoObject.getOsyncId(), integInfoObj.getLeftServiceId(), integInfoObj.getRightServiceId());
			
			if(findByServiceIds == null) {
				integInfoObj = intPropsRepo.save(integInfoObj);
			} else {
				integInfoObj = findByServiceIds;
			}
			//need to add authorization header
			
			integResponse.setId(integInfoObj.getIntegId()+"");
			
			ServiceInfoEntity leftServiceAuthObj = serviceRepo.findByServiceId(integInfoObj.getLeftServiceId());
			
			if(leftServiceAuthObj.getAuthType().equals("oauth")) {
				String leftAuthUrl = constructOAuthUrl(leftServiceAuthObj,integInfoObj);
				IntegrationResponse.ServiceDetails leftServiceDetails = integResponse.new ServiceDetails();
				
				leftServiceDetails.setServiceId(leftServiceAuthObj.getServiceId()+"");
				leftServiceDetails.setServiceName(leftServiceAuthObj.getName());
				
				IntegrationResponse.AuthDetails leftAuthDetails = integResponse.new AuthDetails();
				
				leftAuthDetails.setType("oauth");
				leftAuthDetails.setUrl(leftAuthUrl);
				
				leftServiceDetails.setAuthDetails(leftAuthDetails);
				
				integResponse.setLeftDetails(leftServiceDetails);
				
			}
			
			ServiceInfoEntity rightServiceAuthObj = serviceRepo.findByServiceId(integInfoObj.getRightServiceId());
			
			if(rightServiceAuthObj.getAuthType().equals("oauth")) {
				String rightAuthUrl = constructOAuthUrl(rightServiceAuthObj,integInfoObj);
				IntegrationResponse.ServiceDetails rightServiceDetails = integResponse.new ServiceDetails();
				
				rightServiceDetails.setServiceId(rightServiceAuthObj.getServiceId()+"");
				rightServiceDetails.setServiceName(rightServiceAuthObj.getName());
				
				
				IntegrationResponse.AuthDetails rightAuthDetails = integResponse.new AuthDetails();
				
				rightAuthDetails.setType("oauth");
				rightAuthDetails.setUrl(rightAuthUrl);
				
				rightServiceDetails.setAuthDetails(rightAuthDetails);
				
				integResponse.setRightDetails(rightServiceDetails);
				
			}

			
			return integResponse;
			
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;

	}
	private String constructOAuthUrl(ServiceInfoEntity serviceAuthObj, IntegrationPropsEntity intInfoObj) {
		String url = "";
		
		String authorizeUrl = serviceAuthObj.getAuthorizeUrl();
		String authScopes = serviceAuthObj.getAuthScopes();
		String clientId = serviceAuthObj.getClientId();
		
		String stateParam = intInfoObj.getOsyncId()+"::"+serviceAuthObj.getServiceId()+"::"+intInfoObj.getIntegId();
		
		url = authorizeUrl +"?response_type=code&client_id="+clientId+"&scope="+authScopes+"&redirect_uri="+OSyncAuthorizerUtil.redirectUrl+"&state="+stateParam;
//		url += "&prompt=consent";
		
		return url;
	}
}
