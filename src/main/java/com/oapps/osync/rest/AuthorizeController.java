package com.oapps.osync.rest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oapps.osync.entity.ModuleInfoEntity;
import com.oapps.osync.entity.ServiceAuthInfoEntity;
import com.oapps.osync.invoker.Invoker;
import com.oapps.osync.repository.ServiceAuthInfoRepository;
import com.oapps.osync.repository.ServiceInfoRepository;
import com.oapps.osync.util.AuthorizeParams;
import com.oapps.osync.util.AuthorizerUtil;

import lombok.extern.java.Log;

@RestController
@Log
public class AuthorizeController {
	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	ServiceInfoRepository serviceRepo;


	@Autowired
	ServiceAuthInfoRepository serviceAuthRepo;

	//	
	@GetMapping(path = "/api/v1/redirect",produces = "text/html; charset=utf-8")
	public String redirectHandler(AuthorizeParams authParams) {
		try {
			System.out.println(" authParams >>>>>>"+authParams.toString());

			String sendCodeToAuthorizationService = AuthorizerUtil.getAccessToken(authParams ,serviceRepo);

			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			AuthorizeParams newAuthParamObj = mapper.readValue(sendCodeToAuthorizationService, AuthorizeParams.class);
			
			saveToken(newAuthParamObj, authParams.getState());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Success";
	}

	public void saveToken(AuthorizeParams authParams, String state) {
		String[] decryptedArray = state.split("::");

		String osyncId = decryptedArray[0];
		String serviceId = decryptedArray[1];
		String integId = decryptedArray[2];
		ServiceAuthInfoEntity entityObj = new ServiceAuthInfoEntity();
		
		ServiceAuthInfoEntity findTopByOsyncIdAndServiceId = serviceAuthRepo.findTopByOsyncIdAndServiceId(Long.valueOf(osyncId), Long.valueOf(serviceId));
		if(findTopByOsyncIdAndServiceId != null) {
			entityObj.setAuthId(findTopByOsyncIdAndServiceId.getAuthId());
		}
		entityObj.setAccessToken(authParams.getAccess_token());
		entityObj.setOsyncId(Long.valueOf(osyncId));
		entityObj.setIntegId(Long.valueOf(integId));
		entityObj.setRefreshToken(authParams.getRefresh_token());
		entityObj.setTokenType("org");
		entityObj.setServiceId(Long.valueOf(serviceId));
		serviceAuthRepo.save(entityObj);

		
	}
	@DeleteMapping(path = "/api/v1/revoke")
	public void revoke(@RequestParam String serviceId,@RequestParam String osyncId, @RequestParam String integId) {
		if(serviceId != null && osyncId != null && integId != null ) {
			serviceAuthRepo.delete(serviceAuthRepo.findByOsyncIdAndServiceIdAndIntegId(Long.valueOf(osyncId),Long.valueOf(serviceId),Long.valueOf(integId) ));
		}
	}
	
	
}
