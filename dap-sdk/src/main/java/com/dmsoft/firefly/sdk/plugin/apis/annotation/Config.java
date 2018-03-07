package com.dmsoft.firefly.sdk.plugin.apis.annotation;

import java.lang.annotation.*;

/**
 * annotation class for confgi, runtime will scan the annotated class and register as config plugin class
 *
 * @author Can Guan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Config {
}
