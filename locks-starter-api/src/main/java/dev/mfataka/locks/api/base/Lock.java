package dev.mfataka.locks.api.base;

import java.time.Duration;
import java.util.Collection;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;

/**
 * LockFactory is an interface that provides methods for creating and managing locks.
 * It serves as a factory for different types of locks.
 *
 * @param <T> the type of lock to be created
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 02.08.2024 13:12
 */
public interface Lock<T extends Locker> {

    /**
     * Creates a new lock with the specified name.
     *
     * @param lockName the name of the lock
     * @return a new lock instance
     */
    T get(final String lockName);

    /**
     * Removes the lock with the specified name.
     *
     * @param lockName the name of the lock
     */

    void remove(final String lockName);

    /**
     * Checks if a lock with the specified name exists.
     *
     * @param lockName the name of the lock
     * @return true if the lock exists, false otherwise
     */
    boolean exists(final String lockName);


    int clearAllLocks(final Duration maxAge);

    int clearAllLocks();

    int existingLocksCount();

    Collection<Locker> getLockers();


    LockType getLockType();

    LockMode getLockMode();

    LockContext getLockContext();


}
