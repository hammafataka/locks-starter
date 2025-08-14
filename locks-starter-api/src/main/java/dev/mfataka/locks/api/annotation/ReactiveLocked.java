package dev.mfataka.locks.api.annotation;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

import org.intellij.lang.annotations.Language;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 10.04.2025 12:40
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReactiveLocked {
    @Language("SpEL")
    String value();

    long waitFor() default 5;

    ChronoUnit timeUnit() default ChronoUnit.SECONDS;
}
