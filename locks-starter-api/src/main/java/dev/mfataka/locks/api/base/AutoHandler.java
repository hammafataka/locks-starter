package dev.mfataka.locks.api.base;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import dev.mfataka.locks.api.factory.AutoHandlerFactory;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 27.03.2023 22:10
 */
public interface AutoHandler {

    static AutoHandler of(final BaseLocker baseLock) {
        return AutoHandlerFactory.getOrCreateHandler(baseLock);
    }

    /**
     * automatically locks before consuming it and releases it after consuming it
     *
     * @param lockResultAndExecute consumer for result of lock
     */
    void then(final Consumer<Boolean> lockResultAndExecute);

    /**
     * automatically locks before consuming it and releases it after consuming it
     *
     * @param timeout              time to obtain lock
     * @param lockResultAndExecute consumer for result of lock
     */
    void then(final Duration timeout, final Consumer<Boolean> lockResultAndExecute) throws InterruptedException;

    /**
     * automatically locks before consuming it and releases it after consuming it, and return the functions return
     *
     * @param lockResultAndExecute consumer for result of lock
     */
    <T> T thenReturn(final Function<Boolean, T> lockResultAndExecute);

    /**
     * automatically locks before consuming it and releases it after consuming it, and return the functions return
     *
     * @param timeout              time to obtain lock
     * @param lockResultAndExecute consumer for result of lock
     */

    <T> T thenReturn(final Duration timeout, final Function<Boolean, T> lockResultAndExecute) throws InterruptedException;


    default <E> AutoHandler errorMap(final Class<E> type, Function<? super Throwable, ? extends Throwable> mapper) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(mapper, "mapper");
        return errorMap(type::isInstance, mapper);
    }

    default AutoHandler errorMap(final Predicate<? super Throwable> predicate, Function<? super Throwable, ? extends Throwable> mapper) {
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(mapper, "mapper");
        return onErrorResume(predicate, mapper);
    }


    <T> AutoHandler onErrorResume(final Predicate<? super Throwable> predicate, Function<? super Throwable, T> fallback);

}
