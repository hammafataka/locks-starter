package dev.mfataka.locks.core.resolver;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.annotation.DistributedLocked;
import dev.mfataka.locks.api.annotation.SimpleLocked;
import dev.mfataka.locks.api.annotation.ReactiveDistributedLocked;
import dev.mfataka.locks.api.annotation.ReactiveLocked;
import dev.mfataka.locks.core.descriptor.LockDescriptor;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.core.spel.SpelExpressionEvaluator;

/**
 * @author HAMMA FATAKA
 */
@RequiredArgsConstructor
public class LockMetadataResolver {
    protected final SpelExpressionEvaluator expressionEvaluator;

    public LockDescriptor resolve(Method method, Object[] args) {
        if (method.isAnnotationPresent(SimpleLocked.class)) {

            final var annotation = method.getAnnotation(SimpleLocked.class);
            final var lockName = getLockName(method, args, annotation.value());
            return new LockDescriptor(lockName, annotation.waitFor(), annotation.timeUnit(), LockType.LOCAL, LockMode.BLOCKING);

        } else if (method.isAnnotationPresent(ReactiveLocked.class)) {

            final var annotation = method.getAnnotation(ReactiveLocked.class);
            final var lockName = getLockName(method, args, annotation.value());
            return new LockDescriptor(lockName, annotation.waitFor(), annotation.timeUnit(), LockType.LOCAL, LockMode.REACTIVE);

        } else if (method.isAnnotationPresent(DistributedLocked.class)) {

            final var annotation = method.getAnnotation(DistributedLocked.class);
            final var lockName = getLockName(method, args, annotation.value());
            return new LockDescriptor(lockName, annotation.waitFor(), annotation.timeUnit(), LockType.DISTRIBUTED, LockMode.BLOCKING);

        } else if (method.isAnnotationPresent(ReactiveDistributedLocked.class)) {

            final var annotation = method.getAnnotation(ReactiveDistributedLocked.class);
            final var lockName = getLockName(method, args, annotation.value());
            return new LockDescriptor(lockName, annotation.waitFor(), annotation.timeUnit(), LockType.DISTRIBUTED, LockMode.REACTIVE);

        }
        throw new IllegalStateException("No supported lock annotation found on method: " + method.getName());
    }

    private String getLockName(final Method method, final Object[] args, final String name) {
        return expressionEvaluator.evaluate(name, method, args);
    }
}
