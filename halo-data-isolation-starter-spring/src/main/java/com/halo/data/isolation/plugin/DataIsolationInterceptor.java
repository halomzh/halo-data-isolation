package com.halo.data.isolation.plugin;

import com.halo.data.isolation.exception.HaloDataIsolationException;
import com.halo.data.isolation.holder.DataIsolationInfo;
import com.halo.data.isolation.holder.DataIsolationInfoHolder;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author shoufeng
 */

@Slf4j
@Setter
@Accessors(chain = true)
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataIsolationInterceptor implements Interceptor {

	@Autowired
	private DataIsolationInfoHolder dataIsolationInfoHolder;

	private static final Pattern FROM_TABLE_PATTERN = Pattern.compile("(from\\s+\\w+\\s+)");

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (ObjectUtils.isEmpty(dataIsolationInfoHolder)
				|| !dataIsolationInfoHolder.isEnableIsolation()) {
			return invocation.proceed();
		}
		DataIsolationInfo dataIsolationInfo = dataIsolationInfoHolder.getDataIsolationInfo();
		if (ObjectUtils.isEmpty(dataIsolationInfo)) {
			throw new HaloDataIsolationException("运行数据隔离插件失败: 数据隔离信息(DataIsolationInfo)为空");
		}
		//真实对象为RoutingStatementHandler，具体逻辑可以看configuration的newStatementHandler
		MetaObject metaObject = SystemMetaObject.forObject(invocation.getTarget());
		MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
		//非select请求和存储过程不拦截
		if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
				|| StatementType.CALLABLE == mappedStatement.getStatementType()) {

			return invocation.proceed();
		}
		BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
		String sql = boundSql.getSql().toLowerCase();
		log.info("数据隔离插件拦截前的sql: {}", sql);

		if (!sql.contains("where")) {
			Matcher matcher = FROM_TABLE_PATTERN.matcher(sql);
			if (matcher.find()) {
				String oldFromTableStr = matcher.group();
				sql = sql.replace(oldFromTableStr, oldFromTableStr + " where 1 = 1 ");
			}
		}

		StringBuilder sqlSb = new StringBuilder(sql);

		Map<String, List<String>> fieldNameIncludeValueListMap = dataIsolationInfo.getFieldNameIncludeValueListMap();
		for (String fieldName : fieldNameIncludeValueListMap.keySet()) {
			List<String> valueList = fieldNameIncludeValueListMap.get(fieldName);
			if (CollectionUtils.isEmpty(valueList)) {
				sqlSb = new StringBuilder(sqlSb.toString().replace("where", " where 1 != 1 and "));

				metaObject.setValue("delegate.boundSql.sql", sqlSb.toString());
				log.info("数据隔离插件拦截后的sql: {}", sqlSb.toString());

				return invocation.proceed();
			}

			String valueListStr = Arrays.toString(valueList.toArray()).replace("[", "(").replace("]", ")");
			sqlSb = new StringBuilder(sqlSb.toString().replace("where", " where " + fieldName + " in " + valueListStr + " and "));

		}

		metaObject.setValue("delegate.boundSql.sql", sqlSb.toString());
		log.info("数据隔离插件拦截后的sql: {}", sqlSb.toString());

		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}

		return target;
	}

	@Override
	public void setProperties(Properties properties) {

	}

}
