package dev.mfataka.locks.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 13.04.2025 16:30
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Interval {
    /**
     * Interval period.
     * By default, can be specified as 'property placeholder', e.g. {@code ${locked.interval}}.
     */
    String value();

    /**
     * Interval {@link TimeUnit} represented by {@link #value()}.
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
