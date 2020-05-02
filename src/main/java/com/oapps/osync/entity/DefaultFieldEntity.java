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
@Table(name = "DefaultField")
public class DefaultFieldEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long defaultFieldId;

	@Getter
	@Setter
	private Long serviceId;

	@Getter
	@Setter
	private Long moduleId;

	@Getter
	@Setter
	private String columnName;

	@Getter
	@Setter
	private String columnType;

}
