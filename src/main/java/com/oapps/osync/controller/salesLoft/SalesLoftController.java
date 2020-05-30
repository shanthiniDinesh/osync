package com.oapps.osync.controller.salesLoft;

import java.io.IOException;
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
public class SalesLoftController implements Controller {

	String moduleName = null;

	public static final String ACCOUNT_MODULE = "Accounts";
	public static final String PEOPLE_MODULE = "People";

	public static final int firstElement_Array = 1;
	
	public String[] ignorePeopleFields= {};
	public String[] ignoreAccountFields= {"country"};
	

	public SalesLoftController(String moduleName) {
		this.moduleName = moduleName;
	}

	public SalesLoftController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fields getFields() {

		if ((ACCOUNT_MODULE).equals(moduleName)) {
			try {
				String response = HttpUtil.get("https://api.salesloft.com/v2/accounts.json", getAuthMap());
				JSONObject jsonObject = new JSONObject(response);
				JSONArray values = jsonObject.getJSONArray("data");
				JSONObject accountJSON = values.getJSONObject(firstElement_Array);
				Fields field = SalesLoftController.getModuleFields(accountJSON,ignoreAccountFields);
				return field;
			} catch (IOException e) {

			}
		}

		if ((PEOPLE_MODULE).equals(moduleName)) {

			try {
				String response = HttpUtil.get("https://api.salesloft.com/v2/people.json", getAuthMap());
				JSONObject jsonObject = new JSONObject(response);
				JSONArray values = jsonObject.getJSONArray("data");
				JSONObject peopleJSON = values.getJSONObject(firstElement_Array);
				Fields field = SalesLoftController.getModuleFields(peopleJSON,ignorePeopleFields);
				return field;
			} catch (IOException e) {

			}
		}
		return null;

	}

	private static Fields getModuleFields(JSONObject json,String ignoreFields[]) {
		Fields field = Fields.of();
		Iterator<String> iterator = json.keys();
		if (iterator != null) {
			while (iterator.hasNext()) {
				String key = iterator.next();
				Object value = json.get(key);
				System.out.println("key value Pair==="+key+"==="+value);

				if (value instanceof JSONObject) {
					JSONObject innerJson = new JSONObject(value.toString());
					Iterator<String> innerIterator = innerJson.keys();
					if (innerIterator != null) {
						while (innerIterator.hasNext()) {
							String innerKey = innerIterator.next();
							Object InnerValue = innerJson.get(innerKey);
							System.out.println("key value Pair==="+innerKey+"==="+InnerValue);

							String dataType = InnerValue.getClass().getSimpleName();
							if (dataType.equalsIgnoreCase("Integer")) {
								field.number(innerKey, innerKey);

							} else if (dataType.equalsIgnoreCase("Long")) {
								field.number(innerKey, innerKey);

							} else if (dataType.equalsIgnoreCase("Float")) {

							} else if (dataType.equalsIgnoreCase("Double")) {

							} else if (dataType.equalsIgnoreCase("Boolean")) {
								field.bool(innerKey, innerKey);

							} else if (dataType.equalsIgnoreCase("String") || (InnerValue.equals(null))) {
								field.text(innerKey, innerKey);
							}
							
						}
					}
				} else {
					String dataType = value.getClass().getSimpleName();
					if (dataType.equalsIgnoreCase("Integer")) {
					} else if (dataType.equalsIgnoreCase("Long")) {
						field.number(key, key);
					} else if (dataType.equalsIgnoreCase("Float")) {
					} else if (dataType.equalsIgnoreCase("Double")) {
					} else if (dataType.equalsIgnoreCase("Boolean")) {
						field.bool(key, key);
					} else if (dataType.equalsIgnoreCase("String") || (value.equals(null))) {
						field.text(key, key);
					}
				}
				
			}
		}
		
		return field;

	}
	
