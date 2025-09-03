package dev.mfataka.locks.core.locker.distibuted;

import java.time.Duration;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.DistributedLocker;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.api.service.JdbcService;

/**
 * @author HAMMA FATAKA
 */
@Slf4j
public class JdbcLocker implements DistributedLocker {
    private final JdbcService jdbcService;
    private final String lockName;
    private final String ownerId;

    JdbcLocker(final String name, final JdbcService jdbcService) {
        this.lockName = name;
        this.jdbcService = jdbcService;
        this.ownerId = UUID.randomUUID().toString(); // unique ID per lock instance
    }

    @Override
    public boolean tryLock() {
        return jdbcService.insertLock(lockName, ownerId);
    }

    @Override
    public boolean obtainLock(final Duration waitTimeout) {
        final var deadline = System.currentTimeMillis() + waitTimeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (tryLock()) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }


    @Override
    public boolean isLocked() {
        return jdbcService.isLocked(lockName);

    }

    @Override
    public long getLockElapsedTime() {
        return jdbcService.elapsedTime(lockName);
    }

    @Override
    public boolean isReleased() {
        return !isLocked();
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return jdbcService.isLockedBy(lockName, ownerId);

    }

    @Override
    public boolean releaseLock() {
        jdbcService.deleteLock(lockName, ownerId);
        return true;
    }

    @Override
    public String getLockName() {
        return lockName;
    }

    @Override
    public LockMode getLockMode() {
        return LockMode.BLOCKING;
    }

    @Override
    public LockType getLockType() {
        return LockType.DISTRIBUTED;
    }

}
