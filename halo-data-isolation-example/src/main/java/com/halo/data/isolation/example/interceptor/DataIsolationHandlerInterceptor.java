package com.halo.data.isolation.example.interceptor;

import com.halo.data.isolation.holder.DataIsolationInfo;
import com.halo.data.isolation.interceptor.AbstractDataIsolationHandlerInterceptor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shoufeng
 */

@Component(value = "dataIsolationHandlerInterceptor")
public class DataIsolationHandlerInterceptor extends AbstractDataIsolationHandlerInterceptor {

	@Override
	protected DataIsolationInfo getDataIsolationInfo(HttpServletRequest httpServletRequest) {
		//理论上是要从当前登陆人的信息中获取的，因为只是个demo我就写死数据了
		DataIsolationInfo dataIsolationInfo = new DataIsolationInfo();
		dataIsolationInfo.setEnableIsolation(true);
		dataIsolationInfo.addFieldNameIncludeValue("tagA", "1");

		return dataIsolationInfo;
	}

}
