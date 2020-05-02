package com.oapps.osync;

import org.hibernate.EmptyInterceptor;

public class DBInterceptor extends EmptyInterceptor {
	@Override
	public String onPrepareStatement(String sql) {
		System.out.println("SQL to be executed::" + sql);
		return sql;
	}
}
