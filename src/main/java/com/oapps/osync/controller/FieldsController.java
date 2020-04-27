package com.oapps.osync.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.oapps.osync.Controller;
import com.oapps.osync.ControllerRepo;
import com.oapps.osync.entity.EntityMap;
import com.oapps.osync.entity.FieldMap;
import com.oapps.osync.entity.UniqueValuesMap;
import com.oapps.osync.fields.Fields;
import com.oapps.osync.fields.Record;
import com.oapps.osync.fields.RecordSet;
import com.oapps.osync.repository.EntityMapRepository;
import com.oapps.osync.repository.FieldMapRepository;
import com.oapps.osync.repository.UniqueValuesMapRepo;

import lombok.extern.java.Log;

@RestController
@Log
public class FieldsController {

	@Autowired
	EntityMapRepository entityMapRepo;

	@Autowired
	FieldMapRepository fieldMapRepo;

	@Autowired
	UniqueValuesMapRepo uvMapRepo;

	@GetMapping(path = "/fields")
	public @ResponseBody Fields getAllFields(@RequestParam String type, @RequestParam String integration) {
		return ControllerRepo.getInstance(type).getFields(integration);
	}

	@GetMapping(path = "/fields/sync")
	public @ResponseBody String sync(@RequestParam(name = "account") String accountId) {
		EntityMap entity = entityMapRepo.findByAccountId(accountId);
		List<FieldMap> fieldMaps = fieldMapRepo.findByAccountId(accountId);

		Controller left = ControllerRepo.getInstance(entity.getLeftControllerName());
		Controller right = ControllerRepo.getInstance(entity.getRightControllerName());

		RecordSet leftRecordSet = left.fetchUpdatedRecords(accountId, System.currentTimeMillis() - (10 * 60 * 1000l));
		RecordSet rightRecordSet = right.fetchUpdatedRecords(accountId, System.currentTimeMillis() - (10 * 60 * 1000l));

		leftRecordSet.fillLeftUniqueValueMap(uvMapRepo, accountId);
		
		rightRecordSet.fillRightUniqueValueMap(uvMapRepo, accountId);

		RecordSet rightRecordsToCreate = RecordSet.of(entity.getRightControllerName(), entity.getRightUniqueColumn());
		RecordSet leftRecordsToCreate = RecordSet.of(entity.getLeftControllerName(), entity.getLeftUniqueColumn());

		RecordSet rightRecordsToUpdate = RecordSet.of(entity.getRightControllerName(), entity.getRightUniqueColumn());
		RecordSet leftRecordsToUpdate = RecordSet.of(entity.getLeftControllerName(), entity.getLeftUniqueColumn());

		for (Record leftRecord : leftRecordSet) {

			if (leftRecord.getMappedRecordUniqueValue() == null) {
				Record record = rightRecordsToCreate.createRecordObject();
				record = fillRecord(record, leftRecord, fieldMaps, true);
				record.setMappedRecordUniqueValue(leftRecord.getUniqueValue());
			} else {
				Record record = rightRecordsToUpdate.add(leftRecord.getMappedRecordUniqueValue());
				record = fillRecord(record, leftRecord, fieldMaps, true);
				record.setMappedRecordUniqueValue(leftRecord.getUniqueValue());
			}

		}

		for (Record rightRecord : rightRecordSet) {

			if (rightRecord.getMappedRecordUniqueValue() == null) {
				Record record = leftRecordsToCreate.createRecordObject();
				record = fillRecord(record, rightRecord, fieldMaps, false);
				record.setMappedRecordUniqueValue(rightRecord.getUniqueValue());
			} else {
				Record record = leftRecordsToUpdate.add(rightRecord.getMappedRecordUniqueValue());
				record = fillRecord(record, rightRecord, fieldMaps, false);
				record.setMappedRecordUniqueValue(rightRecord.getUniqueValue());
			}
		}

		// TODO :
		// Conflict mgmt
		// one way vs two way
		// fill unique value vs dest unique vals
		// scheduler
		// bulk vs single
		// field validation while copying

		if (leftRecordsToCreate.length() > 0) {
			HashMap<String, String> leftUniqueValueMap = left.createNewRecords(leftRecordsToCreate,
					entity.getRightControllerName());

			List<UniqueValuesMap> list = new ArrayList<UniqueValuesMap>();
			for (Entry<String, String> record : leftUniqueValueMap.entrySet()) {
				UniqueValuesMap uvMap = new UniqueValuesMap();
				uvMap.setAccountId(accountId);
				uvMap.setLeftUniqueValue(record.getKey());
				uvMap.setRightUniqueValue(record.getValue());
				list.add(uvMap);
			}
			uvMapRepo.saveAll(list);
		}
		
		if(rightRecordsToCreate.length() > 0 ) {
			HashMap<String, String> rightUniqueValueMap = right.createNewRecords(rightRecordsToCreate,
					entity.getLeftControllerName());
			
			List<UniqueValuesMap> list = new ArrayList<UniqueValuesMap>();
			for (Entry<String, String> record : rightUniqueValueMap.entrySet()) {
				UniqueValuesMap uvMap = new UniqueValuesMap();
				uvMap.setAccountId(accountId);
				uvMap.setRightUniqueValue(record.getKey());
				uvMap.setLeftUniqueValue(record.getValue());
				list.add(uvMap);
			}

			uvMapRepo.saveAll(list);
		}

		
		if (leftRecordsToUpdate.length() > 0) {
			left.updateRecords(leftRecordsToUpdate, entity.getRightControllerName());
		}

		if (rightRecordsToUpdate.length() > 0) {
			right.updateRecords(rightRecordsToUpdate, entity.getLeftControllerName());
		}

		/*
		 * Cases: A. Can be first time sync // this will be handled separately
		 * 
		 * B. Can be other time sync // we will handle this here
		 * 
		 * 1. Fetch the records that were updated after last update for controller left
		 * 2. Fetch the records that were updated after last update for controller right
		 * 3. It can be left way or right way
		 * 
		 * If one way 1. Take the updated records from source 2. fetch the unique id of
		 * source 3. using the unique id of source fetch the destination unique id from
		 * our db 4. if null, add to destination and map the unique id in our local db
		 * 5. if not null, fetch the record, merge the new value and push
		 * 
		 * If two way, 1. Take the updated records from source 2. fetch the unique id of
		 * source 3. using the unique id of source fetch the destination unique id from
		 * our db 4. if null, add to destination and map the unique id in our local db
		 * 5. if not null, fetch the record, merge the new value and push, push back to
		 * source also 6. Repeat the same for destination and add only new records
		 * present in dest to source
		 * 
		 * 
		 */

		return null;
	}

