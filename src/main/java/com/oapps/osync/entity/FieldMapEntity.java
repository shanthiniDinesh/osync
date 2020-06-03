package com.oapps.osync.entity;

import java.util.Date;

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
@Table(name = "FieldMap")
public class FieldMapEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long fieldmapId;

	@Getter
	@Setter
	private Long osyncId;

	@Getter
	@Setter
	private Long integId;

	@Getter
	@Setter
	private String leftColumnName;

	@Getter
	@Setter
	private String rightColumnName;

	@Getter
	@Setter
	private String leftColumnType;

	@Getter
	@Setter
	private String rightColumnType;

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
	
	@Getter
	@Setter
	private int  isEnabled;
}
