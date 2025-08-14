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


/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 13.09.2023 15:04
 */
public class SimpleJvmLock implements Lock<SimpleJvmLocker> {
    private final Map<String, SimpleJvmLocker> jvmLocks = new ConcurrentHashMap<>();

    @Getter
    private final LockContext lockContext;

    private SimpleJvmLock(final LockContext lockContext) {
        this.lockContext = lockContext;
    }

    public static SimpleJvmLock create(final LockContext factoryContext) {
        return new SimpleJvmLock(factoryContext);
    }


    @Override
    public SimpleJvmLocker get(final String lockName) {
        final var defaultLock = jvmLocks.get(lockName);
        if (Objects.nonNull(defaultLock)) {
            return defaultLock;
        }
        final var createdLock = new SimpleJvmLocker(lockName);
        jvmLocks.put(lockName, createdLock);
        return createdLock;
    }

    @Override
    public void remove(final String lockName) {
        jvmLocks.remove(lockName);
    }

    public boolean exists(final String key) {
        return jvmLocks.containsKey(key);
    }

    @Override
    public int clearAllLocks(final Duration maxAge) {
        final var list = jvmLocks.values()
                .stream()
                .filter(Locker.expiredPredicate(maxAge))
                .toList();
        list.forEach(locker -> jvmLocks.remove(locker.getLockName()));
        return list.size();
    }

    @Override
    public int clearAllLocks() {
        return jvmLocks.values()
                .stream()
                .filter(SimpleJvmLocker::isReleased)
                .mapToInt(lock -> {
                    jvmLocks.remove(lock.getLockName());
                    return 1;
                })
                .sum();
    }

    @Override
    public int existingLocksCount() {
        return jvmLocks.size();
    }

    @Override
    public Collection<Locker> getLockers() {
        return jvmLocks.values()
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
