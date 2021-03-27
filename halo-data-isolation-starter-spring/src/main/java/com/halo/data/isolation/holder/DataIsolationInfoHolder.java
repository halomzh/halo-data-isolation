package com.halo.data.isolation.holder;

import com.halo.data.isolation.exception.HaloDataIsolationException;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.NamedInheritableThreadLocal;

/**
 * @author shoufeng
 */

@Data
public class DataIsolationInfoHolder {

	private ThreadLocal<DataIsolationInfo> dataIsolationInfoThreadLocal = new NamedInheritableThreadLocal<>("data_isolation_info_thread_local");

	public boolean isEnableIsolation() {
		DataIsolationInfo dataIsolationInfo = dataIsolationInfoThreadLocal.get();
		if (ObjectUtils.isEmpty(dataIsolationInfo)) {
			return false;
		}

		return dataIsolationInfo.isEnableIsolation();
	}

	public void setEnableIsolation(boolean enableIsolation) {
		DataIsolationInfo dataIsolationInfo = dataIsolationInfoThreadLocal.get();
		if (ObjectUtils.isEmpty(dataIsolationInfo)) {
			throw new HaloDataIsolationException((enableIsolation ? "启用" : "关闭") + "数据隔离插件失败: 数据隔离信息(DataIsolationInfo)为空");
		}

		dataIsolationInfo.setEnableIsolation(enableIsolation);
		dataIsolationInfoThreadLocal.set(dataIsolationInfo);
	}

	public ThreadLocal<DataIsolationInfo> getDataIsolationInfoThreadLocal() {
		return dataIsolationInfoThreadLocal;
	}

	public void setDataIsolationInfoThreadLocal(ThreadLocal<DataIsolationInfo> dataIsolationInfoThreadLocal) {
		this.dataIsolationInfoThreadLocal = dataIsolationInfoThreadLocal;
	}

	public void setDataIsolationInfo(DataIsolationInfo dataIsolationInfo) {
		dataIsolationInfoThreadLocal.set(dataIsolationInfo);
	}

	public DataIsolationInfo getDataIsolationInfo() {

		return dataIsolationInfoThreadLocal.get();
	}

	public void clear() {
		dataIsolationInfoThreadLocal.remove();
	}

}
