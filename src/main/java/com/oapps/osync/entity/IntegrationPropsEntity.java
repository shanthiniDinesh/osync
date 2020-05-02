package com.oapps.osync.entity;

import java.sql.Date;

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
@Table(name = "IntegrationProps")
public class IntegrationPropsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	@Column(nullable = false, unique = true, updatable = false)
	private Long integId;

	@Getter
	@Setter
	@Column(nullable = false)
	private Long osyncId;

	@Getter
	@Setter
	private Long leftServiceId;

	@Getter
	@Setter
	private Long rightServiceId;

	@Getter
	@Setter
	private Long leftModuleId;

	@Getter
	@Setter
	private Long rightModuleId;

	@Getter
	@Setter
	private boolean syncRecordsWithEmail;

	@Getter
	@Setter
	private Long masterService;

	@Getter
	@Setter
	private boolean lookupUniqueColumn;

	@Getter
	@Setter
	private int status;

	@Getter
	@Setter
	private int direction;

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
