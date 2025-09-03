package dev.mfataka.locks.core.factory;

import java.time.Duration;

import dev.mfataka.locks.api.DistributedLocker;
import dev.mfataka.locks.api.ReactiveDistributedLocker;
import dev.mfataka.locks.api.ReactiveLocker;
import dev.mfataka.locks.core.descriptor.LockDescriptor;
import dev.mfataka.locks.core.locker.simple.SimpleLocker;

/**
 * @author HAMMA FATAKA
 */
public interface StandardReliableLocks extends LockRegistry {

    SimpleLocker createSimple(final String lockName);

    ReactiveLocker createReactive(final String lockName);


    DistributedLocker createDistributed(final String lockName);

    ReactiveDistributedLocker createReactiveDistributed(final String lockName);

    int clean(final LockDescriptor descriptor);

    int cleanAll(Duration maxAge);

    int cleanAll();
}
