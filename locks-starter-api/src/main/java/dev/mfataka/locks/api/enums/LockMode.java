package dev.mfataka.locks.api.enums;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 14.04.2025 17:30
 */
public enum LockMode {
    BLOCKING, REACTIVE;


    public boolean isBlocking() {
        return this == BLOCKING;
    }

    public boolean isReactive() {
        return this == REACTIVE;
    }
}
