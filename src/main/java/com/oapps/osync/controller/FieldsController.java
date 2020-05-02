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
import com.oapps.osync.entity.FieldMap;
import com.oapps.osync.entity.IntegrationPropsEntity;
import com.oapps.osync.entity.UniqueValuesMap;
import com.oapps.osync.fields.Fields;
import com.oapps.osync.fields.Record;
import com.oapps.osync.fields.RecordSet;
import com.oapps.osync.repository.FieldMapRepository;
import com.oapps.osync.repository.IntegrationPropsRepository;
import com.oapps.osync.repository.UniqueValuesMapRepo;

import lombok.extern.java.Log;

@RestController
@Log
public class FieldsController {

	@Autowired
	IntegrationPropsRepository intPropsRepo;

	@Autowired
	FieldMapRepository fieldMapRepo;

	@Autowired
	UniqueValuesMapRepo uvMapRepo;

	@GetMapping(path = "/fields")
	public @ResponseBody Fields getAllFields(@RequestParam String type, @RequestParam("module") String moduleName) {
		return ControllerRepo.getInstance(type, moduleName).getFields();
	}

	@GetMapping(path = "/fields/sync")
	public @ResponseBody String sync(@RequestParam(name = "account") String accountId) {
		IntegrationPropsEntity intProps = intPropsRepo.findByAccountId(accountId);
		List<FieldMap> fieldMaps = fieldMapRepo.findByAccountId(accountId);

		Controller left = ControllerRepo.getInstance(intProps.getLeftControllerName(), intProps.getLeftModuleName());
		Controller right = ControllerRepo.getInstance(intProps.getRightControllerName(), intProps.getRightModuleName());

		RecordSet leftRecordSet = left.fetchUpdatedRecords(accountId, System.currentTimeMillis() - (10 * 60 * 1000l));
		

		leftRecordSet.fillLeftUniqueValueMap(uvMapRepo, accountId);

		

		RecordSet rightRecordsToCreate = RecordSet.init(intProps.getRightControllerName(),
				intProps.getRightPrimaryColumn());
		RecordSet leftRecordsToCreate = RecordSet.init(intProps.getLeftControllerName(),
				intProps.getLeftPrimaryColumn());

		RecordSet rightRecordsToUpdate = RecordSet.init(intProps.getRightControllerName(),
				intProps.getRightPrimaryColumn());
		RecordSet leftRecordsToUpdate = RecordSet.init(intProps.getLeftControllerName(),
				intProps.getLeftPrimaryColumn());

		for (Record leftRecord : leftRecordSet) {

			if (intProps.isSyncRecordsWithEmail() && leftRecord.getValue(intProps.getLeftEmailColumn()) == null) {
				// do not sync the records without email
				continue;
			}
			boolean isUpdate = leftRecord.getMappedRecordUniqueValue() != null;

			if (leftRecord.getMappedRecordUniqueValue() == null && intProps.isLookupUniqueColumn()) {
				isUpdate = findMatchedRecord(accountId, right, leftRecord, isUpdate, intProps.getLeftUniqueColumn(), true);
			}

			if (isUpdate) {
				// update now
				Record record = rightRecordsToUpdate.add(leftRecord.getMappedRecordUniqueValue());
				record = fillRecord(record, leftRecord, fieldMaps, true);
				record.setMappedRecordUniqueValue(leftRecord.getUniqueValue());
			} else {
				// create now
				Record record = rightRecordsToCreate.createEmptyObject();
				record = fillRecord(record, leftRecord, fieldMaps, true);
				record.setMappedRecordUniqueValue(leftRecord.getUniqueValue());
			}
		}

		RecordSet rightRecordSet = right.fetchUpdatedRecords(accountId, System.currentTimeMillis() - (10 * 60 * 1000l));
		rightRecordSet.fillRightUniqueValueMap(uvMapRepo, accountId);
		
		for (Record rightRecord : rightRecordSet) {

			if (intProps.isSyncRecordsWithEmail() && rightRecord.getValue(intProps.getRightEmailColumn()) == null) {
				// do not sync the records without email
				continue;
			}
			boolean isUpdate = rightRecord.getMappedRecordUniqueValue() != null;

			if (rightRecord.getMappedRecordUniqueValue() == null && intProps.isLookupUniqueColumn()) {
				isUpdate = findMatchedRecord(accountId, left, rightRecord, isUpdate, intProps.getRightUniqueColumn(), false);
			}

			if (isUpdate) {
				// update now
				Record record = leftRecordsToUpdate.add(rightRecord.getMappedRecordUniqueValue());
				record = fillRecord(record, rightRecord, fieldMaps, false);
				record.setMappedRecordUniqueValue(rightRecord.getUniqueValue());
			} else {
				// create now
				Record record = leftRecordsToCreate.createEmptyObject();
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
			for (Record record : leftRecordsToCreate) {
				log.info("Creating records :" + record);
			}
			HashMap<String, String> leftUniqueValueMap = left.createNewRecords(leftRecordsToCreate,
					intProps.getRightControllerName());

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

		if (rightRecordsToCreate.length() > 0) {
			for (Record record : rightRecordsToCreate) {
				log.info("Creating records :" + record);
			}
			HashMap<String, String> rightUniqueValueMap = right.createNewRecords(rightRecordsToCreate,
					intProps.getLeftControllerName());

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
			left.updateRecords(leftRecordsToUpdate, intProps.getRightControllerName());
		}

		if (rightRecordsToUpdate.length() > 0) {
			right.updateRecords(rightRecordsToUpdate, intProps.getLeftControllerName());
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

	private boolean findMatchedRecord(String accountId, Controller controller, Record record, boolean isUpdate,
			String uniqueColumn, boolean isLeftToRight) {
		// if (record.getMappedRecordUniqueValue() == null &&
		// intProps.isLookupUniqueColumn()) {
		String uniqueValue = record.getValue(uniqueColumn).toString();
		if (uniqueValue != null && !uniqueValue.trim().isEmpty() && !uniqueValue.equals("null")) {
			Record matchedRecord = controller.getMatchedRecord(uniqueValue);
			if (matchedRecord != null) {
				// it is present, create a uvMap and mark it as record to update
				UniqueValuesMap uniqueValuesMap = new UniqueValuesMap();
				uniqueValuesMap.setAccountId(accountId);
				if (isLeftToRight) {
					uniqueValuesMap.setLeftUniqueValue(record.getUniqueValue());
					uniqueValuesMap.setRightUniqueValue(matchedRecord.getUniqueValue());
				} else {
					uniqueValuesMap.setRightUniqueValue(record.getUniqueValue());
					uniqueValuesMap.setLeftUniqueValue(matchedRecord.getUniqueValue());
				}
				uvMapRepo.save(uniqueValuesMap);

				record.setMappedRecordUniqueValue(matchedRecord.getUniqueValue());
				isUpdate = true;
			}
		}
		return isUpdate;
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
