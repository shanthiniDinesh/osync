package com.oapps.osync.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oapps.osync.entity.ModuleInfoEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationResponse {
	
	@JsonProperty("integId")
	private String id;
	private String osyncId;
	private String hash;
	
	@JsonProperty("data")
	private IntegrationResponse.Entity entity;
	
	@JsonProperty("left")
	private IntegrationResponse.ServiceDetails leftDetails;
	
	@JsonProperty("right")
	private IntegrationResponse.ServiceDetails rightDetails;
	
	
	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class ServiceDetails {
		private String serviceId;
		private String serviceName;
		private Long moduleId;
		
		@JsonProperty("auth")
		private IntegrationResponse.AuthDetails authDetails;
		
		private List<ModuleInfoEntity> modules;
		
	}
	
	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class AuthDetails {
		private String type;
		private String url;
		private boolean isAuthorized;
		
	}
	
	@Getter
	@Setter
	public class Entity{
		
		@JsonProperty("left_module_id")
		private String leftId;
		
		@JsonProperty("right_module_id")
		private String rightId;
		
		private String direction;
	}

}
