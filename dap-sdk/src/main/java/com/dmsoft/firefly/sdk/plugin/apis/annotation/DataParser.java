package com.dmsoft.firefly.sdk.plugin.apis.annotation;

import java.lang.annotation.*;

/**
 * annotation class for data parser, runtime will scan the annotated class and register as data parser class
 *
 * @author Can Guan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataParser {
}
