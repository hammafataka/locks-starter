package dev.mfataka.locks.core.descriptor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;

/**
 * @author HAMMA FATAKA
 */
public record LockDescriptor(String name, long waitFor, TemporalUnit timeUnit, LockType lockType, LockMode lockMode) {

    public Duration getTimeout() {
        return Duration.of(waitFor(), timeUnit());
    }

    public static LockDescriptor forClean(final LockType lockType, final LockMode lockMode) {
        return new LockDescriptor("", 0, ChronoUnit.SECONDS, lockType, lockMode);
    }
}
