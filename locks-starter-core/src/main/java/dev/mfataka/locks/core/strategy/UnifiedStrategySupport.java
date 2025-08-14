package dev.mfataka.locks.core.strategy;

import java.util.function.Function;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.base.ReactiveBaseLocker;
import dev.mfataka.locks.api.exception.LockAlreadyAcquiredException;
import dev.mfataka.locks.api.exception.LockOperationException;
import dev.mfataka.locks.core.descriptor.LockDescriptor;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 14.04.2025 17:55
 */
@RequiredArgsConstructor
public abstract class UnifiedStrategySupport {

    protected static Object executeCriticalSection(final ProceedingJoinPoint joinPoint, final LockDescriptor lockDescriptor, final BaseLocker locker) throws LockOperationException {
        final var lockName = lockDescriptor.name();
        try {
            final var waitTime = lockDescriptor.getTimeout();
            if (!waitTime.isZero() && !waitTime.isNegative()) {
                final var locked = locker.obtainLock(waitTime);
                if (locked) {
                    final var executed = executeJoinPoint(joinPoint);
                    locker.releaseLock();
                    return executed;
                }
                throw new LockAlreadyAcquiredException(lockName, "Could not obtain lock: " + lockName);
            }

            final var locked = locker.tryLock();
            if (locked) {
                final var executed = executeJoinPoint(joinPoint);
                locker.releaseLock();
                return executed;
            }
            throw new LockAlreadyAcquiredException(lockName, "Could not obtain lock: " + lockName);
        } catch (Exception e) {
            if (e instanceof LockOperationException) {
                throw (LockOperationException) e;
            }
            throw new LockOperationException(lockName, e);
        }
    }

    private static Object executeJoinPoint(final ProceedingJoinPoint joinPoint) throws LockOperationException {
        final Object proceed;
        try {
            proceed = joinPoint.proceed();
        } catch (Throwable e) {
            throw new LockOperationException(e);
        }
        return proceed;
    }


    protected @NotNull Publisher<?> getCorePublisher(final ProceedingJoinPoint joinPoint, final LockDescriptor lockDescriptor, final ReactiveBaseLocker lock) {
        try {
            final var signature = (MethodSignature) joinPoint.getSignature();
            final var returnType = signature.getReturnType();

            final var evaluatedName = lockDescriptor.name();
            final var timeout = lockDescriptor.getTimeout();

            if (Mono.class.isAssignableFrom(returnType)) {
                final var execution = getMonoFunction(joinPoint, evaluatedName);
                if (timeout.isZero()) {
                    return lock.tryLock(execution);
                }
                return lock.obtainLockMono(timeout, execution);

            } else if (Flux.class.isAssignableFrom(returnType)) {
                final var execution = getFluxFunction(joinPoint, evaluatedName);
                if (timeout.isZero()) {
                    return lock.tryLockManyFlux(execution);
                }
                return lock.obtainLockManyFlux(timeout, execution);
            }
            throw new LockOperationException("@ReactiveLocked must be applied to Mono<T> or Flux<T> methods");
        } catch (Exception e) {
            if (e instanceof LockOperationException) {
                return Mono.error(e);
            }
            return Mono.error(new LockOperationException(lock.getLockName(), e));
        }
    }

    protected static @NotNull Function<Boolean, Mono<Object>> getMonoFunction(final ProceedingJoinPoint joinPoint, final String name) {
        return success -> {
            try {
                return success ? ((Mono<Object>) executeJoinPoint(joinPoint)) : Mono.error(new LockAlreadyAcquiredException("Reactive lock not acquired: " + name));
            } catch (Throwable e) {
                return Mono.error(e);
            }
        };
    }

    protected static @NotNull Function<Boolean, Flux<Object>> getFluxFunction(final ProceedingJoinPoint joinPoint, String name) {
        return success -> {
            try {
                return success ? ((Flux<Object>) executeJoinPoint(joinPoint)) : Flux.error(new LockAlreadyAcquiredException("Reactive lock not acquired: " + name));
            } catch (Throwable e) {
                return Flux.error(e);
            }
        };
    }
}
