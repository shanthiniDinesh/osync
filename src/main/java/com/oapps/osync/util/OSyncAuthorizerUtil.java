package com.oapps.osync.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.oapps.osync.authorizer.AuthorizeParams;
import com.oapps.osync.entity.ServiceAuthInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;
import com.oapps.osync.repository.ServiceAuthInfoRepository;
import com.oapps.osync.repository.ServiceInfoRepository;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class OSyncAuthorizerUtil {
	
	public static final String redirectUrl = "https://0f3c54c1.ngrok.io/api/v1/redirect";
	


	
	public static String sendCodeToAuthorizationService(AuthorizeParams authParams , ServiceInfoRepository serviceInfoRepo) {
		
		Client client = Client.create();
//		String stateParam = intInfoObj.getOsyncId()+"::"+serviceAuthObj.getServiceId()+"::"+intInfoObj.getIntegId();
		
		System.out.println("respJson >>>>>>>>>>>>>"+authParams.getState());
		String state = authParams.getState();
		
		String[] decryptedArray = state.split("::");
		
		String serviceId = decryptedArray[1];
		
		
		ServiceInfoEntity serviceInfo = serviceInfoRepo.findByServiceId(Long.valueOf(serviceId));
		
		String tokenUrl = serviceInfo.getTokenUrl();
		String clientId = serviceInfo.getClientId();
		String clientSecret = serviceInfo.getClientSecret();
		
		WebResource webRes = client.resource(tokenUrl);
		
		webRes = webRes.queryParam("client_id", clientId);
		webRes = webRes.queryParam("client_secret", clientSecret);
		webRes = webRes.queryParam("redirect_uri", redirectUrl);
		
		webRes = webRes.queryParam("code", authParams.getCode());
		webRes = webRes.queryParam("state",  authParams.getState());
		if(authParams.getLocation() != null) {
			webRes = webRes.queryParam("location",  authParams.getLocation());
		}
		if(authParams.getContext() != null) {
			webRes = webRes.queryParam("context",  authParams.getContext());
		}
		if(authParams.getScope() != null) {
			webRes = webRes.queryParam("scope",  authParams.getScope());
		}
		webRes = webRes.queryParam("grant_type",  "authorization_code");
		webRes = webRes.queryParam("accounts-server",  "https://accounts.zoho.com");
		webRes = webRes.queryParam("scope",  serviceInfo.getAuthScopes());
		
		ClientResponse clientResponse = webRes.post(ClientResponse.class);
		String authResponse = clientResponse.getEntity(String.class);
		
		System.out.println("respJson >>>>>>>>>>>>>"+authResponse);
		
		return authResponse;
		
	}
	

}
