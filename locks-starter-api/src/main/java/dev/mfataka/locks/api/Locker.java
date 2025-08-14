package dev.mfataka.locks.api;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 02.08.2024 13:05
 */
public interface Locker {

    String getLockName();

    LockMode getLockMode();

    LockType getLockType();

    long getLockElapsedTime();


    static @NotNull Predicate<Locker> expiredPredicate(final Duration maxAge) {
        return locker -> locker.getLockElapsedTime() < Instant.now().getEpochSecond() - maxAge.getSeconds();
    }


    static Locker jdbcLocker(final String lockName, final long lockElapsedTime, final LockMode lockMode) {
        return new Locker() {
            @Override
            public String getLockName() {
                return lockName;
            }

            @Override
            public LockMode getLockMode() {
                return lockMode;
            }

            @Override
            public LockType getLockType() {
                return LockType.DISTRIBUTED;
            }

            @Override
            public long getLockElapsedTime() {
                return lockElapsedTime;
            }
        };
    }
}
