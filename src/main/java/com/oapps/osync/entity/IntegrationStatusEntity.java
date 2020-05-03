package com.oapps.osync.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.oapps.osync.service.OsyncEnums.IntegrationStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Table(name = "IntegrationStatus")
public class IntegrationStatusEntity {
	@Id
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date prevStartTime;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date prevEndTime;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private IntegrationStatus status;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date createdTime;
}
