package com.oapps.osync.invoker;

import java.util.Iterator;

import javax.persistence.EntityManager;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import com.oapps.osync.bean.BeanUtil;
import com.oapps.osync.entity.ServiceAuthInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;
import com.oapps.osync.repository.ServiceAuthInfoRepository;
import com.oapps.osync.repository.ServiceInfoRepository;
import com.oapps.osync.util.AuthorizerUtil;
import com.oapps.osync.util.JPAUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import lombok.extern.java.Log;

@Log
public class Invoker {


	private Client client;
	private Long authId;

	public Invoker(Long authId) {
		super();
		this.client = Client.create();
		this.authId = authId;
	}
	
	@Bean
	public void query() {
		log.info("query result :: " + getServiceInfo().findAll());
		log.info("output...." + getServiceInfo().count());
	}

	private ServiceAuthInfoRepository getServiceAuthInfo() {
		return BeanUtil.getBean(ServiceAuthInfoRepository.class);
	}
	
	private ServiceInfoRepository getServiceInfo() {
		return BeanUtil.getBean(ServiceInfoRepository.class);
	}

	public String get(String targetUrl ,JSONObject headerJson,JSONObject queryParams){
		log.info("targetUrl >>>>>>>: " + targetUrl +":::: queryParams >>>"+queryParams);
		String response = "";
		WebResource webRes = this.client.resource(targetUrl);
		ServiceAuthInfoEntity serviceAuth = null;

		if(queryParams != null && queryParams.length() > 0) {
			Iterator<String> keys = queryParams.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				String value = queryParams.getString(key);
				webRes = webRes.queryParam(key, value);
			}
		}

		Builder requestBuilder = webRes.getRequestBuilder();
		if(headerJson != null && headerJson.length() > 0) {
			Iterator<String> keys = headerJson.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				String value = headerJson.getString(key);
				requestBuilder.header(key, value);
			}
		}
//		public List<Map<String, Object>> listUsers() {
//		    return jdbcTemplate.queryForList("SELECT * FROM user;");
//		}
//		entityManager = BeanUtil.getBean(EntityManager.class);
		if(this.authId != null) {
			serviceAuth = getServiceAuthInfo().findById(this.authId).get();
			if(serviceAuth != null && serviceAuth.getAccessToken() != null && !serviceAuth.getAccessToken().isEmpty()) {
				requestBuilder.header("Authorization", "Bearer "+serviceAuth.getAccessToken());
			}
		}

		ClientResponse clientResponse = requestBuilder.get(ClientResponse.class);
		if(clientResponse.getStatus() == 401 && serviceAuth != null) {
			ServiceInfoEntity serviceInfo = getServiceInfo().findByServiceId(serviceAuth.getServiceId());
			String refreshTokenResp = AuthorizerUtil.getAccessTokenUsingRefreshToken(serviceInfo, serviceAuth, this.authId);
			JSONObject refreshTokenObj = new JSONObject(refreshTokenResp);


			saveAccessTokenUsingRefreshToken(serviceAuth,refreshTokenObj.getString("access_token"));
			//			serviceAuthRepo.save(serviceAuth);

			response = get(targetUrl, headerJson, queryParams);

		} else {
			response = clientResponse.getEntity(String.class);
		}
		log.info("response >>>>>>"+response);
		log.info("clientResponse.getStatus(); >>>>>>"+clientResponse.getStatus());
		return response;
	}

	public String post(String targetUrl ,JSONObject headerJson,JSONObject queryParams,JSONObject payloadJson){
		log.info("targetUrl >>>>>>>: " + targetUrl +":::: queryParams >>>"+queryParams);
		WebResource webRes = this.client.resource(targetUrl);
		ServiceAuthInfoEntity serviceAuth = null;

		if(queryParams != null && queryParams.length() > 0) {
			Iterator<String> keys = queryParams.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				String value = queryParams.getString(key);
				webRes = webRes.queryParam(key, value);
			}
		}

		Builder requestBuilder = webRes.getRequestBuilder();
		if(headerJson != null && headerJson.length() > 0) {
			Iterator<String> keys = headerJson.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				String value = headerJson.getString(key);
				requestBuilder.header(key, value);
			}
		}
		EntityManager entityManager = null; //BeanUtil.getBean(EntityManager.class);
		if(this.authId != null) {
			serviceAuth = entityManager.find(ServiceAuthInfoEntity.class, this.authId);
			if(serviceAuth != null && serviceAuth.getAccessToken() != null && !serviceAuth.getAccessToken().isEmpty()) {
				requestBuilder.header("Authorization", "Bearer "+serviceAuth.getAccessToken());
			}
		}

		ClientResponse clientResponse = requestBuilder.post(ClientResponse.class);
		if(clientResponse.getStatus() == 401 && serviceAuth != null) {
			ServiceInfoEntity serviceInfo = entityManager.find(ServiceInfoEntity.class, serviceAuth.getServiceId());
		}
		String response = clientResponse.getEntity(String.class);
		log.info("response >>>>>>"+response);
		log.info("clientResponse.getStatus(); >>>>>>"+clientResponse.getStatus());
		return response;
	}

	public ServiceAuthInfoEntity getServiceAuthInfo( Long osyncId ,  Long serviceId ,  Long integId) {
		log.info(" chekcing the osyncId >>>>>"+osyncId+":::::: serviceId>>>>>"+serviceId+":::::: integId >>>>>>"+integId);
		return getServiceAuthInfo().findByOsyncIdAndServiceIdAndIntegId(osyncId,serviceId,integId );
	}

	public static void main(String[] args) {
		String authId = "8";

		Invoker osync = new Invoker(Long.valueOf(authId));
		String targetUrl = "https://www.zohoapis.com/crm/v2/settings/fields?module=Contacts";

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type","application/json");

		JSONObject queryParams = new JSONObject();
		osync.get(targetUrl,headerJson,queryParams);
	}

	@Transactional
	public void saveAccessTokenUsingRefreshToken(ServiceAuthInfoEntity serviceAuth,String accessToken) {

		EntityManager entityManager = null;
		try {
			entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
			entityManager.getTransaction().begin();

			serviceAuth.setAccessToken(accessToken);
			entityManager.persist(serviceAuth);

			entityManager.getTransaction().commit();
			System.out.println("Person saved successfully");
		} catch (Exception e) {
			e.printStackTrace();
			if (entityManager != null) {
				System.out.println("Transaction is being rolled back.");
				entityManager.getTransaction().rollback();
			}
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
		JPAUtil.shutdown();
	}
}
