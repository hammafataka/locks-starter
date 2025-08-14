package dev.mfataka.locks.core.locker.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.mfataka.locks.api.enums.LockType;


/**
 * simple lock that is based on the thread for release, not only signal
 *
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 27.03.2023 12:47
 */
public class SimpleLocker extends SimpleLockSupport {
    private static final Logger log = LoggerFactory.getLogger(SimpleLocker.class);

    SimpleLocker(final String lockName) {
        super(log, lockName);
    }

    @Override
    public boolean releaseLock() {
        if (isReleased()) {
            return true;
        }
        final var lockedByCurrentThread = isLockedByCurrentThread();
        if (lockedByCurrentThread) {
            return super.releaseLock();
        }
        log.trace("lock is not locked by current thread [{}], cannot release", Thread.currentThread().getName());
        return false;
    }

    @Override
    public long getLockElapsedTime() {
        return lastLockedAt.get();
    }


    @Override
    public String getLockName() {
        return lockName;
    }

    @Override
    public LockType getLockType() {
        return LockType.LOCAL;
    }
}
