package dev.mfataka.locks.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.exception.LockOperationException;
import dev.mfataka.locks.core.resolver.LockMetadataResolver;
import dev.mfataka.locks.core.strategy.LockExecutionStrategy;

/**
 * @author HAMMA FATAKA
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class UnifiedLockAspect {
    private final LockExecutionStrategy strategy;
    private final LockMetadataResolver resolver;

    @Around("@annotation(dev.mfataka.locks.api.annotation.SimpleLocked) " +
            "|| @annotation(dev.mfataka.locks.api.annotation.ReactiveLocked) " +
            "|| @annotation(dev.mfataka.locks.api.annotation.DistributedLocked) " +
            "|| @annotation(dev.mfataka.locks.api.annotation.ReactiveDistributedLocked)")
    public Object around(final ProceedingJoinPoint pjp) throws LockOperationException {
        final var method = ((MethodSignature) pjp.getSignature()).getMethod();
        log.debug("aspect called for {}", method.getName());
        final var descriptor = resolver.resolve(method, pjp.getArgs());
        return strategy.execute(descriptor, pjp);
    }
}
