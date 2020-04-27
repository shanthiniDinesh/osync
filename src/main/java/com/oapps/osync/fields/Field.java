package com.oapps.osync.fields;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Field {
	@Getter
	@Setter
	String dataType;
	@Getter
	@Setter
	String id;
	@Getter
	@Setter
	String displayName;
}
