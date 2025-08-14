package dev.mfataka.locks.core.locker.simple;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.base.Lock;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.api.factory.AutoHandlerFactory;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 02.08.2024 13:13
 */
public class SimpleLock implements Lock<SimpleLocker> {

    private final Map<String, SimpleLocker> defaultLocks = new ConcurrentHashMap<>();
    @Getter
    private final LockContext lockContext;

    private SimpleLock(final LockContext lockContext) {
        this.lockContext = lockContext;
    }

    public static SimpleLock create(final LockContext factoryContext) {
        return new SimpleLock(factoryContext);
    }


    @Override
    public SimpleLocker get(final String lockName) {
        final var defaultLock = defaultLocks.get(lockName);
        if (Objects.nonNull(defaultLock)) {
            return defaultLock;
        }
        final var createdLock = new SimpleLocker(lockName);
        defaultLocks.put(lockName, createdLock);
        return createdLock;
    }

    @Override
    public void remove(final String lockName) {
        final var simpleLocker = defaultLocks.get(lockName);
        if (Objects.nonNull(simpleLocker)) {
            AutoHandlerFactory.removeHandler(simpleLocker);
        }
        defaultLocks.remove(lockName);
    }

    @Override
    public boolean exists(final String lockName) {
        return defaultLocks.containsKey(lockName);
    }

    @Override
    public int clearAllLocks(final Duration maxAge) {
        final var list = defaultLocks.values()
                .stream()
                .filter(Locker.expiredPredicate(maxAge))
                .toList();
        list.forEach(locker -> defaultLocks.remove(locker.getLockName()));
        return list.size();
    }

    @Override
    public int clearAllLocks() {
        return defaultLocks.values()
                .stream()
                .filter(SimpleLocker::isReleased)
                .mapToInt(lock -> {
                    defaultLocks.remove(lock.getLockName());
                    return 1;
                })
                .sum();
    }

    @Override
    public int existingLocksCount() {
        return defaultLocks.size();
    }

    @Override
    public Collection<Locker> getLockers() {
        return defaultLocks.values()
                .stream()
                .map(r -> (Locker) r)
                .toList();
    }

    @Override
    public LockType getLockType() {
        return LockType.LOCAL;
    }

    @Override
    public LockMode getLockMode() {
        return LockMode.BLOCKING;
    }
}
