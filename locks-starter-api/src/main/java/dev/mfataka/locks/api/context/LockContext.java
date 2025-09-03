package dev.mfataka.locks.api.context;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author HAMMA FATAKA
 */
@Slf4j
public record LockContext(String factoryName,
                          boolean isCleanable,
                          boolean debugEnabled,
                          long lockAge) {

    public LockContext(String factoryName, boolean isCleanable, boolean debugEnabled) {
        this(factoryName, isCleanable, debugEnabled, System.currentTimeMillis() / 1000L);
    }

    public boolean isExpired(final Duration maxAge) {
        return lockAge < System.currentTimeMillis() / 1000L - maxAge.getSeconds();
    }
}
