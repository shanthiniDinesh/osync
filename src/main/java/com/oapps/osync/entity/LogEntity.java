package com.oapps.osync.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

public class LogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long logId;

	@Getter
	@Setter
	private Long osyncId;

	@Getter
	@Setter
	private Date syncTime;

	@Getter
	@Setter
	private Integer timeTaken = 0;

	@Getter
	@Setter
	@Column()
	private Integer updatedLeftCount = 0;

	@Getter
	@Setter
	private Integer updatedRightCount = 0;

	@Getter
	@Setter
	private Integer createdLeftCount = 0;

	@Getter
	@Setter
	private Integer createdRightCount = 0;

	@Getter
	@Setter
	private Integer errorsCount = 0;

	@Getter
	@Setter
	private Integer duplicatesCount = 0;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date createdTime;
}
