package com.oapps.osync.controller;

import com.oapps.osync.controller.freshworks.freshsales.FreshSalesController;
import com.oapps.osync.controller.zoho.crm.ZohoCRMController;

public class ControllerRepo {

	public static Controller getInstance(String type, String moduleName) {
		Controller controller = null;
		switch (type) {
		case "zohocrm":
			controller = new ZohoCRMController(moduleName);
			break;
		case "freshsales":
			controller = new FreshSalesController(moduleName);
			break;
		}
		return controller;

	}

}
