package com.oapps.osync.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IntegrationResponse {
	
	private String id;
	
	private String hash;
	
	@JsonProperty("left")
	private IntegrationResponse.ServiceDetails leftDetails;
	
	@JsonProperty("right")
	private IntegrationResponse.ServiceDetails rightDetails;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public IntegrationResponse.ServiceDetails getLeftDetails() {
		return leftDetails;
	}

	public void setLeftDetails(IntegrationResponse.ServiceDetails leftDetails) {
		this.leftDetails = leftDetails;
	}

	public IntegrationResponse.ServiceDetails getRightDetails() {
		return rightDetails;
	}

	public void setRightDetails(IntegrationResponse.ServiceDetails rightDetails) {
		this.rightDetails = rightDetails;
	}

	public class ServiceDetails {
		private String serviceId;
		private String serviceName;
		private String serviceLogo;
		
		@JsonProperty("auth")
		private IntegrationResponse.AuthDetails authDetails;
		
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		public String getServiceLogo() {
			return serviceLogo;
		}
		public void setServiceLogo(String serviceLogo) {
			this.serviceLogo = serviceLogo;
		}
		public IntegrationResponse.AuthDetails getAuthDetails() {
			return authDetails;
		}
		public void setAuthDetails(IntegrationResponse.AuthDetails authDetails) {
			this.authDetails = authDetails;
		}
		
		
	}
	
	public class AuthDetails {
		private String type;
		private String url;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		
	}

}
