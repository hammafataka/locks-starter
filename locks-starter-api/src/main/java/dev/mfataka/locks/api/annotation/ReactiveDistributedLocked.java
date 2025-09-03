package dev.mfataka.locks.api.annotation;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

import org.intellij.lang.annotations.Language;

/**
 * @author HAMMA FATAKA
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReactiveDistributedLocked {
    @Language("SpEL")
    String value();

    long waitFor() default 5;

    ChronoUnit timeUnit() default ChronoUnit.SECONDS;
}
