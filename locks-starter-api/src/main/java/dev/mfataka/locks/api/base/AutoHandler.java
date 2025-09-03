package dev.mfataka.locks.api.base;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Blocking;

import dev.mfataka.locks.api.handler.SimpleAutoHandler;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * <p>
 * default implementation of {@link SimpleAutoHandler} is threadsafe
 * </P>
 */
@Blocking
public interface AutoHandler<T> {

    static <T> AutoHandler<T> of(final BaseLocker baseLock) {
        return SimpleAutoHandler.of(baseLock);
    }

    /**
     * automatically locks before consuming it and releases it after consuming it
     *
     * @param lockResultAndExecute consumer for result of lock
     */
    AutoHandler<Void> accept(final Consumer<Boolean> lockResultAndExecute);

    /**
     * automatically locks before consuming it and releases it after consuming it
     *
     * @param timeout              time to obtain lock
     * @param lockResultAndExecute consumer for result of lock
     */
    AutoHandler<Void> accept(final Duration timeout, final Consumer<Boolean> lockResultAndExecute) throws InterruptedException;

    /**
     * automatically locks before consuming it and releases it after consuming it, and return the functions return
     *
     * @param lockResultAndExecute consumer for result of lock
     */
    AutoHandler<T> map(final Function<Boolean, T> lockResultAndExecute);

    /**
     * automatically locks before consuming it and releases it after consuming it, and return the functions return
     *
     * @param timeout              time to obtain lock
     * @param lockResultAndExecute consumer for result of lock
     */

    AutoHandler<T> map(final Duration timeout, final Function<Boolean, T> lockResultAndExecute) throws InterruptedException;


    default <E> AutoHandler<T> onErrorMap(final Class<E> type, Function<? super Throwable, ? extends Throwable> mapper) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(mapper, "mapper");
        return onErrorMap(type::isInstance, mapper);
    }

    AutoHandler<T> onErrorMap(final Predicate<? super Throwable> predicate, Function<? super Throwable, ? extends Throwable> mapper);


    AutoHandler<T> onErrorResume(final Function<? super Throwable, T> fallback);

    AutoHandler<T> onErrorResume(final Predicate<? super Throwable> predicate, Function<? super Throwable, T> fallback);


    AutoHandler<T> onErrorReturn(T fallback);

    AutoHandler<T> onErrorReturn(Predicate<? super Throwable> predicate, Function<? super Throwable, ? extends T> fallback);

    default <E> AutoHandler<T> onErrorReturn(Class<E> type, Function<? super Throwable, ? extends T> fallback) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(fallback, "fallback");
        return onErrorReturn(type::isInstance, fallback);
    }


    T block();





    /* backward compatibility and deprecation    */


    /**
     * @deprecated use {@link #accept(Consumer)} and will be removed
     */
    @Deprecated(forRemoval = true)
    default AutoHandler<Void> then(final Consumer<Boolean> lockResultAndExecute) {
        return accept(lockResultAndExecute);
    }


    /**
     * @deprecated use {@link #accept(Duration, Consumer)} and will be removed
     */
    @Deprecated(forRemoval = true)
    default AutoHandler<Void> then(final Duration timeout, final Consumer<Boolean> lockResultAndExecute) throws InterruptedException {
        return accept(timeout, lockResultAndExecute);
    }

    /**
     * @deprecated use {@link #map(Function)} and will be removed
     */
    @Deprecated(forRemoval = true)
    default AutoHandler<T> thenReturn(final Function<Boolean, T> lockResultAndExecute) {
        return map(lockResultAndExecute);
    }

    /**
     * @deprecated use {@link #map(Duration, Function)} and will be removed
     */
    @Deprecated(forRemoval = true)
    default AutoHandler<T> thenReturn(final Duration timeout, final Function<Boolean, T> lockResultAndExecute) throws InterruptedException {
        return map(timeout, lockResultAndExecute);
    }


}
