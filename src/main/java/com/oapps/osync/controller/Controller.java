package com.oapps.osync.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.oapps.osync.fields.Fields;
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
	 * Fetch the records from services.
	 * @param startPage - Number of the page
	 * @param totalRecords - Total records per page
	 * @param startTime - Start time of the changes. If -1, then its a first time sync
	 * @return
	 */
	public RecordSet fetchRecords(int startPage, int totalRecords, Long startTime);

	/**
	 * Matching records based on the Unique Identifier. It can be a ID
	 * @param recordsToFetchRemote
	 * @return
	 */
	public RecordSet getMatchedRecordsById(List<String> recordsToFetchRemote);

	/**
	 * List of values of unique columns. Return the record set
	 * @param values
	 * @return
	 */
	public RecordSet getMatchedRecordsByUniqueColumn(Collection<String> values);
}
