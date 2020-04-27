package com.oapps.osync;

import java.util.HashMap;

import com.oapps.osync.freshworks.freshsales.FreshSalesController;
import com.oapps.osync.zoho.crm.ZohoCRMController;

public class ControllerRepo {

	static HashMap<String, Controller> instanceMap = new HashMap<String, Controller>();

	public static Controller getInstance(String type) {
		Controller controller = instanceMap.get(type);
		if (controller == null) {
			switch (type) {
			case "zohocrm-contacts":
				controller = new ZohoCRMController();
				break;
			case "freshsales-contacts":
				controller = new FreshSalesController();
				break;
			}
			instanceMap.put(type, controller);
		}
		return controller;

	}
}
