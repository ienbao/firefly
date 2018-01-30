package com.dmsoft.firefly.sdk.plugin.annotation;

import java.lang.annotation.*;

/**
 * annotation class for remote data acquisition, runtime will scan the annotated class and register as RDA protocol class
 *
 * @author Can Guan
 */
@OpenService
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RDAProtocol {
}
