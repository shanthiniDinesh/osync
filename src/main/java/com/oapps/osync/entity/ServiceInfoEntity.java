package com.oapps.osync.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Table(name = "ServiceInfo")
public class ServiceInfoEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	@Column(nullable = false, unique = true, updatable = false)
	private Long serviceId;
	
	
	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private String authType;
	
	@Getter
	@Setter
	private String authScopes;
	
	@Getter
	@Setter
	private String clientId;
	
	
	@Getter
	@Setter
	private String clientSecret;
	
	
	@Getter
	@Setter
	private String authorizeUrl;
	
	
	@Getter
	@Setter
	private String tokenUrl;
	
	
	@Getter
	@Setter
	private String refreshTokenUrl;
	
	
	@Getter
	@Setter
	private String revokeTokenUrl;
	
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date createdTime;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modifiedTime;
}
