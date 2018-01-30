package com.dmsoft.firefly.sdk.plugin.annotation;

import java.lang.annotation.*;

/**
 * Annotation for open api, application will scan the annotated class and register in context.
 * All public method will be register except the {@link ExcludeMethod}
 *
 * @author Can Guan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenService {
}
