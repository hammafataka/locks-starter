package dev.mfataka.locks.api.base;

import java.time.Duration;

import dev.mfataka.locks.api.Locker;

/**
 * @author HAMMA FATAKA
 */
public interface BaseLocker extends Locker {
    /**
     * this method adds lock to {@link AutoHandler} for using automatic locking before
     * executing function and releasing after execution is finished
     *
     * @return {@link AutoHandler}
     */

    default <T> AutoHandler<T> handler() {
        return AutoHandler.of(this);
    }

    /**
     * Obtain a lock, and return true if the lock was obtained, or false if the lock was not obtained.
     *
     * @return A boolean value.
     */
    boolean tryLock();

    /**
     * tries to obtain lock, retries to obtain until reaching timeout
     *
     * @param timeout non-negative time
     * @return result whether is locked or not
     * @throws InterruptedException if thread is interrupted
     */

    boolean obtainLock(final Duration timeout) throws InterruptedException;

    /**
     * Releases the lock
     */
    boolean releaseLock();

    /**
     * Returns true if the current thread holds the lock on this object.
     *
     * @return A boolean value that indicates whether the current thread holds this lock.
     */
    boolean isLockedByCurrentThread();

    /**
     * method to check if lock is locked
     *
     * @return result of locked
     */
    boolean isLocked();

    /**
     * method to check if lock is release
     *
     * @return result of release
     */
    default boolean isReleased() {
        return !isLocked();
    }
}
