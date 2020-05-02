package com.oapps.osync.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "OsyncAccountUser")
public class AccountUserInfoEntity {
	
	@Getter
	@Setter
	@Column(nullable = false)
	private Long osyncId;
	
	@Getter
	@Setter
	private Long osyncUserId;
	
	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private String email;
	
	
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
