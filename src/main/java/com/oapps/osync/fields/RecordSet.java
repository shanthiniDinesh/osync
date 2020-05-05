package com.oapps.osync.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.oapps.osync.entity.UniqueValuesMapEntity;
import com.oapps.osync.repository.UniqueValuesMapRepo;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(staticName = "init")
public class RecordSet implements Iterable<Record> {

	@Getter
	@Setter
	@NonNull
	String controllerName;

	@Getter
	@Setter
	@NonNull
	String uniqueId;

	@Getter
	@Setter
	String uniqueColumnName;

	HashMap<String, Record> recordSets = new HashMap<String, Record>();

	public Record add(String uniqueValue) {
		Record record = new Record(uniqueValue);
		recordSets.put(uniqueValue, record);
		return record;
	}

	public void remove(String uniqueValue) {
		recordSets.remove(uniqueValue);
	}

	public Record createEmptyObject() {
		String uniqueValue = "generated-" + (this.count() + 1) + "-" + System.currentTimeMillis();
		Record record = new Record(uniqueValue);
		record.setNewRecord(true);
		recordSets.put(uniqueValue, record);
		return record;
	}

	public Record find(String uniqueValue) {
		return recordSets.get(uniqueValue);
	}

	public Record findByUniqueColumn(String uniqueValue) {
		// TODO : Optimize
		for (Record record : recordSets.values()) {
			Object valueObj = record.getValue(uniqueColumnName);
			if (valueObj != null) {
				if (valueObj.toString().equalsIgnoreCase(uniqueValue)) {
					return record;
				}
			}
		}
		return null;
	}

	public int count() {
		return recordSets.size();
	}

	@Override
	public Iterator<Record> iterator() {
		return getAllRecords().iterator();
	}

	public Collection<Record> getAllRecords() {
		return recordSets.values();
	}

	public void fillRightUniqueValueMap(UniqueValuesMapRepo uvMapRepo, Long integId) {
		List<String> uniqueIns = new ArrayList<String>();
		for (Record record : getAllRecords()) {
			uniqueIns.add(record.getUniqueValue());
		}
		HashMap<String, String> uniqueValues = new HashMap<String, String>();
		List<UniqueValuesMapEntity> uniqueValuesMap = uvMapRepo.findByIntegIdAndRightUniqueValueIn(integId, uniqueIns);
		for (UniqueValuesMapEntity uniqueValuesMap2 : uniqueValuesMap) {
			uniqueValues.put(uniqueValuesMap2.getRightUniqueValue(), uniqueValuesMap2.getLeftUniqueValue());
		}
		for (Record record : getAllRecords()) {
			record.setMappedRecordUniqueValue(uniqueValues.get(record.getUniqueValue()));
		}
	}

	public void fillLeftUniqueValueMap(UniqueValuesMapRepo uvMapRepo, Long integId) {
		List<String> uniqueIns = new ArrayList<String>();
		for (Record record : getAllRecords()) {
			uniqueIns.add(record.getUniqueValue());
		}
		HashMap<String, String> uniqueValues = new HashMap<String, String>();
		List<UniqueValuesMapEntity> uniqueValuesMap = uvMapRepo.findByIntegIdAndLeftUniqueValueIn(integId, uniqueIns);
		for (UniqueValuesMapEntity uniqueValuesMap2 : uniqueValuesMap) {
			uniqueValues.put(uniqueValuesMap2.getLeftUniqueValue(), uniqueValuesMap2.getRightUniqueValue());
		}
		for (Record record : getAllRecords()) {
			record.setMappedRecordUniqueValue(uniqueValues.get(record.getUniqueValue()));
		}
	}

	public void fillUniqueValueMap(UniqueValuesMapRepo uvMapRepo, Long integId, boolean isLeft) {
		if (isLeft) {
			fillLeftUniqueValueMap(uvMapRepo, integId);
		} else {
			fillRightUniqueValueMap(uvMapRepo, integId);
		}
	}
}
