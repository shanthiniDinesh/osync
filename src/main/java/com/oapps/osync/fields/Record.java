package com.oapps.osync.fields;

import java.util.HashMap;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Record {

	@Getter
	@Setter
	@NonNull
	String uniqueValue;

	@Getter
	@Setter
	String mappedRecordUniqueValue;

	@Getter
	@Setter
	boolean isNewRecord;

	@Getter
	@Setter
	HashMap<String, Object> columnValues = new HashMap<String, Object>();

	public Record addNewValue(String key, Object value) {
		columnValues.put(key, value);
		return this;
	}

	public Object getValue(String key) {
		return columnValues.get(key);
	}

	public JSONObject columnValuesAsJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return new JSONObject(mapper.writeValueAsString(columnValues));
	}
	
	public JSONObject toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return new JSONObject(mapper.writeValueAsString(this));
	}
}
