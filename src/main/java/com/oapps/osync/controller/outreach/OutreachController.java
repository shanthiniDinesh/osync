package com.oapps.osync.controller.outreach;

import java.io.Console;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;

import com.oapps.osync.controller.Controller;
import com.oapps.osync.controller.salesLoft.SalesLoftController;
import com.oapps.osync.fields.Fields;
import com.oapps.osync.fields.Record;
import com.oapps.osync.fields.RecordSet;
import com.oapps.osync.util.HttpUtil;

import lombok.extern.java.Log;

@Log
public class OutreachController implements Controller {

	String moduleName = null;

	public static final String ACCOUNT_MODULE = "Accounts";
	public static final String PROSPECT_MODULE = "Prospect";

	public static final int firstElement_Array = 1;

	public OutreachController(String moduleName) {
		this.moduleName = moduleName;
	}

	public OutreachController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fields getFields() {

		if ((ACCOUNT_MODULE).equals(moduleName)) {
			try {
				String response = HttpUtil.get("https://api.outreach.io/api/v2/accounts", getAuthMap());
				JSONObject jsonObject = new JSONObject(response);
				JSONArray values = jsonObject.getJSONArray("data");
				JSONObject accountJSON = values.getJSONObject(firstElement_Array);
				Fields field = OutreachController.getModuleFields(accountJSON);
				return field;
			} catch (IOException e) {

			}
		}

		if ((PROSPECT_MODULE).equals(moduleName)) {

			try {
				String response = HttpUtil.get("https://api.outreach.io/api/v2/prospects", getAuthMap());
				JSONObject jsonObject = new JSONObject(response);
				JSONArray values = jsonObject.getJSONArray("data");
				JSONObject peopleJSON = values.getJSONObject(firstElement_Array);
				Fields field = OutreachController.getModuleFields(peopleJSON);
				return field;
			} catch (IOException e) {

			}
		}
		return null;

	}

