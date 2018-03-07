package com.dmsoft.firefly.sdk.plugin.apis.annotation;

import java.lang.annotation.*;

/**
 * annotation class for chart, runtime will scan the annotated class and register as chart class
 *
 * @author Can Guan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Chart {
}
