package dev.mfataka.locks.api.handler;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.base.AutoHandler;
import dev.mfataka.locks.api.base.BaseLocker;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 27.03.2023 22:26
 */
@RequiredArgsConstructor(staticName = "of")
public class SimpleAutoHandler implements AutoHandler {
    private final BaseLocker lock;
    private Predicate<? super Throwable> predicate;
    private Function<? super Throwable, Object> fallback;

    @Override
    public void then(final Consumer<Boolean> locked) {
        final var obtainLock = lock.tryLock();
        if (obtainLock) {
            executeThen(locked);
        } else {
            locked.accept(false);
        }
    }

    @Override
    public void then(final Duration timeout, final Consumer<Boolean> lockResultAndExecute) throws InterruptedException {
        final var obtainLock = lock.obtainLock(timeout);
        if (obtainLock) {
            executeThen(lockResultAndExecute);
        } else {
            lockResultAndExecute.accept(false);
        }
    }

    @Override
    public <T> T thenReturn(final Function<Boolean, T> lockResultAndExecute) {
        final var locked = lock.tryLock();
        if (locked) {
            return getT(lockResultAndExecute);
        } else {
            return lockResultAndExecute.apply(false);
        }

    }

    @Override
    public <T> T thenReturn(final Duration timeout, final Function<Boolean, T> lockResultAndExecute) throws InterruptedException {
        final var locked = lock.obtainLock(timeout);
        if (locked) {
            return getT(lockResultAndExecute);
        } else {
            return lockResultAndExecute.apply(false);
        }
    }

    @Override
    public <T> AutoHandler onErrorResume(Predicate<? super Throwable> predicate, Function<? super Throwable, T> fallback) {
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(fallback, "fallback");

        this.predicate = predicate;
        this.fallback = (Function<? super Throwable, Object>) fallback;
        return this;
    }

    private <T> @Nullable T getT(Function<Boolean, T> lockResultAndExecute) {
        try {
            return lockResultAndExecute.apply(true);
        } catch (Throwable e) {
            return onErrorReturn(e);
        } finally {
            lock.releaseLock();
        }
    }

    private void executeThen(final Consumer<Boolean> locked) {
        try {
            locked.accept(true);
        } catch (Throwable e) {
            onError(e);
        } finally {
            lock.releaseLock();
        }
    }

    private void onError(final Throwable throwable) {
        if (predicate != null && fallback != null) {
            final var test = predicate.test(throwable);
            if (test) {
                fallback.apply(throwable);
                return;
            }
        }
        reThrowError(throwable);
    }

    @Nullable
    private <T> T onErrorReturn(final Throwable throwable) {
        if (predicate != null && fallback != null) {
            final var test = predicate.test(throwable);
            return test ? (T) fallback.apply(throwable) : reThrowError(throwable);
        }
        if (fallback != null) {
            return (T) fallback.apply(throwable);
        }
        return reThrowError(throwable);
    }

    private static <T> T reThrowError(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }
        throw new RuntimeException(throwable);
    }


}