	private static Fields getModuleFields(JSONObject json) {
		Fields field = Fields.of();
		Iterator<String> iterator = json.keys();
		if (iterator != null) {
			while (iterator.hasNext()) {
				String key = iterator.next();
				Object value = json.get(key);
				if (value instanceof JSONObject) {
					JSONObject innerJson = new JSONObject(value.toString());
					Iterator<String> innerIterator = innerJson.keys();
					if (innerIterator != null) {
						while (innerIterator.hasNext()) {
							String innerKey = innerIterator.next();
							Object InnerValue = innerJson.get(innerKey);

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
				"bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrYXZpdGhhQG9hcHBzLnh5eiIsImlhdCI6MTU5MDU3MzE1OCwiZXhwIjoxNTkwNTgwMzU4LCJiZW50byI6ImFwcDFhIiwib3JnX3VzZXJfaWQiOjEsImF1ZCI6Ik9BcHBTIiwic2NvcGVzIjoiQUpBQUVBPT0ifQ.OHokXDNvyr_b8k_B1lhc8qSR0EKxsAyq_7rgGB1bVWg");
		map.put("Content-Type", "application/json");
		return map;
	}

	public RecordSet fetchUpdatedRecords(Long osyncId, Long lastSyncTime) {
		if ((ACCOUNT_MODULE).equals(moduleName)) {
			
		try {
			//lastSyncTime= Long.parseLong("2020-01-01T00:00:00.000000-05:00");
			System.out.println("fetch updated records");
			//String response = HttpUtil.get("https://api.outreach.io/api/v2/accounts?filter[updatedAt]=2020-05-01T09:29:31.000Z", getAuthMap());
			String response = HttpUtil.get("https://api.outreach.io/api/v2/accounts?filter[id]=50", getAuthMap());
		
			System.out.println("Response ==>"+response);
				JSONObject json = new JSONObject(response);
			JSONArray accountArrays= json.getJSONArray("data");
		   
			RecordSet recordSet = RecordSet.init("salesloft-account", "id");
			for (int i = 0; i < accountArrays.length(); i++) {
				JSONObject masterObj = accountArrays.optJSONObject(i);
				JSONObject accountObj = masterObj.getJSONObject("attributes");
				Record record = recordSet.add(accountObj.optString("attributes"));
				Iterator<String> iterator = accountObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					System.out.println("key==>"+key);
					record.addNewValue(key, accountObj.get(key));
					System.out.println("value==>"+ accountObj.get(key));
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
		
		if ((PROSPECT_MODULE).equals(moduleName)) {
			try {
				//lastSyncTime= Long.parseLong("2020-01-01T00:00:00.000000-05:00");
				System.out.println("fetch updated records");
				String response = HttpUtil.get("https://api.outreach.io/api/v2/prospects?filter[updatedAt]=2020-05-01T07:41:53.000Z", getAuthMap());
				System.out.println("Response ==>"+response);
				JSONObject json = new JSONObject(response);
				JSONArray accountArrays= json.getJSONArray("data");
			
				RecordSet recordSet = RecordSet.init("salesloft-people", "id");
				for (int i = 0; i < accountArrays.length(); i++) {
					JSONObject masterObj = accountArrays.optJSONObject(i);
					JSONObject accountObj = masterObj.getJSONObject("attributes");
					Record record = recordSet.add(accountObj.optString("attributes"));
					System.out.println("accountObj==>>"+accountObj);
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
		System.out.println("moduleName=="+moduleName);
		for (Record record : recordSet) {
			try {
				String response="";
				if ((ACCOUNT_MODULE).equals(moduleName)) {
					JSONObject accountJson = record.columnValuesAsJson();
					System.out.println("Json=="+accountJson.toString());
					
					 JSONObject obj = new JSONObject();
					 JSONObject ob1= new JSONObject();
					 obj.put("data", "type:account");
					 obj.put("attributes", accountJson);
					 System.out.println("account object=="+ obj.toString());
					 
					if (accountJson != null) {
						log.info("creating this: " + accountJson.toString());	
						 response = HttpUtil.post("https://api.outreach.io/api/v2/accounts", obj.toString(), getAuthMap());
							System.out.println("Response==>."+response);
								
					} else {
						log.severe("Error while creating contact : " + response);
					}
				}
				else if ((PROSPECT_MODULE).equals(moduleName)) {
					JSONObject peopleJson = record.columnValuesAsJson();
					 
					if (peopleJson != null) {
						log.info("creating this: " + peopleJson.toString());	
						 JSONObject obj = new JSONObject();
						 JSONObject ob1= new JSONObject();
						 ob1.put("type", "prospect");
						 obj.put("data",ob1 );
						 obj.put("attributes", peopleJson.toString());
						 System.out.println("object=="+ obj.toString());
						
						 response = HttpUtil.post("https://api.outreach.io/api/v2/prospects", obj.toString(), getAuthMap());
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
		    OutreachController outreachController = new OutreachController("Accounts");
			    Fields fields = outreachController.getFields();
				System.out.println("===========Outreach  Accounts Module=======");
				System.out.println("outReach Schemma Info=="+fields);
		    	System.out.println("----------------------------------------------");
		    	System.out.println("----------------------------------------------");
			   RecordSet set=	outreachController.fetchUpdatedRecords(null,null);
			   System.out.println("----------------------------------------------");
				 System.out.println("SalesLoft New Record  creation info");
		         outreachController.createNewRecords(set, "Salesloft");
		       	//System.out.println("SalesLoft Update Record   info");
		         //salesLoftController.updateRecords(set, "Salesloft");
		          
		      	//System.out.println("----------------------------------------------");
		    	//System.out.println("----------------------------------------------");
		    	 OutreachController outreachController1 = new OutreachController("Prospect");
		   	   Fields fieldspeople = outreachController1.getFields();
		   		System.out.println("===========Outreach  Prospect Module=======");
		   		System.out.println("OutReach Schemma Info=="+fieldspeople);
		       	System.out.println("----------------------------------------------");
		       	System.out.println("----------------------------------------------");
		   	   RecordSet setPeople=	outreachController1.fetchUpdatedRecords(null,null);
		   		System.out.println("----------------------------------------------");
		   	 System.out.println("SalesLoft New Record  creation info");
		   	outreachController1.createNewRecords(setPeople, "Salesloft");
		   	 	//System.out.println("SalesLoft Update Record   info");
		      	//salesLoftControllerPeople.updateRecords(setPeople, "Salesloft");
		   	 
		   	
		   		
			 
			
	
		
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
