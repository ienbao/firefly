package com.dmsoft.firefly.sdk.plugin.apis.annotation;

import java.lang.annotation.*;

/**
 * annotation class for data out put and register as data output class
 *
 * @author Can Guan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataOutput {
}
