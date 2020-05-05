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
	private String leftService;

	@Getter
	@Setter
	private String rightService;

	@Getter
	@Setter
	private String leftModule;

	@Getter
	@Setter
	private String rightModule;

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
	private Integer leftCountUpdated = 0;
	
	@Getter
	@Setter
	@Column()
	private Integer leftNoChangeCount = 0;
	
	@Getter
	@Setter
	@Column()
	private Integer rightNoChangeCount = 0;

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
	private Integer rightCountUpdated = 0;

	@Getter
	@Setter
	private Integer leftCountCreated = 0;

	@Getter
	@Setter
	private Integer rightCountCreated = 0;

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

	public void addFetchCount(int count, boolean isLeft) {
		if (isLeft) {
			setLeftCountFetched(getLeftCountFetched() + count);
		} else {
			setRightCountFetched(getRightCountFetched() + count);
		}

	}

	public void incrementSkippedCount(boolean isLeft) {
		if (isLeft) {
			setLeftSkippedForEmailColumn(getLeftSkippedForEmailColumn() + 1);
		} else {
			setRightSkippedForEmailColumn(getRightSkippedForEmailColumn() + 1);
		}
	}

	public void incrementUniqueColumnMatch() {
		setMatchedOnUniqueColumn(getMatchedOnUniqueColumn() + 1);
	}

	public void addCreatedCount(int count, boolean isLeft) {
		if (isLeft) {
			setLeftCountCreated(getLeftCountCreated() + count);
		} else {
			setRightCountCreated(getRightCountCreated() + count);
		}
	}

	public void addUpdatedCount(int count, boolean isLeft) {
		if (isLeft) {
			setLeftCountUpdated(getLeftCountUpdated() + count);
		} else {
			setRightCountUpdated(getRightCountUpdated() + count);
		}
	}
	
	public void incrNoChangeCount(boolean isLeft) {
		if (isLeft) {
			setLeftNoChangeCount(getLeftNoChangeCount() + 1);
		} else {
			setRightNoChangeCount(getRightNoChangeCount() + 1);
		}
	}
}
