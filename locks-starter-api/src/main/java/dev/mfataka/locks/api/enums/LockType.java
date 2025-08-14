package dev.mfataka.locks.api.enums;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 14.04.2025 17:30
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
