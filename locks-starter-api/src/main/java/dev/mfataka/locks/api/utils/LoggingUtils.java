package dev.mfataka.locks.api.utils;

import org.slf4j.Logger;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 25.11.2022 13:41
 */
public final class LoggingUtils {
    public static final String lockMessageLog = "locking with try lock, lockResult [{}]";
    public static final String asyncCancelReleaseLog = "mono triggered AsyncCancel, releasing lock, releaseResult [{}]";
    public static final String asyncDebugLog = "lock with name [{}] is releasing because of mono Async cancel trigger";
    public static final String errorDebugMessageLog = "lock with name [{}] releasing because of error occurrence, error message [{}]";
    public static final String errorInfoMessageLog = "error occurred releasing lock, releaseResult [{}]";
    public static final String releaseDebugMessageLog = "lock with name [{}] releasing normally, release result [{}]";
    public static final String releaseInfoMessageLog = "releasing lock normally, releaseResult [{}]";
    public static final String repeatDebugMessageLog = "repeating acquire lock, repeat context [{}]";
    public static final String durationDebugMessageLog = "locking lock [{}], with duration [{}]";
    public static final String graceFullLockMessage = "lock with name [{}], trying to lock gracefully";


    private final Logger logger;
    private boolean debugEnabled = false;

    public static LoggingUtils of(final Logger logger, final boolean debugEnabled) {
        return new LoggingUtils(logger, debugEnabled);
    }

    public LoggingUtils(final Logger logger) {
        this.logger = logger;
    }

    LoggingUtils(final Logger logger, final boolean debugEnabled) {
        this.logger = logger;
        this.debugEnabled = debugEnabled;
    }

    public void debugIfEnabled(final boolean isEnabled, final String message, final Object... args) {
        if (isEnabled) {
            logger.debug(message, args);
        }
    }

    public void debugIfEnabled(final String message, final Object... args) {
        debugIfEnabled(debugEnabled, message, args);
    }
}
