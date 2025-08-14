package dev.mfataka.locks.api.exception;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 15.04.2025 8:53
 */
public class LockAlreadyAcquiredException extends LockOperationException {
    public LockAlreadyAcquiredException(final String lockName) {
        super(lockName);
    }

    public LockAlreadyAcquiredException(final String lockName, final String message, final Throwable cause) {
        super(lockName, message, cause);
    }

    public LockAlreadyAcquiredException(final String lockName, final String message) {
        super(lockName, message);
    }

    public LockAlreadyAcquiredException(final String lockName, final Throwable e) {
        super(lockName, e);
    }
}
