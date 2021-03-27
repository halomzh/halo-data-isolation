package com.halo.data.isolation.config;

import com.halo.data.isolation.aspect.DataIsolationContextAspect;
import com.halo.data.isolation.config.properties.HaloDataIsolationProperties;
import com.halo.data.isolation.holder.DataIsolationInfoHolder;
import com.halo.data.isolation.interceptor.AbstractDataIsolationHandlerInterceptor;
import com.halo.data.isolation.plugin.DataIsolationInterceptor;
import com.halo.data.isolation.utils.DataIsolationContextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author shoufeng
 */

@Configuration
@EnableConfigurationProperties(value = {
		HaloDataIsolationProperties.class
})
@ConditionalOnProperty(prefix = HaloDataIsolationProperties.PREFIX, value = "enable")
@Slf4j
@Data
public class HaloDataIsolationConfig implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Bean
	public DataIsolationInfoHolder dataIsolationInfoHolder() {

		return new DataIsolationInfoHolder();
	}

	@Bean
	@DependsOn(value = {"dataIsolationInfoHolder"})
	public DataIsolationInterceptor dataIsolationInterceptor() {

		return new DataIsolationInterceptor();
	}

	@Bean
	@DependsOn(value = {"dataIsolationInterceptor"})
	public DataIsolationContextUtils dataIsolationContextUtils() {

		return new DataIsolationContextUtils();
	}

	@Bean
	@DependsOn(value = {"dataIsolationContextUtils"})
	public DataIsolationContextAspect dataIsolationContextAspect() {

		return new DataIsolationContextAspect();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		HaloDataIsolationProperties haloDataIsolationProperties = applicationContext.getBean(HaloDataIsolationProperties.class);
		HandlerInterceptor dataIsolationHandlerInterceptor = applicationContext.getBean(haloDataIsolationProperties.getHandlerInterceptorName(), AbstractDataIsolationHandlerInterceptor.class);

		registry.addInterceptor(dataIsolationHandlerInterceptor).addPathPatterns("/**");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
