package com.oapps.osync.service;

public class OsyncEnums {
	private static final int DIRECTION_LEFT=1;
	private static final int DIRECTION_RIGHT=2;
	private static final int DIRECTION_BOTH=3;
	public enum IntegrationStatus {
		RUNNING, COMPLETE, ERROR, NOT_STARTED
	}
	public enum IntegrationDirection {
		DIRECTION_LEFT,DIRECTION_RIGHT,DIRECTION_BOTH
	}
	public enum IntegrationSyncStatus {
		START, PAUSE, STOP
	}
}
