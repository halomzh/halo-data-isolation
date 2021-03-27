package com.halo.data.isolation.aspect;

import com.halo.data.isolation.annotation.DataIsolationContext;
import com.halo.data.isolation.utils.DataIsolationContextUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author shoufeng
 */

@Data
@Aspect
@Slf4j
public class DataIsolationContextAspect {

	@Pointcut("@annotation(com.halo.data.isolation.annotation.DataIsolationContext)")
	public void dataIsolationContextPointcut() {
	}

	@Around("dataIsolationContextPointcut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		DataIsolationContext dataIsolationContext = getAnnotation(point, DataIsolationContext.class);

		boolean enableIsolationTemp = DataIsolationContextUtils.isEnableIsolation();
		if (dataIsolationContext.enableDataIsolation()) {
			DataIsolationContextUtils.enter();
		} else {
			DataIsolationContextUtils.quit();
		}

		Object value = point.proceed();

		if (enableIsolationTemp) {
			DataIsolationContextUtils.enter();
		} else {
			DataIsolationContextUtils.quit();
		}

		return value;
	}

	@SneakyThrows
	public <T extends Annotation> T getAnnotation(ProceedingJoinPoint point, Class<T> clazz) {
		String methodName = point.getSignature().getName();
		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getParameterTypes();
		Method objMethod = point.getTarget().getClass().getMethod(methodName, parameterTypes);

		return objMethod.getAnnotation(clazz);
	}

}
