package com.oapps.osync.controller;

import java.util.HashMap;
import java.util.List;

import com.oapps.osync.fields.Fields;
import com.oapps.osync.fields.Record;
import com.oapps.osync.fields.RecordSet;

public interface Controller {

	/**
	 * List of fields
	 * 
	 * @param accountId
	 * @return
	 */
	public Fields getFields();

	/**
	 * Get the lastest records post the syncup
	 * 
	 * @param accountId
	 * @param lastSyncTime
	 * @return
	 */
	public RecordSet fetchUpdatedRecords(Long osyncId, Long lastSyncTime);

	/**
	 * 
	 * Unique column in the service. For example it can be an email address. If this
	 * is returned this will be used for searching the value
	 * 
	 * @param destination
	 * @return
	 */
	public String getUniqueColumnName(String destination, String syncController);

	/**
	 * Name of the primary key column which cannot be updated by a user
	 * 
	 * @param destination
	 * @return
	 */
	public String getPrimaryKey(String destination);

	/**
	 * Create the new records and return the unique values
	 * 
	 * @param leftRecordsToCreate
	 * @param syncFrom
	 * @return
	 */
	public HashMap<String, String> createNewRecords(RecordSet leftRecordsToCreate, String syncFrom);

	/**
	 * Update record
	 * 
	 * @param leftRecordsToUpdate
	 * @param syncFrom
	 */
	public void updateRecords(RecordSet leftRecordsToUpdate, String syncFrom);

	/**
	 * Fetch a single record using the unique column
	 * 
	 * @param value
	 */
	public Record getMatchedRecord(String value);

	public RecordSet fetchRecords(int startPage, int totalRecords, Long startTime, Long endTime);

	public RecordSet getMatchedRecordsByUniqueId(List<String> recordsToFetchRemote);
}
