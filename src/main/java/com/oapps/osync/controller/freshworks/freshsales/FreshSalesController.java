package com.oapps.osync.controller.freshworks.freshsales;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;

import com.oapps.osync.controller.Controller;
import com.oapps.osync.fields.Fields;
import com.oapps.osync.fields.Record;
import com.oapps.osync.fields.RecordSet;
import com.oapps.osync.util.HttpUtil;

import lombok.extern.java.Log;

@Log
public class FreshSalesController implements Controller {

	String moduleName = null;

	public FreshSalesController(String moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	public Fields getFields() {
		try {
			String response = HttpUtil.get("https://oapps.freshsales.io/api/settings/contacts/fields", getAuthMap());
			JSONObject json = new JSONObject(response);
			JSONArray array = json.optJSONArray("fields");
			if (array != null) {
				Fields field = Fields.of();
				for (int i = 0; i < array.length(); i++) {
					JSONObject fieldObj = array.optJSONObject(i);
					if (fieldObj != null) {
						String id = fieldObj.optString("name");
						String displayName = fieldObj.optString("label");
						String type = fieldObj.optString("type");
						boolean isDefault = fieldObj.optBoolean("default", true);
						if (!isDefault) {
							id = "custom_field." + id;
						}
						switch (type) {
						case "text":
							field.text(id, displayName);
							break;
						case "checkbox":
							field.bool(id, displayName);
							break;
						case "number":
							field.number(id, displayName);
							break;
						default:
							System.out.println("unsupported-type: " + type + ":" + id + ":" + displayName);
						}
					}
				}
				return field;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private HashMap<String, String> getAuthMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Authorization", "Token token=0s5ouaBOyvST0y-b1MBsDA");
		map.put("Content-Type", "application/json");
		return map;
	}

	@Override
	public HashMap<String, String> createNewRecords(RecordSet recordSet, String syncFrom) {

		HashMap<String, String> uvMap = new HashMap<String, String>();
		for (Record record : recordSet) {
			try {
				JSONObject json = record.columnValuesAsJson();
				Iterator<String> keys = json.keys();
				JSONObject cfJson = new JSONObject();
				List<String> keysToRemove = new ArrayList<String>();
				while (keys.hasNext()) {
					String key = keys.next();
					if (key.startsWith("custom_field")) {
						cfJson.put(key.replaceFirst("custom_field.", ""), json.get(key));
						//json.remove(key);
						keysToRemove.add(key);
					}
				}
				for (String key : keysToRemove) {
					json.remove(key);
				}
				json.put("custom_field", cfJson);

				JSONObject contactJson = new JSONObject();
				contactJson.put("contact", json);
				log.info("posting this: " + json.toString());
				String response = HttpUtil.post("https://oapps.freshsales.io/api/contacts", contactJson.toString(),
						getAuthMap());
				JSONObject responseJson = new JSONObject(response);
				// TODO: contact may not be present
				JSONObject contactResJson = responseJson.optJSONObject("contact");
				if (contactResJson != null) {
					Long responseId = contactResJson.optLong("id");
					uvMap.put(responseId.toString(), record.getMappedRecordUniqueValue());
				} else {
					log.severe("Error while creating contact : " + response);
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating new records to Freshsales - " + syncFrom, e);
				log.info("Continuing the next record..");
			}
		}

		return uvMap;

	}

	@Override
	public void updateRecords(RecordSet recordSet, String syncFrom) {
		for (Record record : recordSet) {
			try {
				JSONObject json = record.columnValuesAsJson();
				Iterator<String> keys = json.keys();
				JSONObject cfJson = new JSONObject();
				
				List<String> keysToRemove = new ArrayList<String>();
				while (keys.hasNext()) {
					String key = keys.next();
					if (key.startsWith("custom_field")) {
						cfJson.put(key.replaceFirst("custom_field.", ""), json.get(key));
						//json.remove(key);
						keysToRemove.add(key);
					}
				}
				for (String key : keysToRemove) {
					json.remove(key);
				}
				
				json.put("custom_field", cfJson);

				JSONObject contactJson = new JSONObject();
				contactJson.put("contact", json);

				log.info("updating this: " + json.toString());
				String response = HttpUtil.put("https://oapps.freshsales.io/api/contacts/" + record.getUniqueValue(),
						contactJson.toString(), getAuthMap());
				log.info("Sync response " + response);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating new records to Freshsales - " + syncFrom, e);
				log.info("Continuing the next record..");
			}
		}

	}

	public static void main(String[] args) {
		// FreshSalesController freshSalesController = new FreshSalesController();
		// Fields fields = freshSalesController.getFields(null);
//		System.out.println(fields);

//		RecordSet recordSet = freshSalesController.fetchUpdatedRecords(null, null);
//		for (Record record : recordSet) {
//			System.out.println(record.getUniqueValue() + "::::" + record);
//		}
	}

	@Override
	public String getUniqueColumnName(String destination, String syncController) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrimaryKey(String destination) {
		return "id";
	}

	@Override
	public RecordSet fetchRecords(int startPage, int totalRecords, Long startTime) {
		try {
			String response = HttpUtil.get("https://oapps.freshsales.io/api/contacts/view/13000503716?per_page="
					+ totalRecords + "&page=" + startPage, getAuthMap());
			JSONObject json = new JSONObject(response);
			JSONArray contactsArray = json.optJSONArray("contacts");
			RecordSet recordSet = RecordSet.init("freshsales-contacts", "id");
			for (int i = 0; i < contactsArray.length(); i++) {
				JSONObject contactObj = contactsArray.optJSONObject(i);
				Record record = recordSet.add(contactObj.optString("id"));
				Iterator<String> iterator = contactObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					if ("custom_field".equals(key)) {
						JSONObject customField = contactObj.optJSONObject(key);
						Iterator<String> cfKeys = customField.keys();
						while (cfKeys.hasNext()) {
							String cfKey = cfKeys.next();
							record.addNewValue(key + "." + cfKey, customField.get(cfKey));
						}
					} else {
						record.addNewValue(key, contactObj.get(key));
					}
				}
			}
			log.info("Records fetched from freshsales :" + recordSet.count() );
			return recordSet;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RecordSet getMatchedRecordsById(List<String> recordsToFetchRemote) {
		RecordSet recordSet = RecordSet.init("freshsales-contacts", "id");
		for (String uniqueId : recordsToFetchRemote) {
			try {
				String response = HttpUtil.get("https://oapps.freshsales.io/api/contacts/" + uniqueId.trim(),
						getAuthMap());
				JSONObject json = new JSONObject(response);
				JSONObject contactObj = json.optJSONObject("contact");
				Record record = recordSet.add(contactObj.optString("id"));
				Iterator<String> iterator = contactObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					if ("custom_field".equals(key)) {
						JSONObject customField = contactObj.optJSONObject(key);
						Iterator<String> cfKeys = customField.keys();
						while (cfKeys.hasNext()) {
							String cfKey = cfKeys.next();
							record.addNewValue(key + "." + cfKey, customField.get(cfKey));
						}
					} else {
						record.addNewValue(key, contactObj.get(key));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		log.info("Matched Records fetched from freshsales :" + recordSet.count() );
		return recordSet;
	}

	@Override
	public RecordSet getMatchedRecordsByUniqueColumn(Collection<String> values) {
		JSONObject searchJson = new JSONObject();
		JSONArray filterRule = new JSONArray();

		JSONObject filterObject = new JSONObject();
		filterObject.put("attribute", "contact_email.email");
		filterObject.put("operator", "is_in");
		filterObject.put("value", values);
		filterRule.put(filterObject);

		searchJson.put("filter_rule", filterRule);

		try {
			String response = HttpUtil.post("https://oapps.freshsales.io/api/filtered_search/contact",
					searchJson.toString(), getAuthMap());
			JSONObject json = new JSONObject(response);
			JSONArray contactsArray = json.optJSONArray("contacts");
			RecordSet rs = RecordSet.init("freshsales-contacts", "id");
			for (int i = 0; i < contactsArray.length(); i++) {
				JSONObject contactObj = contactsArray.optJSONObject(i);
				Record record = rs.add(contactObj.optString("id"));
				Iterator<String> iterator = contactObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					if ("custom_field".equals(key)) {
						JSONObject customField = contactObj.optJSONObject(key);
						Iterator<String> cfKeys = customField.keys();
						while (cfKeys.hasNext()) {
							String cfKey = cfKeys.next();
							record.addNewValue(key + "." + cfKey, customField.get(cfKey));
						}
					} else {
						record.addNewValue(key, contactObj.get(key));
					}
				}
			}
			log.info("Matched Records fetched from freshsales :" + rs.count() );
			return rs;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
