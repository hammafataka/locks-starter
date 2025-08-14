package dev.mfataka.locks.core.locker.distibuted;

import java.time.Duration;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.DistributedLocker;
import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.api.factory.DistributedLock;
import dev.mfataka.locks.api.service.JdbcService;
import dev.mfataka.locks.api.utils.LoggingUtils;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 29.06.2022 13:29
 */
@Slf4j
public class SimpleDistributedLock implements DistributedLock {
    private final JdbcService jdbcService;
    @Getter
    private final LockContext lockContext;
    private final LoggingUtils loggingUtils;

    public static SimpleDistributedLock create(final JdbcService jdbcService, final LockContext lockContext) {
        return new SimpleDistributedLock(jdbcService, lockContext);
    }

    private SimpleDistributedLock(final JdbcService jdbcService, final LockContext context) {
        this.jdbcService = jdbcService;
        this.lockContext = context;
        this.loggingUtils = LoggingUtils.of(log, context.debugEnabled());
    }


    @Override
    public DistributedLocker get(final String lockName) {
        final var defaultJdbcLock = new JdbcLocker(lockName, jdbcService);
        loggingUtils.debugIfEnabled("lock [{}] is retrieving", lockName);
        return defaultJdbcLock;
    }

    @Override
    public void remove(final String lockName) {
        jdbcService.deleteLock(lockName);
    }

    @Override
    public boolean exists(final String lockName) {
        return jdbcService.isLocked(lockName);
    }

    @Override
    public int clearAllLocks(final Duration maxAge) {
        return (int) jdbcService.cleanExpiredLocks(maxAge);
    }

    @Override
    public int clearAllLocks() {
        return jdbcService.deleteAllLocks();
    }

    @Override
    public int existingLocksCount() {
        return jdbcService.existingLocksCount();
    }

    @Override
    public List<Locker> getLockers() {
        return jdbcService.findAll(LockMode.BLOCKING);
    }

    @Override
    public LockType getLockType() {
        return LockType.DISTRIBUTED;
    }

    @Override
    public LockMode getLockMode() {
        return LockMode.BLOCKING;
    }

}
