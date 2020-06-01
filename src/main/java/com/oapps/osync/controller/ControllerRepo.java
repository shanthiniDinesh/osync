package com.oapps.osync.controller;

import com.oapps.osync.controller.freshworks.freshsales.FreshSalesController;
import com.oapps.osync.controller.outreach.OutreachController;
import com.oapps.osync.controller.salesLoft.SalesLoftController;
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
		case "salesloft":
			controller = new SalesLoftController(moduleName);
			break;
		case "outreach":
			controller = new OutreachController(moduleName);
			break;
		}
		return controller;

	}

}
