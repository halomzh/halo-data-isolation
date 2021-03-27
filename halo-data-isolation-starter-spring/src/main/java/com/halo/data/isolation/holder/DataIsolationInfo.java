package com.halo.data.isolation.holder;

import com.google.common.base.CaseFormat;
import com.halo.data.isolation.exception.HaloDataIsolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shoufeng
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataIsolationInfo {

	/**
	 * 是否需要数据隔离
	 */
	private boolean enableIsolation = false;

	/**
	 * 字段名、值数组
	 */
	private Map<String, List<String>> fieldNameIncludeValueListMap = new HashMap<>();

	public void addFieldNameIncludeValue(String fieldName, String includeValue) {
		fieldName = convertFieldName(fieldName);
		List<String> includeValueListTemp = fieldNameIncludeValueListMap.getOrDefault(fieldName, new ArrayList<>());
		includeValueListTemp.add(includeValue);

		fieldNameIncludeValueListMap.put(fieldName, includeValueListTemp);
	}

	public void addFieldNameIncludeValueList(String fieldName, List<String> includeValueList) {
		fieldName = convertFieldName(fieldName);
		List<String> includeValueListTemp = fieldNameIncludeValueListMap.getOrDefault(fieldName, new ArrayList<>());
		includeValueListTemp.addAll(includeValueList);

		fieldNameIncludeValueListMap.put(fieldName, includeValueListTemp);
	}

	public void removeFieldNameInclude(String fieldName, String includeValue) {
		fieldName = convertFieldName(fieldName);
		List<String> includeValueListTemp = fieldNameIncludeValueListMap.getOrDefault(fieldName, new ArrayList<>());
		includeValueListTemp.remove(includeValue);
		fieldNameIncludeValueListMap.put(fieldName, includeValueListTemp);
	}

	public void removeFieldName(String fieldName) {
		fieldName = convertFieldName(fieldName);
		fieldNameIncludeValueListMap.remove(fieldName);
	}

	private String convertFieldName(String fieldName) {
		if (StringUtils.isBlank(fieldName)) {
			throw new HaloDataIsolationException("设置DataIsolationInfo失败: fieldName不能为空");
		}
		if (fieldName.contains("_")) {

			return fieldName.toLowerCase();
		}

		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
	}

}
