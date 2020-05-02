package com.oapps.osync.controller.freshworks.freshsales;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
							System.out.println(type + "::::" + id + "::::" + displayName);
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
	public RecordSet fetchUpdatedRecords(Long osyncId, Long lastSyncTime) {
		try {
			String response = HttpUtil.get("https://oapps.freshsales.io/api/contacts/view/13000503716", getAuthMap());
			JSONObject json = new JSONObject(response);
			JSONArray contactsArray = json.optJSONArray("contacts");
			RecordSet recordSet = RecordSet.init("freshsales-contacts", "id");
			for (int i = 0; i < contactsArray.length(); i++) {
				JSONObject contactObj = contactsArray.optJSONObject(i);
				Record record = recordSet.add(contactObj.optString("id"));
				Iterator<String> iterator = contactObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					record.addNewValue(key, contactObj.get(key));
				}
			}
			return recordSet;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HashMap<String, String> createNewRecords(RecordSet recordSet, String syncFrom) {

		HashMap<String, String> uvMap = new HashMap<String, String>();
		for (Record record : recordSet) {
			try {
				JSONObject json = record.columnValuesAsJson();
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
	public Record getMatchedRecord(String value) {
		String searchString = "{ \"filter_rule\"\n"
				+ "			 : [{\"attribute\" : \"contact_email.email\", \"operator\":\"is_in\",\n"
				+ "			 \"value\":\"" + value.trim() + "\"}] }";

		try {
			String response = HttpUtil.post("https://oapps.freshsales.io/api/filtered_search/contact", searchString,
					getAuthMap());
			JSONObject json = new JSONObject(response);
			JSONArray contactsArray = json.optJSONArray("contacts");
			if (contactsArray.length() > 0) {
				JSONObject contactObj = contactsArray.optJSONObject(0);
				Record record = new Record();
				record.setUniqueValue(contactObj.optString("id"));
				Iterator<String> iterator = contactObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					record.addNewValue(key, contactObj.get(key));
				}
				return record;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
