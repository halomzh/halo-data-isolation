package com.halo.data.isolation.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shoufeng
 */

@Data
@ConfigurationProperties(prefix = HaloDataIsolationProperties.PREFIX)
@NoArgsConstructor
@AllArgsConstructor
public class HaloDataIsolationProperties {

	public static final String PREFIX = "halo.data.isolation";

	/**
	 * 是否开启
	 */
	private boolean enable = false;

	/**
	 * 数据隔离web拦截器名称
	 */
	private String handlerInterceptorName = "dataIsolationHandlerInterceptor";

}
