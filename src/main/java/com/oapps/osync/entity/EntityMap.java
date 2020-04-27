package com.oapps.osync.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
public class EntityMap {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String accountId;

	@Getter
	@Setter
	private String leftControllerName;

	@Getter
	@Setter
	private String rightControllerName;

	@Getter
	@Setter
	private String leftUniqueColumn;

	@Getter
	@Setter
	private String rightUniqueColumn;
}