	private Record fillRecord(Record destRecord, Record srcRecord, List<FieldMap> fieldMaps, boolean isLeftToRight) {
		for (FieldMap fieldMap : fieldMaps) {
			String srcColumn;
			String srcColumnType;
			String destColumn;
			String destColumnType;
			if (isLeftToRight) {
				srcColumn = fieldMap.getLeftColumn();
				srcColumnType = fieldMap.getLeftColumnType();
				destColumn = fieldMap.getRightColumn();
				destColumnType = fieldMap.getRightColumnType();
			} else {
				srcColumn = fieldMap.getRightColumn();
				srcColumnType = fieldMap.getRightColumnType();
				destColumn = fieldMap.getLeftColumn();
				destColumnType = fieldMap.getLeftColumnType();
			}
			Object value = srcRecord.getValue(srcColumn);
			if (value != null) {
				if (!srcColumnType.equals(destColumnType)) {
					log.info("Source and destination column types are not equal. Continuing for now.");
				}
				switch (destColumnType) {
				case "boolean":
					destRecord.addNewValue(destColumn, Boolean.valueOf(value.toString()));
					break;
				case "integer":
					try {
						destRecord.addNewValue(destColumn, Integer.valueOf(value.toString()));
					} catch (NumberFormatException e) {
					}

					break;
				case "text":
					destRecord.addNewValue(destColumn, value.toString());
					break;

				}
			}

		}
		return destRecord;
	}
}
