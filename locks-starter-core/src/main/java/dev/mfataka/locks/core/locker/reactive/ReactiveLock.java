package dev.mfataka.locks.core.locker.reactive;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.ReactiveLocker;
import dev.mfataka.locks.api.base.Lock;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.api.factory.AutoHandlerFactory;
import dev.mfataka.locks.api.locker.JvmLocker;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 20.03.2023 15:19
 */
public class ReactiveLock implements Lock<ReactiveLocker> {
    public static final String DEFAULT_LOCK_KEY = "DEFAULT_REACTIVE_LOCK";

    private final Map<String, ReactiveLocker> reactiveLockers = new ConcurrentHashMap<>();
    @Getter
    private final LockContext lockContext;

    private ReactiveLock(final LockContext lockContext) {
        this.lockContext = lockContext;
    }


    public static ReactiveLock create(final LockContext context) {
        return new ReactiveLock(context);
    }

    @Override
    public ReactiveLocker get(final String lockName) {
        return getOrCreateLock(lockName, false);
    }

    public ReactiveLocker getOrCreateLock(boolean debugEnabled) {
        return getOrCreateLock(DEFAULT_LOCK_KEY, debugEnabled);

    }

    public ReactiveLocker getOrCreateLock() {
        return getOrCreateLock(DEFAULT_LOCK_KEY, false);
    }

    public ReactiveLocker getOrCreateLock(final String name, boolean debugEnabled) {
        final var lock = reactiveLockers.get(name);
        if (Objects.nonNull(lock)) {
            return lock;
        }
        return createDefaultLock(name, debugEnabled);
    }

    private ReactiveLocker createDefaultLock(final String name, final boolean debugEnabled) {
        final var reactiveLock = DefaultReactiveLocker.of(name, debugEnabled);
        reactiveLockers.put(name, reactiveLock);
        return reactiveLock;
    }

    public void remove(final String key) {
        final var reactiveLock = reactiveLockers.get(key);
        if (Objects.isNull(reactiveLock)) {
            return;
        }
        final var lock = reactiveLock.getLock();
        AutoHandlerFactory.removeHandler(lock);
        reactiveLockers.remove(key);
    }

    public boolean exists(final String key) {
        return reactiveLockers.containsKey(key);
    }

    @Override
    public int clearAllLocks(final Duration maxAge) {
        final var list = reactiveLockers.values()
                .stream()
                .filter(Locker.expiredPredicate(maxAge))
                .toList();
        list.forEach(locker -> reactiveLockers.remove(locker.getLockName()));
        return list.size();

    }

    @Override
    public int clearAllLocks() {
        final var allReleasedLocks = reactiveLockers.values()
                .stream()
                .map(ReactiveLocker::getLock)
                .filter(JvmLocker::isReleased)
                .toList();

        allReleasedLocks.forEach(lock -> reactiveLockers.remove(lock.getLockName()));
        return allReleasedLocks.size();
    }

    @Override
    public int existingLocksCount() {
        return reactiveLockers.size();
    }

    @Override
    public Collection<Locker> getLockers() {
        return reactiveLockers.values()
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
        return LockMode.REACTIVE;
    }

}
