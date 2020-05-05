package com.oapps.osync.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
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
			if (intStatus.isPresent()) {
				IntegrationStatusEntity status = intStatus.get();
				if (status.getStatus().equals(IntegrationStatus.COMPLETE)) {
					startTime = status.getEndTime().getTime();

					status.setPrevStartTime(status.getStartTime());
					status.setPrevEndTime(status.getEndTime());

					status.setStartTime(status.getEndTime());
					status.setEndTime(new Date(endTime));

					status.setStatus(IntegrationStatus.RUNNING);

					intStatusRepo.save(status);

				} else if (status.getStatus().equals(IntegrationStatus.RUNNING)) {
					log.severe("New sync initiated, but old sync still in progress, " + integId + intProps + intStatus);
					logEntity.setStatus(IntegrationStatus.NOT_STARTED);
					// throw new OsyncException("Sync in progress");
				} else if (status.getStatus().equals(IntegrationStatus.ERROR)) {
					log.severe("Old sync ended in error. Please check the status. " + integId + intProps + intStatus);
					logEntity.setStatus(IntegrationStatus.NOT_STARTED);
					// throw new OsyncException("Old sync ended in error. Please check the
					// status.");
				}

			} else {
				IntegrationStatusEntity status = new IntegrationStatusEntity();
				status.setStatus(IntegrationStatus.RUNNING);
				status.setIntegId(integId);
				status.setOsyncId(osyncId);
				status.setEndTime(new Date(endTime));
				intStatusRepo.save(status);
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
			boolean isLeft = true;

			logEntity.setLeftService(serviceA.getName());
			logEntity.setRightService(serviceB.getName());
			logEntity.setLeftModule(moduleA.getName());
			logEntity.setRightModule(moduleB.getName());

			syncOneWay(intProps, controllerA, controllerB, startTime, endTime, serviceA, moduleA, serviceB, moduleB,
					fieldMaps, isLeft, logEntity);
			syncOneWay(intProps, controllerB, controllerA, startTime, endTime, serviceB, moduleB, serviceA, moduleA,
					fieldMaps, !isLeft, logEntity);

		} finally {
			logEntity.setStartTime(new Date(syncStartTime));
			logEntity.setEndTime(new Date(System.currentTimeMillis()));
			logEntity.setIntegId(integId);
			logEntity.setOsyncId(osyncId);
			logEntity.setStatus(IntegrationStatus.COMPLETE);
			logRepo.save(logEntity);
		}
		return logEntity;
	}

	private void syncOneWay(IntegrationPropsEntity intProps, Controller controllerA, Controller controllerB,
			Long startTime, Long endTime, ServiceInfoEntity serviceA, ModuleInfoEntity moduleA,
			ServiceInfoEntity serviceB, ModuleInfoEntity moduleB, List<FieldMapEntity> fieldMaps, boolean isLeft,
			SyncLogEntity logEntity) {
		int startPage = 1;
		int totalRecords = 10;
		Long integId = intProps.getIntegId();

		RecordSet recordSetA = controllerA.fetchRecords(startPage, totalRecords, startTime);
		recordSetA.fillUniqueValueMap(uvMapRepo, integId, isLeft);

		do {
			ilog("Sync start", "start", startPage, "totalRecords", totalRecords, "integId", integId, "isLeft", isLeft,
					"Service A", serviceA.getName(), "Module A", moduleA.getName(), "Service B", serviceB.getName(),
					"Module B", moduleB.getName());
			RecordSet toCreate = RecordSet.init(serviceB.getName(), moduleB.getPrimaryColumn());
			RecordSet toUpdate = RecordSet.init(serviceB.getName(), moduleB.getPrimaryColumn());

			logEntity.addFetchCount(recordSetA.count(), isLeft);

			List<String> recordsToFetchB = new ArrayList<String>();
			HashMap<String, String> fetchByUniqueColumn = new HashMap<String, String>();
			for (Record record : recordSetA) {

				if (intProps.isSyncRecordsWithEmail() && record.getValue(moduleA.getEmailColumn()) == null) {
					// do not sync the records without email
					logEntity.incrementSkippedCount(isLeft);
					continue;
				}

				boolean isUpdate = record.getMappedRecordUniqueValue() != null;

				if (isUpdate) {
					recordsToFetchB.add(record.getMappedRecordUniqueValue());
				} else {
					// create
					if (intProps.isLookupUniqueColumn()) {
						// lookup and then decide
						String uniqueValue = record.getValue(moduleA.getUniqueColumn()).toString();
						fetchByUniqueColumn.put(record.getUniqueValue(), uniqueValue);
					} else {
						// no need to lookup, add as new record
						Record nrecord = toCreate.createEmptyObject();
						compareAndFillRecord(nrecord, record, null, fieldMaps, isLeft);
						nrecord.setMappedRecordUniqueValue(record.getUniqueValue());
					}
				}
			}
			ilog("Counts", "Record count to fetch from B", recordsToFetchB.size(),
					"Record count to check for unique columns", fetchByUniqueColumn.size());
			if (recordsToFetchB.size() > 0) {
				// updates
				RecordSet recordSetB = controllerB.getMatchedRecordsById(recordsToFetchB);
				recordSetB.fillUniqueValueMap(uvMapRepo, integId, !isLeft);

				for (Record recordA : recordSetA) {
					Record recordB = recordSetB.find(recordA.getMappedRecordUniqueValue());
					if (recordB != null) {
						Record newRecord = toUpdate.add(recordA.getMappedRecordUniqueValue());
						boolean hasAnyChange = compareAndFillRecord(newRecord, recordA, recordB, fieldMaps, isLeft);
						if (hasAnyChange) {
							newRecord.setMappedRecordUniqueValue(recordA.getUniqueValue());
						} else {
							// there is no change in the object, hence removing from the overall list
							logEntity.incrNoChangeCount(isLeft);
							toUpdate.remove(recordA.getMappedRecordUniqueValue());
						}

					}
				}
			}

			int totalCreateAfterMatch = 0;
			int totalUpdateAfterMatch = 0;

			if (fetchByUniqueColumn.size() > 0) {
				// creates
				RecordSet uniqueRecordSetB = controllerB.getMatchedRecordsByUniqueColumn(fetchByUniqueColumn.values());
				uniqueRecordSetB.setUniqueColumnName(moduleB.getUniqueColumn());
				for (Entry<String, String> entry : fetchByUniqueColumn.entrySet()) {
					String id = entry.getKey();
					String uniqueValue = entry.getValue();
					Record recordA = recordSetA.find(id);
					Record recordB = uniqueRecordSetB.findByUniqueColumn(uniqueValue);
					if (recordB == null) {
						Record nrecord = toCreate.createEmptyObject();
						compareAndFillRecord(nrecord, recordA, null, fieldMaps, isLeft);
						nrecord.setMappedRecordUniqueValue(recordA.getUniqueValue());
						totalCreateAfterMatch++;
					} else {
						addUVMapping(intProps, recordA, recordB, moduleA.getUniqueColumn(), isLeft);
						logEntity.incrementUniqueColumnMatch();
						Record newRecord = toUpdate.add(recordA.getMappedRecordUniqueValue());
						compareAndFillRecord(newRecord, recordA, recordB, fieldMaps, isLeft);
						totalUpdateAfterMatch++;
					}
				}
				ilog("Matched unique columns", "Total create after match", totalCreateAfterMatch,
						" Total update after match", totalUpdateAfterMatch);
			}
			ilog("Overall count", "Create", toCreate.count(), "Update", toUpdate.count());
			if (toCreate.count() > 0) {
				logEntity.addCreatedCount(toCreate.count(), !isLeft);
				controllerB.createNewRecords(toCreate, null);
			}
			if (toUpdate.count() > 0) {
				logEntity.addUpdatedCount(toUpdate.count(), !isLeft);
				controllerB.updateRecords(toUpdate, null);
			}

			startPage++;
			if (recordSetA.count() == totalRecords) {
				recordSetA = controllerA.fetchRecords(startPage, totalRecords, startTime);
				recordSetA.fillUniqueValueMap(uvMapRepo, integId, isLeft);
			} else {
				ilog("Sync completed.");
				// we are done here
				break;
			}
			// TODO: Temp
		} while (recordSetA.count() > 0);
	}

	private void ilog(String message, Object... values) {
		// int startPage, int totalRecords, Long integId, boolean isLeft, String name,
		StringBuilder logMessage = new StringBuilder();
		try {
			logMessage.append(message).append(", ");
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					if (values[i] == null) {
						logMessage.append("NULL").append(": ");
					} else {
						logMessage.append(values[i]).append(": ");
					}
					i++;
					if (values.length > i) {
						if (values[i] == null) {
							logMessage.append("NULL").append(", ");
						} else {
							logMessage.append(values[i]).append(", ");
						}
					}
				}
			}
			log.info(logMessage.toString());
		} catch (Exception e) {
			log.log(Level.WARNING, "Error in formatting the log," + message + " {*}", values);
			log.log(Level.WARNING, logMessage.toString(), e);
		}

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

	private boolean addUVMapping(IntegrationPropsEntity intProps, Record record, Record matchedRecord,
			String uniqueColumn, boolean isLeftToRight) {
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
		return true;
	}

	private boolean compareAndFillRecord(Record newRecord, Record recordA, Record recordB,
			List<FieldMapEntity> fieldMaps, boolean isLeftToRight) {
		boolean hasAnyChange = false;
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
			if (!srcColumnType.equals(destColumnType)) {
				log.info("Source and destination column types are not equal. Continuing for now.");
			}

			Object valueA = recordA.getValue(srcColumn);
			Object valueB = recordB == null ? null : recordB.getValue(destColumn);
			if (isNull(valueA)) {
				valueA = "";
			}
			if (isNull(valueB)) {
				valueB = "";
			}
			if (!isEqual(valueA, valueB, destColumnType)) {
				hasAnyChange = true;
				switch (destColumnType) {
				case "boolean":
					newRecord.addNewValue(destColumn, Boolean.valueOf(valueA.toString()));
					break;
				case "integer":
					try {
						newRecord.addNewValue(destColumn, Integer.valueOf(valueA.toString()));
					} catch (NumberFormatException e) {
					}
					break;
				case "text":
					newRecord.addNewValue(destColumn, valueA.toString());
					break;
				}
			}

		}
		return hasAnyChange;
	}

	private boolean isEqual(Object a, Object b, String type) {
		if (a == null && b == null) {
			return true;
		}
		if (isNull(a) && isNull(b)) {
			return true;
		}
		String astr = a.toString().trim();
		String bstr = b.toString().trim();
		if (astr.equals(bstr)) {
			return true;
		}
		switch (type) {
		case "text":
			if (astr.equals(bstr)) {
				return true;
			}
			break;
		case "boolean":
			if ("true".equalsIgnoreCase(astr) && "true".equalsIgnoreCase(bstr)) {
				return true;
			}
			if ("true".equalsIgnoreCase(astr) && "1".equalsIgnoreCase(bstr)) {
				return true;
			}
			if ("1".equalsIgnoreCase(astr) && "1".equalsIgnoreCase(bstr)) {
				return true;
			}
			if ("false".equalsIgnoreCase(astr) && "false".equalsIgnoreCase(bstr)) {
				return true;
			}
			if ("false".equalsIgnoreCase(astr) && "0".equalsIgnoreCase(bstr)) {
				return true;
			}
			if ("0".equalsIgnoreCase(astr) && "false".equalsIgnoreCase(bstr)) {
				return true;
			}
			if ("1".equals(astr) && "1".equals(bstr)) {
				return true;
			}
			if ("0".equals(astr) && "0".equals(bstr)) {
				return true;
			}

		case "integer":
			if (astr.equals(bstr)) {
				return true;
			}
			break;
		}
		return false;
	}

	private boolean isNull(Object value) {
		return value == null || value.toString().trim().isEmpty() || value.toString().equalsIgnoreCase("null");
	}
}
