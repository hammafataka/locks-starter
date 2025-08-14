package dev.mfataka.locks.core.locks;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.mfataka.locks.core.factory.LockRegistry;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 28.04.2025 11:11
 */
public class SimpleLockTest {


    @Test
    public void shouldLockAndRelease() {
        final var simpleLock = LockRegistry.simpleLock();
        final var locker = simpleLock.get("test");
        final var locked = locker.tryLock();
        Assertions.assertTrue(locked);
        Assertions.assertFalse(locker.isReleased());
        locker.releaseLock();
        Assertions.assertTrue(locker.isReleased());
        Assertions.assertFalse(locker.isLocked());
    }

    @Test
    public void shouldLockAndNotRelease() {
        final var failed = new AtomicBoolean(false);
        final var simpleLock = LockRegistry.simpleLock();
        final var locker = simpleLock.get("test");
        final var locked = locker.tryLock();
        Assertions.assertTrue(locked);

        new Thread(() -> {
            final var released = locker.releaseLock();
            if (released) {
                failed.set(true);
            }
        }).start();
        Assertions.assertFalse(failed.get());
        locker.releaseLock();
        Assertions.assertTrue(locker.isReleased());
    }


    @Test
    public void simpleLockHandler() {
        final var simpleLock = LockRegistry.simpleLock();
        final var locker = simpleLock.get("test");
        locker.handler()
                .then(locked -> {
                    Assertions.assertTrue(locked);
                    Assertions.assertFalse(locker.isReleased());
                });
        Assertions.assertTrue(locker.isReleased());
    }
}
