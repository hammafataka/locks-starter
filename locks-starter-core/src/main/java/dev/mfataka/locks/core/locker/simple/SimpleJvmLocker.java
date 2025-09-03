package dev.mfataka.locks.core.locker.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.mfataka.locks.api.locker.JvmLocker;

/**
 * lock that is based only on the signal and not thread
 *
 * @author HAMMA FATAKA
 */
public class SimpleJvmLocker extends SimpleLockSupport implements JvmLocker {
    private static final Logger log = LoggerFactory.getLogger(SimpleJvmLocker.class);

    public SimpleJvmLocker(final String lockName) {
        super(log, lockName);
    }

    @Override
    public String getLockName() {
        return lockName;
    }

    @Override
    public long getLockElapsedTime() {
        return lastLockedAt.get();
    }
}
