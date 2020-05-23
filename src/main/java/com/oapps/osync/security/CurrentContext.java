package com.oapps.osync.security;

import com.oapps.osync.entity.AuthorizationEntity;

public class CurrentContext {

	static ThreadLocal<Long> currentOsyncId = new ThreadLocal<Long>();

	static ThreadLocal<Boolean> disableDBCheck = new ThreadLocal<Boolean>();

	// static ThreadLocal<AccountInfoEntity> currentOsyncAccount = new
	// ThreadLocal<AccountInfoEntity>();

	static ThreadLocal<AuthorizationEntity> currentToken = new ThreadLocal<AuthorizationEntity>();

	public static void setThreadSync(Long osyncId, /* AccountInfoEntity osyncAccount, */ AuthorizationEntity token) {
		// currentOsyncAccount.set(osyncAccount);
		currentOsyncId.set(osyncId);
		currentToken.set(token);
	}

	public static Long getCurrentOsyncId() {
		return currentOsyncId.get();
	}

//	public static AccountInfoEntity getCurrentOsyncAccount() {
//		return currentOsyncAccount.get();
//	}

	public static AuthorizationEntity getCurrentToken() {
		return currentToken.get();
	}

	public static void clear() {
//		currentOsyncAccount.set(null);
		currentOsyncId.set(null);
		currentToken.set(null);
		disableDBCheck.set(null);
	}

	public static void setDisableDBCheck(Boolean bool) {
		disableDBCheck.set(bool);
	}

	public static boolean isDBCheckDisabled() {
//		return true;
		return disableDBCheck.get();
	}
}
