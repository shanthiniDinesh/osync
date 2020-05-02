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
@Table(name = "OsyncAccountUser")
public class AccountUserInfoEntity {
	
	@Getter
	@Setter
	@Column(nullable = false)
	private Long osyncId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
