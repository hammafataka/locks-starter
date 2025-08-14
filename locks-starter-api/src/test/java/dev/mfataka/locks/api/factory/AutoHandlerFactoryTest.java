package dev.mfataka.locks.api.factory;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 29.04.2025 13:14
 */
public class AutoHandlerFactoryTest {


    @Test
    public void shouldVerifyAllFunctions() {
        final var baseLocker = getBaseLocker();
        final var autoHandler = AutoHandlerFactory.getOrCreateHandler(baseLocker);
        Assertions.assertNotNull(autoHandler);
        final var exists = AutoHandlerFactory.exists(baseLocker);
        Assertions.assertTrue(exists);
        AutoHandlerFactory.removeHandler(baseLocker);
        Assertions.assertFalse(AutoHandlerFactory.exists(baseLocker));
    }


    private BaseLocker getBaseLocker() {
        return new BaseLocker() {
            @Override
            public boolean tryLock() {
                return false;
            }

            @Override
            public boolean obtainLock(Duration timeout) throws InterruptedException {
                return false;
            }

            @Override
            public boolean releaseLock() {
                return false;
            }

            @Override
            public boolean isLockedByCurrentThread() {
                return false;
            }

            @Override
            public boolean isLocked() {
                return false;
            }

            @Override
            public String getLockName() {
                return "";
            }

            @Override
            public LockMode getLockMode() {
                return null;
            }

            @Override
            public LockType getLockType() {
                return null;
            }

            @Override
            public long getLockElapsedTime() {
                return 0;
            }
        };
    }


}
