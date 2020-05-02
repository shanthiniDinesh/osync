package com.oapps.osync.fields;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.oapps.osync.repository.UniqueValuesMapRepo;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(staticName = "of")
public class RecordSet implements Iterable<Record> {

	@Getter
	@Setter
	@NonNull
	String controllerName;

	@Getter
	@Setter
	@NonNull
	String uniqueColumnName;

	HashMap<String, Record> recordSets = new HashMap<String, Record>();

	public Record add(String uniqueValue) {
		Record record = new Record(uniqueValue);
		recordSets.put(uniqueValue, record);
		return record;
	}

	public Record createRecordObject() {
		String uniqueValue = "generated-" + (this.length() + 1) + "-" + System.currentTimeMillis();
		Record record = new Record(uniqueValue);
		record.setNewRecord(true);
		recordSets.put(uniqueValue, record);
		return record;
	}

	public Record find(String uniqueValue) {
		return recordSets.get(uniqueValue);
	}

	public int length() {
		return recordSets.size();
	}

	@Override
	public Iterator<Record> iterator() {
		return getAllRecords().iterator();
	}

	public Collection<Record> getAllRecords() {
		return recordSets.values();
	}

	public void fillRightUniqueValueMap(UniqueValuesMapRepo uvMapRepo, String accountId) {
//		List<String> uniqueIns = new ArrayList<String>();
//		for (Record record : getAllRecords()) {
//			uniqueIns.add(record.getUniqueValue());
//		}
//		HashMap<String, String> uniqueValues = new HashMap<String, String>();
//		List<UniqueValuesMap> uniqueValuesMap = uvMapRepo.findByAccountIdAndRightUniqueValueIn(accountId, uniqueIns);
//		for (UniqueValuesMap uniqueValuesMap2 : uniqueValuesMap) {
//			uniqueValues.put(uniqueValuesMap2.getRightUniqueValue(), uniqueValuesMap2.getLeftUniqueValue());
//		}
//		for (Record record : getAllRecords()) {
//			record.setMappedRecordUniqueValue(uniqueValues.get(record.getUniqueValue()));
//		}
	}

	public void fillLeftUniqueValueMap(UniqueValuesMapRepo uvMapRepo, String accountId) {
//		List<String> uniqueIns = new ArrayList<String>();
//		for (Record record : getAllRecords()) {
//			uniqueIns.add(record.getUniqueValue());
//		}
//		HashMap<String, String> uniqueValues = new HashMap<String, String>();
//		List<UniqueValuesMap> uniqueValuesMap = uvMapRepo.findByAccountIdAndLeftUniqueValueIn(accountId, uniqueIns);
//		for (UniqueValuesMap uniqueValuesMap2 : uniqueValuesMap) {
//			uniqueValues.put(uniqueValuesMap2.getLeftUniqueValue(), uniqueValuesMap2.getRightUniqueValue());
//		}
//		for (Record record : getAllRecords()) {
//			record.setMappedRecordUniqueValue(uniqueValues.get(record.getUniqueValue()));
//		}
	}
}