	private HashMap<String, String> getAuthMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Authorization",
				"bearer v2_oa_102049_cb2a3bca54d8fe9d4fe4d79b21519a4e99b883d991123bc507d964c73505b68d");
		map.put("Content-Type", "application/json");
		return map;
	}

	public RecordSet fetchUpdatedRecords(Long osyncId, Long lastSyncTime) {
		if ((ACCOUNT_MODULE).equals(moduleName)) {
			
		try {
			//lastSyncTime= Long.parseLong("2020-01-01T00:00:00.000000-05:00");
			System.out.println("SalesLoft fetch updated records");
			String response = HttpUtil.get("https://api.salesloft.com/v2/accounts.json?updated_at[gt]=2020-05-06T02:20:53.665505-04:00", getAuthMap());
			JSONObject json = new JSONObject(response);
			System.out.println("Response ==>"+response);
			JSONArray accountArrays= json.getJSONArray("data");
		
			RecordSet recordSet = RecordSet.init("salesloft-account", "id");
			for (int i = 0; i < accountArrays.length(); i++) {
				JSONObject accountObj = accountArrays.optJSONObject(i);
				Record record = recordSet.add(accountObj.optString("id"));
				Iterator<String> iterator = accountObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					record.addNewValue(key, accountObj.get(key));
				}
			}
			return recordSet;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NumberFormatException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		if ((PEOPLE_MODULE).equals(moduleName)) {
			try {
				//lastSyncTime= Long.parseLong("2020-01-01T00:00:00.000000-05:00");
				System.out.println("fetch updated records");
				String response = HttpUtil.get("https://api.salesloft.com/v2/people.json?updated_at[gt]=2020-05-06T02:20:53.665505-04:00", getAuthMap());
				System.out.println("Response fetch salesforce People==>"+response);
				JSONObject json = new JSONObject(response);
				JSONArray accountArrays= json.getJSONArray("data");
			
				RecordSet recordSet = RecordSet.init("salesloft-people", "id");
				for (int i = 0; i < accountArrays.length(); i++) {
					JSONObject accountObj = accountArrays.optJSONObject(i);
					Record record = recordSet.add(accountObj.optString("id"));
					Iterator<String> iterator = accountObj.keys();
					while (iterator.hasNext()) {
						String key = iterator.next();
						record.addNewValue(key, accountObj.get(key));
					}
				}
				return recordSet;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NumberFormatException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		

		return null;
	}

	@Override
	public HashMap<String, String> createNewRecords(RecordSet recordSet, String syncFrom) {

		HashMap<String, String> uvMap = new HashMap<String, String>();
		for (Record record : recordSet) {
			try {
				String response="";
				if ((ACCOUNT_MODULE).equals(moduleName)) {
					JSONObject accountJson = record.columnValuesAsJson();
					if (accountJson != null) {
						log.info("creating this: " + accountJson.toString());	
						 response = HttpUtil.post("https://api.salesloft.com/v2/accounts.json", accountJson.toString(), getAuthMap());
					System.out.println("Response==>."+response);
					} else {
						log.severe("Error while creating contact : " + response);
					}
				}
				else if ((PEOPLE_MODULE).equals(moduleName)) {
					JSONObject peopleJson = record.columnValuesAsJson();
					 
					if (peopleJson != null) {
						log.info("creating this: " + peopleJson.toString());	
						 response = HttpUtil.post("https://api.salesloft.com/v2/people.json", peopleJson.toString(), getAuthMap());
							System.out.println("Response==>."+response);
								
					} else {
						log.severe("Error while creating contact : " + response);
					}

				}

			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating new records to salesLoft - " + syncFrom, e);
				log.info("Continuing the next record..");
			}


		}

		return uvMap;

	}

	@Override
	public void updateRecords(RecordSet recordSet, String syncFrom) {
		for (Record record : recordSet) {
			try {
				String response="";
				if ((ACCOUNT_MODULE).equals(moduleName)) {
					JSONObject accountJson = record.columnValuesAsJson();
					if (accountJson != null) {
						log.info("creating this: " + accountJson.toString());	
						 response = HttpUtil.put("https://api.salesloft.com/v2/accounts/13345456.json", accountJson.toString(), getAuthMap());
					System.out.println("Response==>."+response);
					} else {
						log.severe("Error while updating Account : " + response);
					}
				}
				else if ((PEOPLE_MODULE).equals(moduleName)) {
					JSONObject peopleJson = record.columnValuesAsJson();
					 
					if (peopleJson != null) {
						log.info("creating this: " + peopleJson.toString());	
						 response = HttpUtil.put("https://api.salesloft.com/v2/people/24783857.json", peopleJson.toString(), getAuthMap());
							System.out.println("Response==>."+response);
								
					} else {
						log.severe("Error while updating People : " + response);
					}

				}

			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while updating new records to salesLoft - " + syncFrom, e);
				
			}


		}

	}


	public static void main(String[] args) {
		SalesLoftController salesLoftController = new SalesLoftController("Accounts");
	   Fields fields = salesLoftController.getFields();
		System.out.println("===========SalesLoft Accounts Module=======");
		System.out.println("SalesLoft Schemma Info=="+fields);
    	System.out.println("----------------------------------------------");
    	System.out.println("----------------------------------------------");
	    RecordSet set=	salesLoftController.fetchUpdatedRecords(null,null);
		System.out.println("----------------------------------------------");
		System.out.println("SalesLoft New Record  creation info");
         salesLoftController.createNewRecords(set, "Salesloft");
       	System.out.println("SalesLoft Update Record   info");
         salesLoftController.updateRecords(set, "Salesloft");
          
      	System.out.println("----------------------------------------------");
    	System.out.println("----------------------------------------------");
	    SalesLoftController salesLoftControllerPeople = new SalesLoftController("People");
   	   Fields fieldspeople = salesLoftControllerPeople.getFields();
   		System.out.println("===========SalesLoft People Module=======");
   		System.out.println("SalesLoft Schemma Info=="+fieldspeople);
       	System.out.println("----------------------------------------------");
       	System.out.println("----------------------------------------------");
   	    RecordSet setPeople=	salesLoftControllerPeople.fetchUpdatedRecords(null,null);
   		System.out.println("----------------------------------------------");
   		System.out.println("SalesLoft New Record  creation info");
   		salesLoftControllerPeople.createNewRecords(setPeople, "Salesloft");
   	 	System.out.println("SalesLoft Update Record   info");
      	salesLoftControllerPeople.updateRecords(setPeople, "Salesloft");
   	 
   	
   		
	 
	
		
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

	public RecordSet fetchRecords(int startPage, int totalRecords, Long startTime, Long endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	public RecordSet getMatchedRecordsByUniqueId(List<String> recordsToFetchRemote) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet fetchRecords(int startPage, int totalRecords, Long startTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet getMatchedRecordsById(List<String> recordsToFetchRemote) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSet getMatchedRecordsByUniqueColumn(Collection<String> values) {
		// TODO Auto-generated method stub
		return null;
	}



}
