package com.oapps.osync.controller.zoho.crm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.oapps.osync.controller.Controller;
import com.oapps.osync.fields.Fields;
import com.oapps.osync.fields.Record;
import com.oapps.osync.fields.RecordSet;
import com.zoho.crm.library.api.response.BulkAPIResponse;
import com.zoho.crm.library.api.response.BulkAPIResponse.EntityResponse;
import com.zoho.crm.library.crud.ZCRMField;
import com.zoho.crm.library.crud.ZCRMModule;
import com.zoho.crm.library.crud.ZCRMRecord;
import com.zoho.crm.library.setup.restclient.ZCRMRestClient;

import lombok.extern.java.Log;

@Log
public class ZohoCRMController implements Controller {

	public String moduleName = null;

	public ZohoCRMController(String moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	public Fields getFields() {
		try {
			setAuth();

//			ZohoOAuthClient cli = ZohoOAuthClient.getInstance();
			// String grantToken =
			// "1000.6391fd10306831163e4444f9f68a68d0.e69e3561d854f118bd3c29059aee5ef1";
			// ZohoOAuthTokens tokens = cli.generateAccessToken(grantToken);
			// String accessToken = tokens.getAccessToken();
			// String refreshToken = tokens.getRefreshToken();
			// System.out.println("access token = " + accessToken + " & refresh token = " +
			// refreshToken);

			ZCRMModule module = ZCRMModule.getInstance("Contacts"); // module apiname
			BulkAPIResponse response = module.getAllFields();
			@SuppressWarnings("unchecked")
			List<ZCRMField> zcrmFields = (List<ZCRMField>) response.getData();
			Fields field = Fields.of();
			for (ZCRMField zcrmField : zcrmFields) {
				String id = zcrmField.getApiName().toString();
				String displayName = zcrmField.getDisplayName();
				switch (zcrmField.getDataType()) {
				case "text":
				case "email":
				case "phone":
				case "textarea":
					field.text(id, displayName);
					break;
				case "boolean":
					field.bool(id, displayName);
					break;
				case "integer":
					field.number(id, displayName);
					break;
				}
				System.out.println(zcrmField.getDataType() + "::::" + id + "::::" + displayName);
			}
			return field;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // for initializing
		return null;
	}

	private void setAuth() throws Exception {
		HashMap<String, String> zcrmConfigurations = new HashMap<String, String>();
		zcrmConfigurations.put("minLogLevel", "ALL");
		zcrmConfigurations.put("currentUserEmail", "vijay@oapps.xyz");
		zcrmConfigurations.put("client_id", "1000.U11DKAIO4UL4CRE9025QNHP0WNDJUH");
		zcrmConfigurations.put("client_secret", "b993ae2145535c9e9377b71b09db70746148b47143");
		zcrmConfigurations.put("redirect_uri", "https://crmsandbox.zoho.com/");
		zcrmConfigurations.put("persistence_handler_class", "com.zoho.oauth.clientapp.ZohoOAuthFilePersistence");
		zcrmConfigurations.put("oauth_tokens_file_path", "/Users/kirubadevi/extensions/oauth/oauthtokens.properties");// optional
		zcrmConfigurations.put("domainSuffix", "com");// optional. Default is com. "cn" and "eu" supported
		zcrmConfigurations.put("accessType", "sandbox");// Production->www(default), Development->developer,
														// Sandbox->sandbox(optional)
		zcrmConfigurations.put("access_type", "offline");// optional
		zcrmConfigurations.put("apiBaseUrl", "https://sandbox.zohoapis.com");// optional
		zcrmConfigurations.put("iamURL", "https://accounts.zoho.com");// optional

		ZCRMRestClient.initialize(zcrmConfigurations);
	}

	@Override
	public RecordSet fetchUpdatedRecords(Long osyncId, Long lastSyncTime) {
		try {
			setAuth();
			ZCRMModule module = ZCRMModule.getInstance("Contacts");
			BulkAPIResponse response = module.getRecords();
			@SuppressWarnings("unchecked")
			List<ZCRMRecord> records = (List<ZCRMRecord>) response.getData();
			RecordSet recordSet = RecordSet.init("zohocrm-contacts", "id");
			for (ZCRMRecord zcrmRecord : records) {
				Record record = recordSet.add(zcrmRecord.getEntityId().toString());
				for (Entry<String, Object> entry : zcrmRecord.getData().entrySet()) {
					Object value = entry.getValue();
					if (!isNull(value)) {
						record.addNewValue(entry.getKey(), value);
					} else {
						record.addNewValue(entry.getKey(), null);
					}
				}
			}
			return recordSet;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HashMap<String, String> createNewRecords(RecordSet recordSet, String syncFrom) {
		try {
			setAuth();
			String propName = "sync-from-oapps-" + syncFrom;
			HashMap<String, String> uvValuesMap = new HashMap<String, String>();
			ZCRMModule module = ZCRMModule.getInstance("Contacts");
			List<ZCRMRecord> newRecords = new ArrayList<ZCRMRecord>();
			Collection<Record> allRecords = recordSet.getAllRecords();
			for (Record record : allRecords) {
				ZCRMRecord zcRecord = new ZCRMRecord("Contacts");
				zcRecord.setData(record.getColumnValues());
				zcRecord.setProperty(propName, record.getMappedRecordUniqueValue());
				newRecords.add(zcRecord);
			}
			BulkAPIResponse response = module.createRecords(newRecords);
			ArrayList<EntityResponse> entityResponses = response.getEntityResponses();
			int i = 0;
			for (EntityResponse entityResponse : entityResponses) {
				if (entityResponse.getStatus().equalsIgnoreCase("success")) {
					ZCRMRecord record = (ZCRMRecord) entityResponse.getData();
					String mappedUnique = newRecords.get(i).getProperty(propName).toString();
					uvValuesMap.put(record.getEntityId().toString(), mappedUnique);
				}
				i++;
			}
			return uvValuesMap;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error on creating new records, " + syncFrom, e);
		}
		return null;

	}

	private static boolean isNull(Object value) {
		return value == null || value.toString().trim().isEmpty() || value.toString().equalsIgnoreCase("null");
	}

	@Override
	public void updateRecords(RecordSet recordSet, String syncFrom) {
		try {
			setAuth();
			ZCRMModule module = ZCRMModule.getInstance("Contacts");
			List<ZCRMRecord> updateRecords = new ArrayList<ZCRMRecord>();
			Collection<Record> allRecords = recordSet.getAllRecords();
			for (Record record : allRecords) {
				ZCRMRecord zcRecord = new ZCRMRecord("Contacts");
				zcRecord.setEntityId(Long.parseLong(record.getUniqueValue()));
				zcRecord.setData(record.getColumnValues());
				updateRecords.add(zcRecord);
			}
			BulkAPIResponse response = module.updateRecords(updateRecords, null);
			ArrayList<EntityResponse> entityResponses = response.getEntityResponses();
			log.info("Update success :" + entityResponses);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error on creating new records, " + syncFrom, e);
		}
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
		try {
			setAuth();
			ZCRMModule module = ZCRMModule.getInstance("Contacts");
			BulkAPIResponse response = module.searchByEmail(value, 1, 1);
			if (response.getData().size() > 0) {
				ZCRMRecord zcRecord = (ZCRMRecord) response.getData().get(0);
				Record record = new Record();
				record.setColumnValues(zcRecord.getData());
				record.setUniqueValue(zcRecord.getEntityId().toString());
				return record;
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error on fetchingMatched record, " + value, e);
		}
		return null;
	}

}
