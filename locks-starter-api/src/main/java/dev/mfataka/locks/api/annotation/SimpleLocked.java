package dev.mfataka.locks.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

import org.intellij.lang.annotations.Language;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 10.04.2025 12:45
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleLocked {
    @Language("SpEL")
    String value();

    long waitFor() default 5;


    ChronoUnit timeUnit() default ChronoUnit.SECONDS;

}
