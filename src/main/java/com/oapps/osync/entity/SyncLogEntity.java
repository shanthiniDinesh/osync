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

	private Integer totalCountFetched = 0;

	private Integer totalCountUpdated = 0;

	private Integer totalCountNoChangeCount = 0;

	private Integer totalCountCreated = 0;

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

	public Integer getTotalCountFetched() {
		if (this.leftCountFetched != null && this.rightCountFetched != null) {
			return this.totalCountFetched = this.leftCountFetched + this.rightCountFetched;
		}
		if (this.leftCountFetched != null) {
			return this.leftCountFetched;
		} else if (this.rightCountFetched != null) {
			return this.rightCountFetched;
		}
		return null;
	}

	public void setTotalCountFetched(Integer totalCount) {

	}

	public Integer getTotalCountUpdated() {

		if (this.leftCountUpdated != null && this.rightCountUpdated != null) {
			return this.totalCountUpdated = this.leftCountUpdated + this.rightCountUpdated;
		}
		if (this.leftCountUpdated != null) {
			return this.leftCountUpdated;
		} else if (this.rightCountUpdated != null) {
			return this.rightCountUpdated;
		}
		return null;
	}

	public void setTotalCountUpdated(Integer totalCountUpdated) {

	}

	public Integer getTotalCountNoChangeCount() {

		if (this.leftNoChangeCount != null && this.rightNoChangeCount != null) {
			return this.totalCountNoChangeCount = this.leftNoChangeCount + this.rightCountUpdated;
		}
		if (this.leftNoChangeCount != null) {
			return this.leftNoChangeCount;
		} else if (this.rightNoChangeCount != null) {
			return this.rightNoChangeCount;
		}
		return null;
	}

	public void setTotalCountNoChangeCount(Integer totalCountNoChangeCount) {
		this.totalCountNoChangeCount = totalCountNoChangeCount;
	}

	public Integer getTotalCountCreated() {
		if (this.leftCountCreated != null && this.rightCountCreated != null) {
			return this.totalCountCreated = this.leftCountCreated + this.rightCountCreated;
		}
		if (this.leftCountCreated != null) {
			return this.leftCountCreated;
		} else if (this.rightCountCreated != null) {
			return this.rightCountCreated;
		}
		return null;
	}

	public void setTotalCountCreated(Integer totalCountCreated) {
		this.totalCountCreated = totalCountCreated;
	}

}
