package com.halo.data.isolation.annotation;

import java.lang.annotation.*;

/**
 * @author shoufeng
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataIsolationContext {

	boolean enableDataIsolation() default true;

}
