package com.dmsoft.firefly.sdk.plugin.annotation;

import java.lang.annotation.*;

/**
 * annotation class for analysis, runtime will scan the annotated class and register as analysis plugin class
 *
 * @author Can Guan
 */
@OpenService
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Analysis {
}
