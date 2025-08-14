package dev.mfataka.locks.api.service;

import java.time.Duration;
import java.util.List;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.exception.LockOperationException;

/**
 * Interface defining the contract for JDBC-based distributed lock operations.
 * Provides methods to create, check, delete, and clean up locks stored in a relational database.
 *
 * @author HAMMA FATAKA
 * @project locks-starter
 * @since 09.04.2025
 */
public interface JdbcService {

    /**
     * Ensures that the required lock table exists in the database.
     * Typically called during application startup.
     *
     * @throws LockOperationException if the table creation fails
     */
    void ensureLockTableExists() throws LockOperationException;

    /**
     * Attempts to acquire a lock by inserting a new row into the lock table.
     *
     * @param name    the name of the lock
     * @param ownerId the identifier of the lock owner
     * @return true if the lock was acquired successfully, false if it already exists
     */
    boolean insertLock(final String name, final String ownerId);

    /**
     * Checks if a lock with the given name exists.
     *
     * @param name the name of the lock
     * @return true if the lock is present, false otherwise
     */
    boolean isLocked(final String name);

    /**
     * Checks if the lock with the given name is held by the specified owner.
     *
     * @param name    the name of the lock
     * @param ownerId the identifier of the lock owner
     * @return true if the lock is held by the owner, false otherwise
     */
    boolean isLockedBy(final String name, final String ownerId);

    /**
     * Deletes a lock if it is held by the specified owner.
     *
     * @param name    the name of the lock
     * @param ownerId the identifier of the lock owner
     */
    void deleteLock(final String name, final String ownerId);

    /**
     * Deletes a lock regardless of the owner.
     *
     * @param name the name of the lock
     */
    void deleteLock(final String name);

    /**
     * Removes all locks that have expired, based on the provided maximum age.
     *
     * @param maxAge the maximum allowed age for locks
     */
    long cleanExpiredLocks(final Duration maxAge);

    /**
     * Returns the number of currently existing locks.
     *
     * @return the total count of locks in the table
     */
    int existingLocksCount();

    /**
     * Deletes all locks in the table.
     *
     * @return the number of deleted locks
     */
    int deleteAllLocks();


    long elapsedTime(final String lockName);

    List<Locker> findAll(final LockMode lockMode);
}