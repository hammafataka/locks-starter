package dev.mfataka.locks.core.utils;

import java.sql.Timestamp;

import lombok.experimental.UtilityClass;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 29.04.2025 11:15
 */
@UtilityClass
public class H2Utils {

    /**
     * This method returns the UNIX timestamp (seconds since 1970-01-01T00:00:00 UTC)
     */
    public static long unixTimestamp() {
        long millis = System.currentTimeMillis();
        return millis / 1000;
    }

    /**
     * Overload version to convert a Timestamp argument to UNIX timestamp
     */
    public static long unixTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return System.currentTimeMillis() / 1000;
        }
        return timestamp.getTime() / 1000;
    }
}
