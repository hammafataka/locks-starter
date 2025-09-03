package dev.mfataka.locks.core.locker.simple;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.enums.LockMode;

/**
 * @author HAMMA FATAKA
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SimpleLockSupport implements BaseLocker {
    private final AtomicBoolean locker = new AtomicBoolean(false);
    private final AtomicLong owner = new AtomicLong();
    private final Logger log;

    protected final String lockName;
    protected final AtomicLong lastLockedAt = new AtomicLong();

    @Override
    public synchronized boolean tryLock() {
        final var locked = locker.compareAndSet(false, true);
        final var threadName = Thread.currentThread().getName();
        if (locked) {
            lastLockedAt.set(System.currentTimeMillis() / 1000L);
            log.debug("lock with name [{}] is locked at [{}] by current thread [{}]", lockName, lastLockedAt.get(), threadName);
            owner.set(Thread.currentThread().getId());
            return true;
        }
        log.debug("lock with name [{}] is not locked by current thread [{}], last locked at [{}]", lockName, threadName, lastLockedAt.get());
        return false;
    }

    @Override
    public synchronized boolean obtainLock(final Duration timeout) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return tryLock() || lockWithTime(timeout);
    }

    private synchronized boolean lockWithTime(Duration timeout) throws InterruptedException {
        if (timeout.isNegative()) {
            return false;
        }
        final var timeoutMillis = timeout.toMillis();
        final var startedMillis = System.currentTimeMillis();
        var elapsedTime = System.currentTimeMillis();
        while (true) {
            final var currentTimeMillis = System.currentTimeMillis();
            elapsedTime = currentTimeMillis - startedMillis;
            final var locked = tryLock();
            if (locked) {
                notifyAll();
                return true;
            }
            if (elapsedTime >= timeoutMillis) {
                notifyAll();
                return false;
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            wait(timeoutMillis);
        }
    }

    public boolean releaseLock() {
        if (isReleased()) {
            return true;
        }
        final var releaseResult = locker.compareAndSet(true, false);
        final var threadName = Thread.currentThread().getName();
        if (releaseResult) {
            log.debug("lock with name [{}] is released by current thread [{}]", lockName, threadName);
            return true;
        }
        log.debug("lock with name [{}] cannot be released by current thread [{}], last locked at [{}] by [{}]", lockName, threadName, lastLockedAt.get(), owner.get());
        return false;
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return owner.get() == Thread.currentThread().getId();
    }

    @Override
    public boolean isLocked() {
        return locker.get();
    }


    @Override
    public LockMode getLockMode() {
        return LockMode.BLOCKING;
    }
}
