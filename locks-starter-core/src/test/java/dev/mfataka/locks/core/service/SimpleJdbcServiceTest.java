package dev.mfataka.locks.core.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.core.AbstractLockTest;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 29.04.2025 14:27
 */
public class SimpleJdbcServiceTest extends AbstractLockTest {

    @Autowired
    private SimpleJdbcService jdbcService;

    private final String lockName = "test-lock";
    private final String ownerId = "test-owner";

    @BeforeEach
    void setUp() {
        jdbcService.deleteAllLocks(); // Clean before each test
    }

    @Test
    void testInsertLockAndIsLocked() {
        boolean inserted = jdbcService.insertLock(lockName, ownerId);
        assertTrue(inserted, "Lock should be inserted successfully");
        assertTrue(jdbcService.isLocked(lockName), "Lock should exist");
    }

    @Test
    void testInsertDuplicateLockFails() {
        jdbcService.insertLock(lockName, ownerId);
        boolean insertedAgain = jdbcService.insertLock(lockName, ownerId);
        assertFalse(insertedAgain, "Inserting the same lock should fail");
    }

    @Test
    void testIsLockedBy() {
        jdbcService.insertLock(lockName, ownerId);
        assertTrue(jdbcService.isLockedBy(lockName, ownerId), "Lock should belong to the owner");
        assertFalse(jdbcService.isLockedBy(lockName, "other-owner"), "Lock should not belong to different owner");
    }

    @Test
    void testDeleteLockByNameAndOwner() {
        jdbcService.insertLock(lockName, ownerId);
        jdbcService.deleteLock(lockName, ownerId);
        assertFalse(jdbcService.isLocked(lockName), "Lock should be deleted");
    }

    @Test
    void testDeleteLockByNameOnly() {
        jdbcService.insertLock(lockName, ownerId);
        jdbcService.deleteLock(lockName);
        assertFalse(jdbcService.isLocked(lockName), "Lock should be deleted");
    }

    @Test
    void testCleanExpiredLocks() throws InterruptedException {
        jdbcService.insertLock(lockName, ownerId);

        Thread.sleep(2000); // wait a bit to simulate expiration

        long deleted = jdbcService.cleanExpiredLocks(Duration.ofSeconds(1));
        assertEquals(1, deleted, "One lock should be expired and deleted");
    }

    @Test
    void testExistingLocksCount() {
        jdbcService.insertLock(lockName, ownerId);
        int count = jdbcService.existingLocksCount();
        assertEquals(1, count, "There should be 1 existing lock");
    }

    @Test
    void testDeleteAllLocks() {
        jdbcService.insertLock(lockName, ownerId);
        jdbcService.insertLock("another-lock", "another-owner");

        final var deleted = jdbcService.deleteAllLocks();
        assertTrue(deleted >= 0, "Delete all should succeed (H2 returns 0 if nothing)");
        final var remaining = jdbcService.existingLocksCount();
        assertEquals(0, remaining, "No locks should remain");
    }

    @Test
    void testElapsedTime() {
        jdbcService.insertLock(lockName, ownerId);
        final var timestamp = jdbcService.elapsedTime(lockName);
        assertTrue(timestamp > 0, "Timestamp should be set");
    }

    @Test
    void testFindAll() {
        final var inserted = jdbcService.insertLock(lockName, ownerId);
        Assertions.assertTrue(inserted, "Inserted lock should exist");

        final var lockers = jdbcService.findAll(LockMode.BLOCKING);
        assertFalse(lockers.isEmpty(), "Should find at least one locker");
        assertEquals(lockName, lockers.get(0).getLockName(), "Lock name should match");
    }
}
