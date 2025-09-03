package dev.mfataka.locks.core.service;

import java.time.Duration;

import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.utils.LoggingUtils;
import dev.mfataka.locks.core.config.LocksCorePropertyConfig;
import dev.mfataka.locks.core.descriptor.LockDescriptor;
import dev.mfataka.locks.core.factory.StandardReliableLocks;

/**
 * @author HAMMA FATAKA
 */
@Slf4j
@RequiredArgsConstructor
public class LockCleanerService {
    private final LocksCorePropertyConfig locksCorePropertyConfig;
    private final StandardReliableLocks standardReliableLocks;
    private final TaskScheduler threadPoolTaskScheduler;
    private LoggingUtils loggingUtils;


    @PostConstruct
    public void init() {
        final var trigger = new PeriodicTrigger(locksCorePropertyConfig.getCleanupInterval());
        trigger.setInitialDelay(Duration.ofMinutes(10));
        threadPoolTaskScheduler.schedule(this::cleanAllExpired, trigger);
        this.loggingUtils = LoggingUtils.of(log, locksCorePropertyConfig.isDebugEnabled());
    }


    public int cleanAll() {
        return standardReliableLocks.cleanAll();
    }

    public void cleanAllExpired() {
        final var cleaned = standardReliableLocks.cleanAll(locksCorePropertyConfig.getMaxAge());
        loggingUtils.debugIfEnabled("cleaned {} expired locks", cleaned);

    }

    public int clean(final LockDescriptor descriptor) {
        return standardReliableLocks.clean(descriptor);
    }
}
