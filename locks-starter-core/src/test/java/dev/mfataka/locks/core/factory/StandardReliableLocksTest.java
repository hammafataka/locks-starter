package dev.mfataka.locks.core.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.mfataka.locks.api.service.JdbcService;
import dev.mfataka.locks.core.AbstractLockTest;
import dev.mfataka.locks.core.config.LocksCorePropertyConfig;

/**
 * @author HAMMA FATAKA
 */
public class StandardReliableLocksTest extends AbstractLockTest {

    private final StandardReliableLocks reliableLocks;

    @Autowired
    public StandardReliableLocksTest(final JdbcService jdbcService, final LocksCorePropertyConfig config) {
        this.reliableLocks = new StandardReliableLocksImp(jdbcService, config);
    }


    @Test
    void createSimple() {
        final var simple = reliableLocks.createSimple("simple");
        Assertions.assertNotNull(simple);
    }

    @Test
    void createReactive() {
        final var reactiveLocker = reliableLocks.createReactive("createReactive");
        Assertions.assertNotNull(reactiveLocker);
    }

    @Test
    void createDistributed() {
        final var distributedLocker = reliableLocks.createDistributed("createDistributed");
        Assertions.assertNotNull(distributedLocker);
    }

    @Test
    void createReactiveDistributed() {
        final var reactiveDistributedLocker = reliableLocks.createReactiveDistributed("createReactiveDistributed");
        Assertions.assertNotNull(reactiveDistributedLocker);
    }

    @Test
    void distributedLock() {
        final var distributedLock = reliableLocks.distributedLock();
        Assertions.assertNotNull(distributedLock);

        final var lockExists = LockRegistry.lockExists(distributedLock.getLockContext().factoryName());
        Assertions.assertTrue(lockExists);

    }

    @Test
    void reactiveDistributedLock() {
        final var reactiveDistributedLock = reliableLocks.reactiveDistributedLock();
        Assertions.assertNotNull(reactiveDistributedLock);
        final var lockExists = LockRegistry.lockExists(reactiveDistributedLock.getLockContext().factoryName());
        Assertions.assertTrue(lockExists);
    }
}
