package dev.mfataka.locks.core.locker.reactive.distributed;

import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.DistributedLocker;
import dev.mfataka.locks.api.ReactiveDistributedLocker;
import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.api.utils.LoggingUtils;
import dev.mfataka.locks.core.locker.reactive.ReactiveLockerSupport;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 21.03.2023 11:44
 */
@Slf4j
public class DefaultReactiveDistributedLocker extends ReactiveLockerSupport implements ReactiveDistributedLocker {
    private final DistributedLocker distributedLocker;

    public static DefaultReactiveDistributedLocker of(final DistributedLocker locker, final boolean debugEnabled) {
        return new DefaultReactiveDistributedLocker(
                locker,
                LoggingUtils.of(log, debugEnabled)
        );
    }

    private DefaultReactiveDistributedLocker(final DistributedLocker locker, final LoggingUtils loggingUtils) {
        super(locker.getLockName(), loggingUtils);
        this.distributedLocker = locker;
    }


    @Override
    protected BaseLocker getBaseLock() {
        return distributedLocker;
    }

    @Override
    public String getLockName() {
        return lockName;
    }

    @Override
    public LockType getLockType() {
        return LockType.DISTRIBUTED;
    }

    @Override
    public long getLockElapsedTime() {
        return distributedLocker.getLockElapsedTime();
    }
}
