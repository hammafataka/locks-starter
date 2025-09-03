package dev.mfataka.locks.core.strategy;

import org.aspectj.lang.ProceedingJoinPoint;

import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.exception.LockOperationException;
import dev.mfataka.locks.core.descriptor.LockDescriptor;
import dev.mfataka.locks.core.factory.StandardReliableLocks;

/**
 * @author HAMMA FATAKA
 */
@RequiredArgsConstructor
public class BlockingLockExecutor extends UnifiedStrategySupport {
    private final StandardReliableLocks standardReliableLocks;


    public Object execute(final ProceedingJoinPoint pjp, final LockDescriptor descriptor) throws LockOperationException {
        final var lock = switch (descriptor.lockMode()) {
            case BLOCKING -> {
                if (descriptor.lockType().isDistributed()) {
                    yield standardReliableLocks.createDistributed(descriptor.name());
                }
                yield standardReliableLocks.createSimple(descriptor.name());

            }
            case REACTIVE -> throw new IllegalStateException("FATAL: Lock mode must not be reactive");
        };
        return executeCriticalSection(pjp, descriptor, lock);
    }

}
