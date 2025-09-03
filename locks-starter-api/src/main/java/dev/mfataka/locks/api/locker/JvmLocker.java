package dev.mfataka.locks.api.locker;

import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;

/**
 * @author HAMMA FATAKA
 */
public interface JvmLocker extends BaseLocker {


    @Override
    default LockMode getLockMode() {
        return LockMode.BLOCKING;
    }

    @Override
    default LockType getLockType() {
        return LockType.LOCAL;
    }
}
