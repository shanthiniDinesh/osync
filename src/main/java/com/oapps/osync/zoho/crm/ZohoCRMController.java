package com.oapps.osync.zoho.crm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.oapps.osync.Controller;
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

	@Override
	public Fields getFields(String accountId) {
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

	public static void main(String[] args) {
		ZohoCRMController zcrm = new ZohoCRMController();
		Fields fields = zcrm.getFields(null);
		//System.out.println(fields.toString());

		// Fields fields = freshSalesController.getFields(null);
		// System.out.println(fields);

//		RecordSet recordSet = zcrm.fetchUpdatedRecords(null, null);
//		for (Record record : recordSet) {
//			System.out.println(record.getUniqueValue() + "::::" + record);
//		}
	}
//access token = 1000.cba967dc61f542e6371175934bcc49b1.621887aba23e2b765c4ff6ae89eaba91 & refresh token = 1000.9a60a82995d3f3da703366056ec47ec0.d09145f2023aaa58e63362b9484bd422
	/*
	 * 
	 * 
	 * ownerlookup:::Contact Owner ::::: 4229020000000002485 text:::First Name :::::
	 * 4229020000000002489 text:::Last Name ::::: 4229020000000002491
	 * picklist:::Lead Source ::::: 4229020000000002487 lookup:::Account Name :::::
	 * 4229020000000002493 integer:::Number of Units ::::: 4229020000000297010
	 * email:::Email ::::: 4229020000000002497 text:::Full Name :::::
	 * 4229020000000002529 phone:::Phone ::::: 4229020000000002503 bigint:::Loooong
	 * Number ::::: 4229020000000297001 lookup:::Vendor Name :::::
	 * 4229020000000002495 phone:::Mobile ::::: 4229020000000002511
	 * text:::Department ::::: 4229020000000002501 picklist:::Salutation :::::
	 * 4229020000000022013 text:::Title ::::: 4229020000000002499 phone:::Home Phone
	 * ::::: 4229020000000002505 text:::Fax ::::: 4229020000000002509 phone:::Other
	 * Phone ::::: 4229020000000002507 date:::Date of Birth :::::
	 * 4229020000000002513 text:::Tag ::::: 4229020000000125051 phone:::Asst Phone
	 * ::::: 4229020000000002517 text:::Assistant ::::: 4229020000000002515
	 * boolean:::Email Opt Out ::::: 4229020000000014171 ownerlookup:::Created By
	 * ::::: 4229020000000002521 text:::Skype ID ::::: 4229020000000014175
	 * ownerlookup:::Modified By ::::: 4229020000000002523 email:::Secondary Email
	 * ::::: 4229020000000044001 datetime:::Created Time ::::: 4229020000000002525
	 * text:::Country Code ::::: 4229020000000277229 datetime:::Modified Time :::::
	 * 4229020000000002527 text:::Twitter ::::: 4229020000000053003 text:::LeadIdCPY
	 * ::::: 4229020000000277233 lookup:::Reporting To ::::: 4229020000000182001
	 * boolean:::SMS Opt Out ::::: 4229020000000277227 text:::National Number :::::
	 * 4229020000000277231 datetime:::Last Activity Time ::::: 4229020000000052005
	 * boolean:::Is Record Duplicate ::::: 4229020000000225704 text:::Mailing Street
	 * ::::: 4229020000000002533 text:::Other Street ::::: 4229020000000002535
	 * text:::Mailing City ::::: 4229020000000002537 text:::Other City :::::
	 * 4229020000000002539 text:::Mailing State ::::: 4229020000000002541
	 * text:::Other State ::::: 4229020000000002543 text:::Mailing Zip :::::
	 * 4229020000000002545 text:::Other Zip ::::: 4229020000000002547 text:::Mailing
	 * Country ::::: 4229020000000002549 text:::Other Country :::::
	 * 4229020000000002551 textarea:::Description ::::: 4229020000000002553
	 * profileimage:::Contact Image ::::: 4229020000000152009
	 */

	@Override
	public RecordSet fetchUpdatedRecords(String integrationId, Long lastSyncTime) {
		try {
			setAuth();
			ZCRMModule module = ZCRMModule.getInstance("Contacts");
			BulkAPIResponse response = module.getRecords();
			@SuppressWarnings("unchecked")
			List<ZCRMRecord> records = (List<ZCRMRecord>) response.getData();
			RecordSet recordSet = RecordSet.of("zohocrm-contacts", "id");
			for (ZCRMRecord zcrmRecord : records) {
				Record record = recordSet.add(zcrmRecord.getEntityId().toString());
				record.setColumnValues(zcrmRecord.getData());
			}
			return recordSet;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getUniqueColName(String destination) {
		return "id";
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

}
