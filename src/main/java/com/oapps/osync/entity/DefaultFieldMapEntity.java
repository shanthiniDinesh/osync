package com.oapps.osync.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Table(name = "DefaultFieldMap")
public class DefaultFieldMapEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long defaultFieldmapId;

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

}
