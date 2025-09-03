package dev.mfataka.locks.core.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.base.Lock;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.factory.DistributedLock;
import dev.mfataka.locks.api.factory.ReactiveDistributedLock;
import dev.mfataka.locks.api.service.JdbcService;
import dev.mfataka.locks.core.locker.distibuted.SimpleDistributedLock;
import dev.mfataka.locks.core.locker.reactive.ReactiveLock;
import dev.mfataka.locks.core.locker.reactive.distributed.DefaultReactiveDistributedLock;
import dev.mfataka.locks.core.locker.simple.SimpleJvmLock;
import dev.mfataka.locks.core.locker.simple.SimpleLock;

/**
 * @author HAMMA FATAKA
 */
public interface LockRegistry {


    String STANDARD_PREFIX = "STANDARD_";

    ReactiveLock REACTIVE_LOCK_FACTORY = ReactiveLock.create(standardLockContextFor(ReactiveLock.class));

    SimpleLock SIMPLE_LOCK_FACTORY = SimpleLock.create(standardLockContextFor(SimpleLock.class));

    SimpleJvmLock DEFAULT_JVM_LOCK_FACTORY = SimpleJvmLock.create(standardLockContextFor(SimpleJvmLock.class));

    Map<String, Lock<?>> lockFactories = Stream.of(REACTIVE_LOCK_FACTORY, SIMPLE_LOCK_FACTORY, DEFAULT_JVM_LOCK_FACTORY)
            .collect(Collectors.toConcurrentMap(lock -> lock.getLockContext().factoryName(), Function.identity()));

    DistributedLock distributedLock();

    ReactiveDistributedLock reactiveDistributedLock();

    default Map<String, Lock<?>> getLockFactories() {
        return lockFactories;
    }

    static ReactiveLock reactiveLock() {
        return REACTIVE_LOCK_FACTORY;
    }

    static SimpleLock simpleLock() {
        return SIMPLE_LOCK_FACTORY;
    }

    static SimpleJvmLock defaultJvmLock() {
        return DEFAULT_JVM_LOCK_FACTORY;
    }

    static DistributedLock createDistributedLock(final JdbcService jdbcService, final LockContext lockContext) {
        return (DistributedLock) lockFactories.computeIfAbsent(lockContext.factoryName(), key -> SimpleDistributedLock.create(jdbcService, lockContext));
    }

    static ReactiveDistributedLock crateReactiveDistributedLock(final JdbcService jdbcService, final LockContext lockContext) {
        final var lockContextForDelegateFactory = new LockContext(lockContext.factoryName() + "_reactive", lockContext.isCleanable(), lockContext.debugEnabled());
        final var distributedLockFactory = createDistributedLock(jdbcService, lockContextForDelegateFactory);
        return (ReactiveDistributedLock) lockFactories.computeIfAbsent(lockContext.factoryName(), key -> DefaultReactiveDistributedLock.of(distributedLockFactory, lockContext));
    }


    static Locker findLocker(final String name) {
        return existingLockers()
                .stream()
                .filter(context -> context.getLockName().equals(name))
                .findAny()
                .orElse(null);
    }

    static Lock<?> findLock(final String name) {
        return lockFactories.get(name);
    }

    static boolean lockerExists(final String lockerName) {
        return existingLockers()
                .stream()
                .anyMatch(locker -> locker.getLockName().equals(lockerName));
    }

    static boolean lockExists(final String lockName) {
        return lockFactories.containsKey(lockName);
    }

    static List<LockContext> existingLocks() {
        return lockFactories.values()
                .stream()
                .map(Lock::getLockContext)
                .toList();
    }

    static List<Locker> existingLockers() {
        return lockFactories.values()
                .stream()
                .flatMap(lock -> lock.getLockers().stream())
                .toList();
    }


    private static LockContext standardLockContextFor(final Class<?> lockRegistryClass) {
        return new LockContext(STANDARD_PREFIX + lockRegistryClass.getSimpleName(), true, true);
    }

}
