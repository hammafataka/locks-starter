package dev.mfataka.locks.api.factory;

import dev.mfataka.locks.api.DistributedLocker;
import dev.mfataka.locks.api.base.Lock;

/**
 * @author HAMMA FATAKA
 * <P>This is interface for using distributed locks by using jdbc connection,
 * in order to use this service distributed_locks table need to be present in db
 * and `distributed.lock.enabled` needs to be set true in application properties
 * </P>
 */
public interface DistributedLock extends Lock<DistributedLocker> {

}

