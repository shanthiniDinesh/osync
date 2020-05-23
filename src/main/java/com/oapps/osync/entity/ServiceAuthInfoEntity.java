package com.oapps.osync.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.oapps.osync.invoker.Invoker;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Table(name = "ServiceAuthInfo")
@EntityListeners(Invoker.class)
public class ServiceAuthInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	@Column(nullable = false, unique = true, updatable = false)
	private Long authId;
	
	@Getter
	@Setter
	@Column(nullable = false)
	private Long osyncId;
	
	@Getter
	@Setter
	@Column(nullable = false)
	private Long serviceId;
	
	@Getter
	@Setter
	private Long osyncUserId;
	
	@Getter
	@Setter
	@Column(nullable = false)
	private Long integId;
	
	@Getter
	@Setter
	private String tokenType;
	
	@Getter
	@Setter
	private String accessToken;
	
	@Getter
	@Setter
	private String refreshToken;
	
	
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
