package dev.mfataka.locks.api.enums;

/**
 * @author HAMMA FATAKA
 */
public enum LockType {
    LOCAL, DISTRIBUTED;


    public boolean isLocal() {
        return this == LOCAL;
    }

    public boolean isDistributed() {
        return this == DISTRIBUTED;
    }
}
