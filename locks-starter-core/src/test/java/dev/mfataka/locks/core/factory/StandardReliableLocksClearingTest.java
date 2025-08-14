package dev.mfataka.locks.core.factory;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.SneakyThrows;

import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.service.JdbcService;
import dev.mfataka.locks.core.AbstractLockTest;
import dev.mfataka.locks.core.locker.distibuted.SimpleDistributedLock;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 29.04.2025 15:26
 */
public class StandardReliableLocksClearingTest extends AbstractLockTest {

    @Autowired
    private StandardReliableLocks reliableLocks;

    @Autowired
    private JdbcService jdbcService;


    @Test
    @SneakyThrows
    void cleanAllDuration() {
        Assertions.assertEquals(6, LockRegistry.existingLocks().size());

        Thread.sleep(5_000);
        final var distributedLock = SimpleDistributedLock.create(jdbcService, new LockContext("test", true, true));
        LockRegistry.lockFactories.put("test", distributedLock);
        Assertions.assertEquals(7, LockRegistry.existingLocks().size());
        final var cleaned = reliableLocks.cleanAll(Duration.ofSeconds(5));
        Assertions.assertEquals(6, cleaned);
        Assertions.assertEquals(1, LockRegistry.existingLocks().size());

    }

}
