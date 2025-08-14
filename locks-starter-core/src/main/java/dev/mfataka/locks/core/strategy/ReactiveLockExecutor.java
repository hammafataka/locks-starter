package dev.mfataka.locks.core.strategy;

import org.aspectj.lang.ProceedingJoinPoint;

import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.core.descriptor.LockDescriptor;
import dev.mfataka.locks.core.factory.StandardReliableLocks;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 14.04.2025 17:40
 */
@RequiredArgsConstructor
public class ReactiveLockExecutor extends UnifiedStrategySupport {
    private final StandardReliableLocks standardReliableLocks;


    public Object execute(ProceedingJoinPoint pjp, LockDescriptor descriptor) {
        final var lock = switch (descriptor.lockMode()) {
            case REACTIVE -> {
                if (descriptor.lockType().isDistributed()) {
                    yield standardReliableLocks.createReactiveDistributed(descriptor.name());
                }
                yield standardReliableLocks.createReactive(descriptor.name());
            }
            case BLOCKING -> throw new IllegalStateException("FATAL: Lock mode must not be Blocking");
        };
        return getCorePublisher(pjp, descriptor, lock);

    }
}
