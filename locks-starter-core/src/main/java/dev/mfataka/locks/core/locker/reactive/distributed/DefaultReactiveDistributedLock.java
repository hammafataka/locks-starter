package dev.mfataka.locks.core.locker.reactive.distributed;

import java.time.Duration;
import java.util.Collection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.ReactiveDistributedLocker;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.api.factory.DistributedLock;
import dev.mfataka.locks.api.factory.ReactiveDistributedLock;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 16.03.2023 11:10
 */
@Slf4j
public class DefaultReactiveDistributedLock implements ReactiveDistributedLock {
    private final DistributedLock delegate;
    @Getter
    private final LockContext lockContext;

    public static DefaultReactiveDistributedLock of(final DistributedLock lockFactory, final LockContext context) {
        return new DefaultReactiveDistributedLock(lockFactory, context);
    }

    private DefaultReactiveDistributedLock(final DistributedLock lockFactory, final LockContext context) {
        this.delegate = lockFactory;
        this.lockContext = context;
    }


    @Override
    public ReactiveDistributedLocker get(final String lockName) {
        final var lock = delegate.get(lockName);
        return DefaultReactiveDistributedLocker.of(lock, lockContext.debugEnabled());
    }

    @Override
    public void remove(final String lockName) {
        delegate.remove(lockName);
    }

    @Override
    public boolean exists(final String lockName) {
        return delegate.exists(lockName);
    }

    @Override
    public int clearAllLocks(final Duration maxAge) {
        return delegate.clearAllLocks(maxAge);
    }

    @Override
    public int clearAllLocks() {
        return delegate.clearAllLocks();
    }

    @Override
    public int existingLocksCount() {
        return delegate.existingLocksCount();
    }

    @Override
    public Collection<Locker> getLockers() {
        return delegate.getLockers();
    }

    @Override
    public LockType getLockType() {
        return LockType.DISTRIBUTED;
    }

    @Override
    public LockMode getLockMode() {
        return LockMode.REACTIVE;
    }
}