package com.oapps.osync.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oapps.osync.controller.Controller;
import com.oapps.osync.controller.ControllerRepo;
import com.oapps.osync.entity.FieldMapEntity;
import com.oapps.osync.entity.IntegrationPropsEntity;
import com.oapps.osync.entity.IntegrationStatusEntity;
import com.oapps.osync.entity.ModuleInfoEntity;
import com.oapps.osync.entity.ServiceInfoEntity;
import com.oapps.osync.entity.SyncLogEntity;
import com.oapps.osync.entity.UniqueValuesMapEntity;
import com.oapps.osync.fields.Record;
import com.oapps.osync.fields.RecordSet;
import com.oapps.osync.repository.FieldMapRepository;
import com.oapps.osync.repository.IntegrationPropsRepository;
import com.oapps.osync.repository.IntegrationStatusRepository;
import com.oapps.osync.repository.ModuleInfoRepository;
import com.oapps.osync.repository.ServiceInfoRepository;
import com.oapps.osync.repository.SyncLogEntityRepo;
import com.oapps.osync.repository.UniqueValuesMapRepo;
import com.oapps.osync.service.OsyncEnums.IntegrationStatus;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
@Service
public class IntegrationServiceImpl implements IntegrationService {

	@Autowired
	SyncLogEntityRepo logRepo;

	@Autowired
	IntegrationPropsRepository intPropsRepo;

	@Autowired
	IntegrationStatusRepository intStatusRepo;

	@Autowired
	ModuleInfoRepository moduleInfoRepo;

	@Autowired
	ServiceInfoRepository serviceInfoRepo;

	@Autowired
	FieldMapRepository fieldMapRepo;

	@Autowired
	UniqueValuesMapRepo uvMapRepo;

