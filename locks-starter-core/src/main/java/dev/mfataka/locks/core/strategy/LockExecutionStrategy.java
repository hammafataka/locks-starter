package dev.mfataka.locks.core.strategy;

import org.aspectj.lang.ProceedingJoinPoint;

import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.exception.LockOperationException;
import dev.mfataka.locks.core.descriptor.LockDescriptor;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 14.04.2025 17:40
 */
@RequiredArgsConstructor
public class LockExecutionStrategy {
    private final BlockingLockExecutor blockingLockExecutor;
    private final ReactiveLockExecutor reactiveLockExecutor;

    public Object execute(final LockDescriptor descriptor, final ProceedingJoinPoint pjp) throws LockOperationException {
        return switch (descriptor.lockMode()) {
            case BLOCKING -> blockingLockExecutor.execute(pjp, descriptor);
            case REACTIVE -> reactiveLockExecutor.execute(pjp, descriptor);
        };
    }
}
