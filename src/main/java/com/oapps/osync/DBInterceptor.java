package com.oapps.osync;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.oapps.osync.entity.AccountInfoEntity;

import lombok.extern.java.Log;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

@Log
public class DBInterceptor extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8980403977800047424L;
	private static final List<String> excludeTables = Arrays.asList("service_info", "default_field",
			"default_field_map", "module", "osync_authorization");

	@Override
	public String onPrepareStatement(String sql) {
		String op = sql.substring(0, 10);

		if (op.trim().toLowerCase().startsWith("select")) {
			Long osyncId = CurrentContext.getCurrentOsyncId();
			if (!CurrentContext.isDBCheckDisabled()
					&& (CurrentContext.isDBCheckDisabled() || osyncId == null || osyncId == -1)) {
				log.warning("REJECTING sql since Osync ID not set : " + sql);
				throw new RuntimeException("Osync Id not set in credentials. ");
			}
			try {
				Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
				// get the body of the select query
				PlainSelect ps = (PlainSelect) selectStatement.getSelectBody();
				net.sf.jsqlparser.schema.Table table = (net.sf.jsqlparser.schema.Table) ps.getFromItem();
				if (!excludeTables.contains(table.getName())) {
					// create new condition expression
					EqualsTo equals = new EqualsTo();
					equals.setLeftExpression(new Column(getName(table) + ".osync_id"));
					equals.setRightExpression(new LongValue(osyncId));
					// add and to the existing condition
					Expression oldCondition = ps.getWhere();
					Expression newCondition = equals;
					if (oldCondition != null) {
						newCondition = new AndExpression(new Parenthesis(oldCondition), new Parenthesis(equals));
					}
					ps.setWhere(newCondition);
					sql = selectStatement.toString();
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "error while transforming" + e.getMessage(), e);
			}
		}
		log.info("Query to be executed: " + sql);
		return super.onPrepareStatement(sql);
	}

	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (!(entity instanceof AccountInfoEntity)) {

			int index = 0;
			int foundIndex = -1;
			for (String prop : propertyNames) {
				if (prop.equals("osyncId")) {
					foundIndex = index;
				}
				index++;
			}

			if (foundIndex != -1) {
				Long osyncId = (Long) state[foundIndex];
				Long currentOsyncId = CurrentContext.getCurrentOsyncId();
				if (osyncId == null || currentOsyncId == null || currentOsyncId.longValue() != osyncId.longValue()) {
					throw new RuntimeException("Not allowed, Incorrect OsyncId");
				}
			}
		}
		super.onDelete(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (!(entity instanceof AccountInfoEntity)) {

			int index = 0;
			int foundIndex = -1;
			for (String prop : propertyNames) {
				if (prop.equals("osyncId")) {
					foundIndex = index;
				}
				index++;
			}

			if (foundIndex != -1) {
				Long osyncId = (Long) state[foundIndex];
				Long currentOsyncId = CurrentContext.getCurrentOsyncId();
				if (osyncId == null || currentOsyncId == null || currentOsyncId.longValue() != osyncId.longValue()) {
					throw new RuntimeException("Not allowed, Incorrect OsyncId");
				}
			}
		}
		return super.onSave(entity, id, state, propertyNames, types);
	}

//	private void addOsyncIdScope(Object entity, Object[] state, String[] propertyName) {
//
//		Long orgIdFromContext = CurrentContext.getCurrentOsyncId();
//		Long orgIdFromEntity = ((BaseEntity) entity).getOrgId();
//		if (orgIdFromContext == null || orgIdFromContext.longValue() == -1) {
//			if (orgIdFromEntity == null) {
//				throw new ClassCastException();
//			}
//			log.warn("ServiceContext credential is null. Possibly from worker call. Org Id: {}, Entity: ",
//					orgIdFromEntity, ((BaseEntity) entity).getClass().getName());
//			return;
//		}
//		for (int index = 0; index < propertyName.length; index++) {
//			if (propertyName[index].equals("orgId")) {
//				if (!orgIdFromContext.equals(state[index])) {
//					if (state[index] != null) {
//						log.warn(
//								"Mismatch in entity orgid : {} and request scope orgid: {}. Replacing with request org id",
//								state[index], orgIdFromContext);
//					}
//					((BaseEntity) entity).setOrgId(orgIdFromContext);
//					state[index] = orgIdFromContext;
//				}
//				return;
//			}
//		}
//		throw new ClassCastException();
//
//	}

	private String getName(net.sf.jsqlparser.schema.Table table) {
		return table.getAlias() == null ? table.getName() : table.getAlias().getName();
	}
}
