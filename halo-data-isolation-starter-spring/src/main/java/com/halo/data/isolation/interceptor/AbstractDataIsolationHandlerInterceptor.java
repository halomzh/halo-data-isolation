package com.halo.data.isolation.interceptor;

import com.halo.data.isolation.holder.DataIsolationInfo;
import com.halo.data.isolation.holder.DataIsolationInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shoufeng
 */

public abstract class AbstractDataIsolationHandlerInterceptor implements HandlerInterceptor {

	@Autowired
	private DataIsolationInfoHolder dataIsolationInfoHolder;

	/**
	 * 获取隔离信息
	 *
	 * @param httpServletRequest 请求
	 * @return 隔离信息
	 */
	protected abstract DataIsolationInfo getDataIsolationInfo(HttpServletRequest httpServletRequest);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		DataIsolationInfo dataIsolationInfo = getDataIsolationInfo(request);
		dataIsolationInfoHolder.setDataIsolationInfo(dataIsolationInfo);

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		dataIsolationInfoHolder.clear();
	}

}
