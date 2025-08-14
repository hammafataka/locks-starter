package dev.mfataka.locks.api.factory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import dev.mfataka.locks.api.base.AutoHandler;
import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.handler.SimpleAutoHandler;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 02.08.2024 14:02
 */
public interface AutoHandlerFactory {
    Map<BaseLocker, AutoHandler> handlers = new ConcurrentHashMap<>();

    static AutoHandler getOrCreateHandler(final BaseLocker baseLock) {
        final var handler = handlers.get(baseLock);
        if (Objects.nonNull(handler)) {
            return handler;
        }
        final var createdLock = SimpleAutoHandler.of(baseLock);
        handlers.put(baseLock, createdLock);
        return createdLock;
    }


    static void removeHandler(final BaseLocker lock) {
        handlers.remove(lock);
    }

    static boolean exists(final BaseLocker key) {
        return handlers.containsKey(key);
    }

}
