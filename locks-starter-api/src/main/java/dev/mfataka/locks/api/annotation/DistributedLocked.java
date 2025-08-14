package dev.mfataka.locks.api.annotation;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

import org.intellij.lang.annotations.Language;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 10.04.2025 12:22
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLocked {
    /**
     * The name of the lock.
     * <p>Supports Spring Expression Language (SpEL), e.g. <code>#user.id + '-lock'</code></p>
     */
    @Language("SpEL")
    String value();

    long waitFor() default 5;

    ChronoUnit timeUnit() default ChronoUnit.SECONDS;
}
