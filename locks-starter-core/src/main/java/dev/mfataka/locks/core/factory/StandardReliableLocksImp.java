package dev.mfataka.locks.core.factory;

import java.time.Duration;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.DistributedLocker;
import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.ReactiveDistributedLocker;
import dev.mfataka.locks.api.ReactiveLocker;
import dev.mfataka.locks.api.base.Lock;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.factory.DistributedLock;
import dev.mfataka.locks.api.factory.ReactiveDistributedLock;
import dev.mfataka.locks.api.service.JdbcService;
import dev.mfataka.locks.api.utils.LoggingUtils;
import dev.mfataka.locks.core.config.LocksCorePropertyConfig;
import dev.mfataka.locks.core.descriptor.LockDescriptor;
import dev.mfataka.locks.core.locker.simple.SimpleLocker;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 17.04.2025 13:48
 */
@Slf4j
public class StandardReliableLocksImp implements StandardReliableLocks {
    private final LockContext reactiveDistributedLockContext;
    private final LockContext distributedLockContext;
    private final JdbcService jdbcService;
    private final LoggingUtils loggingUtils;

    public StandardReliableLocksImp(final JdbcService jdbcService, final LocksCorePropertyConfig config) {
        this.jdbcService = jdbcService;
        this.distributedLockContext = buildContext(config, DistributedLock.class);
        this.reactiveDistributedLockContext = buildContext(config, ReactiveDistributedLock.class);
        this.loggingUtils = LoggingUtils.of(log, config.isDebugEnabled());
    }

    @Override
    public SimpleLocker createSimple(final String lockName) {
        final var simpleLocker = LockRegistry.simpleLock().get(lockName);
        loggingUtils.debugIfEnabled("Created simple lock {}", lockName);
        return simpleLocker;
    }

    @Override
    public ReactiveLocker createReactive(final String lockName) {
        final var reactiveLocker = LockRegistry.reactiveLock().get(lockName);
        loggingUtils.debugIfEnabled("Created reactive lock {}", lockName);
        return reactiveLocker;
    }

    @Override
    public DistributedLocker createDistributed(final String lockName) {
        final var distributedLocker = (DistributedLocker) getLockerFromFactories(false, lockName);
        loggingUtils.debugIfEnabled("Created distributed lock {}", lockName);
        return distributedLocker;
    }

    @Override
    public ReactiveDistributedLocker createReactiveDistributed(final String lockName) {
        final var reactiveDistributedLocker = (ReactiveDistributedLocker) getLockerFromFactories(true, lockName);
        loggingUtils.debugIfEnabled("Created reactive distributed lock {}", lockName);
        return reactiveDistributedLocker;
    }

    @Override
    public DistributedLock distributedLock() {
        final var distributedLock = LockRegistry.createDistributedLock(jdbcService, distributedLockContext);
        loggingUtils.debugIfEnabled("created Distributed lock {}", distributedLock);
        return distributedLock;
    }

    @Override
    public ReactiveDistributedLock reactiveDistributedLock() {
        final var reactiveDistributedLock = LockRegistry.crateReactiveDistributedLock(jdbcService, reactiveDistributedLockContext);
        loggingUtils.debugIfEnabled("created Reactive lock {}", reactiveDistributedLock);
        return reactiveDistributedLock;
    }


    @Override
    public int cleanAll() {
        final var values = getLockFactories().values();
        values.forEach(Lock::clearAllLocks);
        return values.size();
    }

    @Override
    public int cleanAll(final Duration maxAge) {
        final var values = getLockFactories()
                .values()
                .stream()
                .filter(getLockPredicate(maxAge))
                .toList();
        values.forEach(lock -> {
            lock.clearAllLocks(maxAge);
            getLockFactories().remove(lock.getLockContext().factoryName());
        });
        return values.size();
    }

    @Override
    public int clean(final LockDescriptor descriptor) {
        loggingUtils.debugIfEnabled("trying to clean lock {}", descriptor);
        final var lockFactoriesFor = findLockFactoriesFor(descriptor);
        lockFactoriesFor.forEach(Lock::clearAllLocks);
        return lockFactoriesFor.size();
    }


    private @NotNull Set<Lock<?>> findLockFactoriesFor(final LockDescriptor descriptor) {
        return getLockFactories().values()
                .stream()
                .filter(getLockPredicate(descriptor))
                .filter(lock -> lock.getLockContext().isCleanable())
                .collect(Collectors.toSet());
    }

    private static @NotNull Predicate<Lock<?>> getLockPredicate(final LockDescriptor descriptor) {
        final var mode = descriptor.lockMode();
        final var type = descriptor.lockType();
        return factory -> factory.getLockMode() == mode && factory.getLockType() == type;
    }

    private @NotNull Predicate<Lock<?>> getLockPredicate(final Duration maxAge) {
        return lock -> {
            final var context = lock.getLockContext();
            final var cleanable = context.isCleanable();
            final var isExpired = context.isExpired(maxAge);
            if (cleanable && isExpired) {
                loggingUtils.debugIfEnabled("cleaning lock {}", context.factoryName());
                return true;
            }
            return false;
        };
    }

    private Locker getLockerFromFactories(final boolean isReactive, final String lockName) {
        final var factoryName = isReactive ? reactiveDistributedLockContext.factoryName() : distributedLockContext.factoryName();
        return getLockFactories()
                .getOrDefault(factoryName, buildLock(isReactive).get())
                .get(lockName);
    }

    private Supplier<Lock<?>> buildLock(boolean isReactive) {
        return () -> isReactive ? reactiveDistributedLock() : distributedLock();
    }

    private static @NotNull LockContext buildContext(final LocksCorePropertyConfig config, Class<?> lockClass) {
        return new LockContext(STANDARD_PREFIX + lockClass.getSimpleName(), true, config.isDebugEnabled());
    }

}