	public SyncLogEntity sync2(@NonNull Long osyncId, @NonNull Long integId) throws OsyncException {
		SyncLogEntity logEntity = new SyncLogEntity();
		Long syncStartTime = System.currentTimeMillis();
		try {
			Optional<IntegrationPropsEntity> intPropsOpt = intPropsRepo.findById(integId);
			if (!intPropsOpt.isPresent()) {
				throw new OsyncException("Integration ID Not present");
			}
			IntegrationPropsEntity intProps = intPropsOpt.get();
			Optional<IntegrationStatusEntity> intStatus = intStatusRepo.findById(integId);
			Long startTime = -1l;
			Long endTime = System.currentTimeMillis();
			if (!intStatus.isPresent()) {
				IntegrationStatusEntity status = intStatus.get();
				if (status.getStatus().equals(IntegrationStatus.COMPLETE)) {
					startTime = status.getEndTime().getTime();
				} else if (status.getStatus().equals(IntegrationStatus.RUNNING)) {
					log.severe("New sync initiated, but old sync still in progress, " + integId + intProps + intStatus);
					logEntity.setStatus(IntegrationStatus.NOT_STARTED);
					throw new OsyncException("Sync in progress");
				} else if (status.getStatus().equals(IntegrationStatus.ERROR)) {
					log.severe("Old sync ended in error. Please check the status. " + integId + intProps + intStatus);
					logEntity.setStatus(IntegrationStatus.NOT_STARTED);
					throw new OsyncException("Old sync ended in error. Please check the status.");
				}
			}

			@NonNull
			ServiceInfoEntity serviceA = getServiceInfo(intProps.getLeftServiceId());
			@NonNull
			ModuleInfoEntity moduleA = getModuleInfo(intProps.getLeftModuleId());

			@NonNull
			ServiceInfoEntity serviceB = getServiceInfo(intProps.getRightServiceId());
			@NonNull
			ModuleInfoEntity moduleB = getModuleInfo(intProps.getRightModuleId());

			@NonNull
			List<FieldMapEntity> fieldMaps = fieldMapRepo.findAllByIntegId(integId);

			@NonNull

			Controller controllerA = getControllerInstance(serviceA.getName(), moduleA.getName());
			Controller controllerB = getControllerInstance(serviceB.getName(), moduleB.getName());
			// controller.fetchUpdatedRecords(osyncId, lastSyncTime)
			int startPage = 1;
			int totalRecords = 100;
			boolean isLeft = true;

			RecordSet recordSetA = controllerA.fetchRecords(startPage, totalRecords, startTime, endTime);
			recordSetA.fillUniqueValueMap(uvMapRepo, integId, isLeft);
			
			RecordSet toCreate = RecordSet.init(serviceB.getName(), moduleB.getPrimaryColumn());

			while (recordSetA.count() > 0) {
				List<String> recordsToFetchRemote = new ArrayList<String>();
				for (Record record : recordSetA) {

					if (intProps.isSyncRecordsWithEmail() && record.getValue(moduleA.getEmailColumn()) == null) {
						// do not sync the records without email
						continue;
					}

					boolean isUpdate = record.getMappedRecordUniqueValue() != null;

					if (!isUpdate) {
						// create block
						if (record.getMappedRecordUniqueValue() == null && intProps.isLookupUniqueColumn()) {
							isUpdate = findMatchedRecord(intProps, controllerB, record, isUpdate,
									moduleA.getUniqueColumn(), true);
						}
					}

					if (isUpdate) {
						recordsToFetchRemote.add(record.getMappedRecordUniqueValue());
					} else {
						Record nrecord = toCreate.createEmptyObject();
						nrecord = fillRecord(nrecord, record, fieldMaps, true);
						nrecord.setMappedRecordUniqueValue(record.getUniqueValue());
					}
				}
				if (recordsToFetchRemote.size() > 0) {
					RecordSet recordSetB = controllerB.getMatchedRecordsByUniqueId(recordsToFetchRemote);
					
					/*
					 * Record record = rightRecordsToUpdate.add(leftRecord.getMappedRecordUniqueValue());
				record = fillRecord(record, leftRecord, field	Maps, true);
				record.setMappedRecordUniqueValue(leftRecord.getUniqueValue());
					 */
				}
			}

		} finally {
			logEntity.setStartTime(new Date(syncStartTime));
			logEntity.setEndTime(new Date(System.currentTimeMillis()));
			logEntity.setIntegId(integId);
			logEntity.setOsyncId(osyncId);
			logRepo.save(logEntity);
		}
		return null;
	}

