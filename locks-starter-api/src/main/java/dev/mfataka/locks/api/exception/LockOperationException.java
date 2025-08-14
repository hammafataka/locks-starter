package dev.mfataka.locks.api.exception;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 13.04.2025 16:28
 */
public class LockOperationException extends RuntimeException {
    protected String lockName;

    public LockOperationException(final String lockName) {
        this.lockName = lockName;
    }

    public LockOperationException(final String lockName, final String message, Throwable cause) {
        super(message, cause);
        this.lockName = lockName;
    }

    public LockOperationException(Throwable cause) {
        super(cause);
    }

    public LockOperationException(final String lockName, final String message) {
        super(message);
        this.lockName = lockName;
    }

    public LockOperationException(final String lockName, final Throwable e) {
        super(e);
        this.lockName = lockName;
    }
}
