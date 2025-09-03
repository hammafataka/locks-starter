package dev.mfataka.locks.api.enums;

/**
 * @author HAMMA FATAKA
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
