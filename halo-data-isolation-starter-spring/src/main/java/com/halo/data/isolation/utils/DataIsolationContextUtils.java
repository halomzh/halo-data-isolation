package com.halo.data.isolation.utils;

import com.halo.data.isolation.holder.DataIsolationInfo;
import com.halo.data.isolation.holder.DataIsolationInfoHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author shoufeng
 */

public class DataIsolationContextUtils implements ApplicationContextAware {

	public static ApplicationContext applicationContext;

	public static DataIsolationInfoHolder dataIsolationInfoHolder;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		DataIsolationContextUtils.applicationContext = applicationContext;
		DataIsolationContextUtils.dataIsolationInfoHolder = applicationContext.getBean(DataIsolationInfoHolder.class);
	}

	public static boolean isEnableIsolation() {
		return dataIsolationInfoHolder.isEnableIsolation();
	}

	public static void enter() {
		dataIsolationInfoHolder.setEnableIsolation(true);
	}

	public static void enter(DataIsolationInfo dataIsolationInfo) {
		dataIsolationInfoHolder.setDataIsolationInfo(dataIsolationInfo);
		dataIsolationInfoHolder.setEnableIsolation(true);
	}

	public static void quit() {
		dataIsolationInfoHolder.setEnableIsolation(false);
	}

}
