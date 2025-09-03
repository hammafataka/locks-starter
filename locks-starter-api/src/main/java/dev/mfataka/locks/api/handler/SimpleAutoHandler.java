package dev.mfataka.locks.api.handler;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.concurrent.ThreadSafe;

import org.jetbrains.annotations.Blocking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.base.AutoHandler;
import dev.mfataka.locks.api.base.BaseLocker;


/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 */
@Blocking
@ThreadSafe
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleAutoHandler<T> implements AutoHandler<T> {

    private final BaseLocker lock;

    /**
     * when function is non-null, we run return-mode
     */
    private final Function<Boolean, ? extends T> mapper;
    /**
     * when consumer non-null, we run consumer-mode
     */
    private final Consumer<Boolean> consumer;

    /**
     * null => tryLock; otherwise obtainLock(timeout)
     */
    private final Duration timeout;

    /**
     * error mapping policy (map & rethrow)
     */
    private final Predicate<? super Throwable> mapPredicate;
    private final Function<? super Throwable, ? extends Throwable> errorMapper;

    /**
     * fallback policy (resume with value / swallow for consumer)
     */
    private final Predicate<? super Throwable> fbPredicate;
    private final Function<? super Throwable, ? extends T> fallback;

    private final Mode mode;

    public static <X> SimpleAutoHandler<X> of(final BaseLocker lock) {
        requireNonNull(lock, "lock");
        return new SimpleAutoHandler<>(lock, null, null, null, null, null, null, null, Mode.UNKNOWN);
    }


    @Override
    @SuppressWarnings("unchecked")
    public AutoHandler<Void> accept(final Consumer<Boolean> lockResultAndExecute) {
        requireNonNull(lockResultAndExecute, "lockResultAndExecute");
        checkArgument(this.mode.isMapper(), "cannot use accept() after using map() functions");
        return new SimpleAutoHandler<>(
                lock,
                null,
                lockResultAndExecute,
                null,
                mapPredicate,
                errorMapper,
                fbPredicate,
                (Function<? super Throwable, ? extends Void>) fallback,
                Mode.CONSUME
        );
    }


    @Override
    @SuppressWarnings("unchecked")
    public AutoHandler<Void> accept(final Duration timeout, final Consumer<Boolean> lockResultAndExecute) {
        requireNonNull(timeout, "timeout");
        requireNonNull(lockResultAndExecute, "lockResultAndExecute");
        checkArgument(this.mode.isMapper(), "cannot use accept() after using map() functions");
        return new SimpleAutoHandler<>(
                lock,
                null,
                lockResultAndExecute,
                timeout,
                mapPredicate, errorMapper,
                fbPredicate,
                (Function<? super Throwable, ? extends Void>) fallback,
                Mode.CONSUME
        );
    }

    @Override
    public AutoHandler<T> map(final Function<Boolean, T> lockResultAndExecute) {
        requireNonNull(lockResultAndExecute, "lockResultAndExecute");
        checkArgument(this.mode.isConsume(), "cannot use map() after using accept() functions");
        return new SimpleAutoHandler<>(
                lock,
                lockResultAndExecute,
                null,
                null,
                mapPredicate,
                errorMapper,
                fbPredicate,
                fallback,
                Mode.MAPPER
        );
    }

    @Override
    public AutoHandler<T> map(final Duration timeout, final Function<Boolean, T> lockResultAndExecute) {
        requireNonNull(timeout, "timeout");
        requireNonNull(lockResultAndExecute, "lockResultAndExecute");
        checkArgument(this.mode.isConsume(), "cannot use map() after using accept() functions");
        return new SimpleAutoHandler<>(
                lock,
                lockResultAndExecute,
                null,
                timeout,
                mapPredicate,
                errorMapper,
                fbPredicate,
                fallback,
                Mode.MAPPER
        );
    }


    @Override
    public AutoHandler<T> onErrorMap(final Predicate<? super Throwable> predicate, final Function<? super Throwable, ? extends Throwable> mapperFn) {
        requireNonNull(predicate, "predicate");
        requireNonNull(mapperFn, "mapper");
        return new SimpleAutoHandler<>(lock, mapper, consumer, timeout, predicate, mapperFn, fbPredicate, fallback, mode);
    }

    @Override
    public AutoHandler<T> onErrorResume(Function<? super Throwable, T> fallback) {
        return onErrorResume(t -> true, fallback);
    }

    @Override
    public AutoHandler<T> onErrorResume(final Predicate<? super Throwable> predicate, final Function<? super Throwable, T> fb) {
        requireNonNull(predicate, "predicate");
        requireNonNull(fb, "fallback");
        return onErrorReturn(predicate, fb);
    }

    @Override
    public AutoHandler<T> onErrorReturn(T fallback) {
        return onErrorReturn(t -> true, t -> fallback);
    }

    @Override
    public AutoHandler<T> onErrorReturn(final Predicate<? super Throwable> predicate, final Function<? super Throwable, ? extends T> fb) {
        requireNonNull(predicate, "predicate");
        requireNonNull(fb, "fallback");
        return new SimpleAutoHandler<>(lock, mapper, consumer, timeout, mapPredicate, errorMapper, predicate, fb, mode);
    }


    @Override
    public T block() {
        final var acquired = isAcquired();
        try {
            return switch (findMode()) {
                case CONSUME -> {
                    consumer.accept(acquired);
                    yield null;
                }
                case MAPPER -> mapper.apply(acquired);
                default -> null;
            };
        } catch (Throwable t) {
            return handleThrowable(t);
        } finally {
            if (acquired) {
                lock.releaseLock();
            }
        }
    }

    private boolean isAcquired() {
        return Objects.isNull(timeout) ? lock.tryLock() : obtainWithTimeout(timeout);
    }


    private boolean obtainWithTimeout(final Duration t) {
        try {
            return lock.obtainLock(t);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }

    private T handleThrowable(Throwable t) {
        if (mapPredicate != null && errorMapper != null && mapPredicate.test(t)) {
            return rethrow(errorMapper.apply(t));
        }
        if (fbPredicate != null && fallback != null && fbPredicate.test(t)) {
            return fallback.apply(t);
        }
        return rethrow(t);
    }

    private static <X> X rethrow(Throwable t) {
        if (t instanceof RuntimeException re) {
            throw re;
        }
        throw new RuntimeException(t);
    }

    private static void checkArgument(final boolean argument, final String message) {
        if (argument) throw new IllegalArgumentException(message);
    }


    private Mode findMode() {
        if (consumer != null) {
            return Mode.CONSUME;
        }
        if (mapper != null) {
            return Mode.MAPPER;
        }
        return Mode.UNKNOWN;
    }

    private enum Mode {
        CONSUME, MAPPER, UNKNOWN;

        public boolean isConsume() {
            return this == CONSUME;
        }

        public boolean isMapper() {
            return this == MAPPER;
        }
    }
}