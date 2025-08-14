package dev.mfataka.locks.api;

import dev.mfataka.locks.api.base.ReactiveBaseLocker;
import dev.mfataka.locks.api.locker.JvmLocker;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 21.03.2023 11:31
 */
public interface ReactiveLocker extends ReactiveBaseLocker {

    JvmLocker getLock();

}
