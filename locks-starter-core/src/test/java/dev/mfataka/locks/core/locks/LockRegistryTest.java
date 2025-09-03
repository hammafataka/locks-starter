package dev.mfataka.locks.core.locks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.core.factory.LockRegistry;
import dev.mfataka.locks.core.service.SimpleJdbcService;

/**
 * @author HAMMA FATAKA
 */
public class LockRegistryTest {
    @BeforeEach
    void setUp() {
        LockRegistry.lockFactories.clear(); // Clean before each test
    }


    @Test
    public void getSimpleLock() {
        final var simpleLock = LockRegistry.simpleLock();
        Assertions.assertNotNull(simpleLock);
    }

    @Test
    public void getReactiveLock() {
        final var reactiveLock = LockRegistry.reactiveLock();
        Assertions.assertNotNull(reactiveLock);
    }

    @Test
    public void getDefaultJvmLock() {
        final var defaultJvmLock = LockRegistry.defaultJvmLock();
        Assertions.assertNotNull(defaultJvmLock);
    }


    @Test
    public void createDistributedLock() {
        final var context = new LockContext(getClass().getSimpleName(), true, true);
        final var jdbcService = new SimpleJdbcService(null, null);
        final var distributedLock = LockRegistry.createDistributedLock(jdbcService, context);
        Assertions.assertNotNull(distributedLock);
    }

    @Test
    public void createReactiveDistributedLock() {
        final var context = new LockContext(getClass().getSimpleName(), true, true);
        final var jdbcService = new SimpleJdbcService(null, null);
        final var reactiveDistributedLock = LockRegistry.crateReactiveDistributedLock(jdbcService, context);
        Assertions.assertNotNull(reactiveDistributedLock);
    }
}
