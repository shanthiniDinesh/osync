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
@Table(name = "OsyncAuthorization")
public class AuthorizationEntity {

	@Getter
	@Setter
	@Column(nullable = false)
	private Long osyncId;
	
	@Getter
	@Setter
	private Long osyncUserId;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long token_id;
	
	@Getter
	@Setter
	private String token;
	
	@Getter
	@Setter
	private boolean admin = false;

	
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
