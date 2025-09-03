package dev.mfataka.locks.api;

import dev.mfataka.locks.api.base.ReactiveBaseLocker;
import dev.mfataka.locks.api.locker.JvmLocker;

/**
 * @author HAMMA FATAKA
 */
public interface ReactiveLocker extends ReactiveBaseLocker {

    JvmLocker getLock();

}
