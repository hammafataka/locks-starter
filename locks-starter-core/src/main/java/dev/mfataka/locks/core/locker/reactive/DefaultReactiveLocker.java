package dev.mfataka.locks.core.locker.reactive;

import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.ReactiveLocker;
import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.api.utils.LoggingUtils;
import dev.mfataka.locks.core.factory.LockRegistry;
import dev.mfataka.locks.core.locker.simple.SimpleJvmLocker;

/**
 * @author HAMMA FATAKA
 */
@Slf4j
public class DefaultReactiveLocker extends ReactiveLockerSupport implements ReactiveLocker {

    public static final String REACTIVE_DELEGATE = "#reactive_delegate";
    private final SimpleJvmLocker delegate;

    public static DefaultReactiveLocker of(final String lockName, final boolean debugEnabled) {
        return new DefaultReactiveLocker(lockName, debugEnabled);
    }

    private DefaultReactiveLocker(final String lockName, final boolean debugEnabled) {
        super(lockName, LoggingUtils.of(log, debugEnabled));
        this.delegate = LockRegistry.defaultJvmLock().get(lockName + REACTIVE_DELEGATE);
    }

    @Override
    public SimpleJvmLocker getLock() {
        return delegate;
    }

    @Override
    protected BaseLocker getBaseLock() {
        return delegate;
    }


    @Override
    public String getLockName() {
        return lockName;
    }

    @Override
    public LockType getLockType() {
        return LockType.LOCAL;
    }

    @Override
    public long getLockElapsedTime() {
        return delegate.getLockElapsedTime();
    }
}
