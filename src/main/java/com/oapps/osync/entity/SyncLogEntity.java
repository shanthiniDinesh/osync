package com.oapps.osync.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "SyncLog")
public class SyncLogEntity {
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
	private Long integId;

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
	private Integer timeTaken = 0;

	@Getter
	@Setter
	@Column()
	private Integer leftCountFetched = 0;

	@Getter
	@Setter
	@Column()
	private Integer updatedLeftCount = 0;

	@Getter
	@Setter
	@Column()
	private Integer rightCountFetched = 0;

	@Getter
	@Setter
	@Column()
	private Integer matchedOnUniqueColumn = 0;

	@Getter
	@Setter
	@Column()
	private Integer leftSkippedForEmailColumn = 0;

	@Getter
	@Setter
	@Column()
	private Integer rightSkippedForEmailColumn = 0;

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
