package com.oapps.osync;

import java.util.HashMap;

import com.oapps.osync.fields.Fields;
import com.oapps.osync.fields.RecordSet;

public interface Controller {

	public Fields getFields(String accountId);

	public RecordSet fetchUpdatedRecords(String accountId, Long lastSyncTime);

	/**
	 * Method to return the unique colname that is going to be persisted in the
	 * source record. For ex: If there is a sync between Zoho & Outreach, Zoho will
	 * hold the unique ID of Outreach. The below method should return the custom
	 * field name.
	 * 
	 * @param destination
	 * @return
	 */
	public String getUniqueColName(String destination);

	public HashMap<String, String> createNewRecords(RecordSet leftRecordsToCreate, String syncFrom);

	public void updateRecords(RecordSet leftRecordsToUpdate, String syncFrom);
}