	@Override
	public SyncLogEntity sync(Long integId) throws OsyncException {

		Optional<IntegrationPropsEntity> intPropsOpt = intPropsRepo.findById(integId);

		if (!intPropsOpt.isPresent()) {
			throw new OsyncException("Integration ID Not present");
		}
		@NonNull
		IntegrationPropsEntity intProps = intPropsOpt.get();
		@NonNull
		Long osyncId = intProps.getOsyncId();

		SyncLogEntity logEntity = new SyncLogEntity();

		@NonNull
		List<FieldMapEntity> fieldMaps = fieldMapRepo.findAllByIntegId(integId);

		@NonNull
		ServiceInfoEntity leftServiceInfo = getServiceInfo(intProps.getLeftServiceId());
		@NonNull
		ServiceInfoEntity rightServiceInfo = getServiceInfo(intProps.getRightServiceId());

		@NonNull
		ModuleInfoEntity leftModuleInfo = getModuleInfo(intProps.getLeftModuleId());
		@NonNull
		ModuleInfoEntity rightModuleInfo = getModuleInfo(intProps.getRightModuleId());

		@NonNull
		Controller left = getControllerInstance(leftServiceInfo.getName(), leftModuleInfo.getName());
		@NonNull
		Controller right = getControllerInstance(rightServiceInfo.getName(), rightModuleInfo.getName());

		RecordSet leftRecordSet = left.fetchUpdatedRecords(osyncId, System.currentTimeMillis() - (10 * 60 * 1000l));
		leftRecordSet.fillLeftUniqueValueMap(uvMapRepo, integId);

		logEntity.setLeftCountFetched(leftRecordSet.count());

		RecordSet leftRecordsToCreate = RecordSet.init(leftServiceInfo.getName(), leftModuleInfo.getPrimaryColumn());
		RecordSet rightRecordsToCreate = RecordSet.init(rightServiceInfo.getName(), rightModuleInfo.getPrimaryColumn());

		RecordSet leftRecordsToUpdate = RecordSet.init(leftServiceInfo.getName(), leftModuleInfo.getPrimaryColumn());
		RecordSet rightRecordsToUpdate = RecordSet.init(rightServiceInfo.getName(), rightModuleInfo.getPrimaryColumn());

		int leftSkipped = 0;
		int rightSkipped = 0;
		int matched = 0;
		for (Record leftRecord : leftRecordSet) {

			if (intProps.isSyncRecordsWithEmail() && leftRecord.getValue(leftModuleInfo.getEmailColumn()) == null) {
				// do not sync the records without email
				leftSkipped++;
				continue;
			}
			boolean isUpdate = leftRecord.getMappedRecordUniqueValue() != null;

			if (leftRecord.getMappedRecordUniqueValue() == null && intProps.isLookupUniqueColumn()) {
				isUpdate = findMatchedRecord(intProps, right, leftRecord, isUpdate, leftModuleInfo.getUniqueColumn(),
						true);
				if (isUpdate) {
					matched++;
				}
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

		RecordSet rightRecordSet = right.fetchUpdatedRecords(osyncId, System.currentTimeMillis() - (10 * 60 * 1000l));
		logEntity.setRightCountFetched(rightRecordSet.count());
		rightRecordSet.fillRightUniqueValueMap(uvMapRepo, integId);

		for (Record rightRecord : rightRecordSet) {

			if (intProps.isSyncRecordsWithEmail() && rightRecord.getValue(rightModuleInfo.getEmailColumn()) == null) {
				// do not sync the records without email
				rightSkipped++;
				continue;
			}
			boolean isUpdate = rightRecord.getMappedRecordUniqueValue() != null;

			if (rightRecord.getMappedRecordUniqueValue() == null && intProps.isLookupUniqueColumn()) {
				isUpdate = findMatchedRecord(intProps, left, rightRecord, isUpdate, rightModuleInfo.getUniqueColumn(),
						false);
				if (isUpdate) {
					matched++;
				}
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

		if (leftRecordsToCreate.count() > 0) {
			for (Record record : leftRecordsToCreate) {
				log.info("Creating records :" + record);
			}
			HashMap<String, String> leftUniqueValueMap = left.createNewRecords(leftRecordsToCreate,
					rightServiceInfo.getName());

			List<UniqueValuesMapEntity> list = new ArrayList<UniqueValuesMapEntity>();
			for (Entry<String, String> record : leftUniqueValueMap.entrySet()) {
				UniqueValuesMapEntity uvMap = new UniqueValuesMapEntity();
				uvMap.setIntegId(integId);
				uvMap.setOsyncId(intProps.getOsyncId());
				uvMap.setLeftUniqueValue(record.getKey());
				uvMap.setRightUniqueValue(record.getValue());
				list.add(uvMap);
			}
			logEntity.setCreatedLeftCount(leftRecordsToCreate.count());
			uvMapRepo.saveAll(list);
		}

		if (rightRecordsToCreate.count() > 0) {
			for (Record record : rightRecordsToCreate) {
				log.info("Creating records :" + record);
			}
			HashMap<String, String> rightUniqueValueMap = right.createNewRecords(rightRecordsToCreate,
					leftServiceInfo.getName());

			List<UniqueValuesMapEntity> list = new ArrayList<UniqueValuesMapEntity>();
			for (Entry<String, String> record : rightUniqueValueMap.entrySet()) {
				UniqueValuesMapEntity uvMap = new UniqueValuesMapEntity();
				uvMap.setIntegId(integId);
				uvMap.setOsyncId(intProps.getOsyncId());
				uvMap.setRightUniqueValue(record.getKey());
				uvMap.setLeftUniqueValue(record.getValue());
				list.add(uvMap);
			}
			logEntity.setCreatedRightCount(rightRecordsToCreate.count());
			uvMapRepo.saveAll(list);
		}

		if (leftRecordsToUpdate.count() > 0) {
			logEntity.setUpdatedLeftCount(leftRecordsToUpdate.count());
			left.updateRecords(leftRecordsToUpdate, rightServiceInfo.getName());
		}

		if (rightRecordsToUpdate.count() > 0) {
			logEntity.setUpdatedRightCount(rightRecordsToUpdate.count());
			right.updateRecords(rightRecordsToUpdate, leftServiceInfo.getName());
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
		logEntity.setOsyncId(osyncId);
		logEntity.setIntegId(integId);
		logEntity.setMatchedOnUniqueColumn(matched);
		logEntity.setLeftSkippedForEmailColumn(leftSkipped);
		logEntity.setRightSkippedForEmailColumn(rightSkipped);
		return logRepo.save(logEntity);
	}

	public ModuleInfoEntity getModuleInfo(Long leftModuleId) {
		Optional<ModuleInfoEntity> findById = moduleInfoRepo.findById(leftModuleId);
		return findById.isPresent() ? findById.get() : null;
	}

	public ServiceInfoEntity getServiceInfo(Long leftServiceId) {
		Optional<ServiceInfoEntity> findById = serviceInfoRepo.findById(leftServiceId);
		return findById.isPresent() ? findById.get() : null;
	}

	private Controller getControllerInstance(String serviceName, String moduleName) {
		return ControllerRepo.getInstance(serviceName, moduleName);
	}

	private boolean findMatchedRecord(IntegrationPropsEntity intProps, Controller controller, Record record,
			boolean isUpdate, String uniqueColumn, boolean isLeftToRight) {
		// if (record.getMappedRecordUniqueValue() == null &&
		// intProps.isLookupUniqueColumn()) {
		String uniqueValue = record.getValue(uniqueColumn).toString();
		if (uniqueValue != null && !uniqueValue.trim().isEmpty() && !uniqueValue.equals("null")) {
			Record matchedRecord = controller.getMatchedRecord(uniqueValue);
			if (matchedRecord != null) {
				// it is present, create a uvMap and mark it as record to update
				UniqueValuesMapEntity uniqueValuesMap = new UniqueValuesMapEntity();
				uniqueValuesMap.setIntegId(intProps.getIntegId());
				uniqueValuesMap.setOsyncId(intProps.getOsyncId());
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

	private Record fillRecord(Record destRecord, Record srcRecord, List<FieldMapEntity> fieldMaps,
			boolean isLeftToRight) {
		for (FieldMapEntity fieldMap : fieldMaps) {
			String srcColumn;
			String srcColumnType;
			String destColumn;
			String destColumnType;
			if (isLeftToRight) {
				srcColumn = fieldMap.getLeftColumnName();
				srcColumnType = fieldMap.getLeftColumnType();
				destColumn = fieldMap.getRightColumnName();
				destColumnType = fieldMap.getRightColumnType();
			} else {
				srcColumn = fieldMap.getRightColumnName();
				srcColumnType = fieldMap.getRightColumnType();
				destColumn = fieldMap.getLeftColumnName();
				destColumnType = fieldMap.getLeftColumnType();
			}
			Object value = srcRecord.getValue(srcColumn);
			if (isNull(value)) {
				value = "";
			}
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
		return destRecord;
	}

	private boolean isNull(Object value) {
		return value == null || value.toString().trim().isEmpty() || value.toString().equalsIgnoreCase("null");
	}
}
