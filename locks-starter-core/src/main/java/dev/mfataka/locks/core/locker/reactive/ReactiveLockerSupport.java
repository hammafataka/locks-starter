package dev.mfataka.locks.core.locker.reactive;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;
import reactor.util.context.Context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.base.BaseLocker;
import dev.mfataka.locks.api.base.ReactiveBaseLocker;
import dev.mfataka.locks.api.utils.LoggingUtils;

/**
 * @author HAMMA FATAKA
 */
@Slf4j
@RequiredArgsConstructor
public abstract class ReactiveLockerSupport implements ReactiveBaseLocker {
    protected final String lockName;
    private final LoggingUtils loggingUtils;

    @Override
    public <T> Mono<T> tryLock(@NotNull Function<Boolean, T> function) {
        return runFunctionWithMono(null, function)
                .contextWrite(this::onContextWrite);
    }

    @Override
    public <T> Mono<T> obtainLock(@NotNull Duration duration, @NotNull Function<Boolean, T> function) {
        return runFunctionWithMono(duration, function)
                .contextWrite(this::onContextWrite);
    }

    @Override
    public <T> Flux<T> tryLockMany(@NotNull Function<Boolean, T> function) {
        return runFunctionWithFlux(null, function)
                .contextWrite(this::onContextWrite);
    }

    @Override
    public <T> Flux<T> obtainLockMany(@NotNull Duration duration, @NotNull Function<Boolean, T> function) {
        return runFunctionWithFlux(duration, function)
                .contextWrite(this::onContextWrite);
    }

    @Override
    public <T> Mono<T> tryLockMono(@NotNull Function<Boolean, Mono<T>> function) {
        return this.runFunctionWithMonoMapped(null, function)
                .contextWrite(this::onContextWrite);
    }

    @Override
    public <T> Flux<T> tryLockManyFlux(@NotNull Function<Boolean, Flux<T>> function) {
        return this.runFunctionWithFluxMapped(null, function)
                .contextWrite(this::onContextWrite);
    }

    @Override
    public <T> Mono<T> obtainLockMono(@NotNull Duration duration, @NotNull Function<Boolean, Mono<T>> function) {
        return this.runFunctionWithMonoMapped(duration, function)
                .contextWrite(this::onContextWrite);
    }

    @Override
    public <T> Flux<T> obtainLockManyFlux(@NotNull Duration duration, @NotNull Function<Boolean, Flux<T>> function) {
        return this.runFunctionWithFluxMapped(duration, function)
                .contextWrite(this::onContextWrite);
    }


    private Context onContextWrite(Context context) {
        return context.put("REACTIVE_LOCK_CONTEXT_ID", "REACTIVE_LOCK_CONTEXT:" + UUID.randomUUID());
    }


    protected abstract BaseLocker getBaseLock();

    protected <T> Flux<T> runFunctionWithFlux(@Nullable Duration duration, Function<Boolean, T> function) {
        if (Objects.isNull(duration) || duration.isNegative()) {
            return Flux.usingWhen(
                    Mono.fromSupplier(this::getBaseLock),
                    baseLock -> onResourceFunction(null, baseLock)
                            .map(function),
                    onAsyncComplete(),
                    onAsyncError(),
                    onAsyncCancel()
            );
        }
        return Flux.usingWhen(
                onSupplier(),
                baseLock -> onResourceFunction(duration, baseLock)
                        .map(function),
                onAsyncComplete(),
                onAsyncError(),
                onAsyncCancel()
        );
    }


    protected <T> Flux<T> runFunctionWithFluxMapped(@Nullable Duration duration, Function<Boolean, Flux<T>> function) {
        if (Objects.isNull(duration) || duration.isNegative()) {
            return Flux.usingWhen(
                    onSupplier(),
                    baseLock -> onResourceFunction(null, baseLock)
                            .flatMapMany(function),
                    onAsyncComplete(),
                    onAsyncError(),
                    onAsyncCancel()
            );
        }
        return Flux.usingWhen(
                onSupplier(),
                baseLock -> onResourceFunction(duration, baseLock)
                        .flatMapMany(function),
                onAsyncComplete(),
                onAsyncError(),
                onAsyncCancel()
        );
    }

    @NotNull
    private <T> Mono<Boolean> onResourceFunction(final Duration duration, final BaseLocker baseLock) {
        if (Objects.nonNull(duration) && !duration.isNegative()) {
            return obtain(baseLock)
                    .filter(result -> result)
                    .repeatWhenEmpty(Repeat.<T>onlyIf(repeatContext -> true)
                            .timeout(duration)
                            .fixedBackoff(Duration.ofMillis(100))
                            .doOnRepeat(objectRepeatContext -> loggingUtils.debugIfEnabled(LoggingUtils.repeatDebugMessageLog, objectRepeatContext))
                    )
                    .defaultIfEmpty(false)
                    .doOnNext(result -> loggingUtils.debugIfEnabled(LoggingUtils.durationDebugMessageLog, lockName, duration));
        }
        return obtain(baseLock)
                .filter(result -> result)
                .defaultIfEmpty(false)
                .doOnNext(lockResult -> {
                    loggingUtils.debugIfEnabled(LoggingUtils.graceFullLockMessage, lockName);
                    log.info(LoggingUtils.lockMessageLog, lockResult);
                });
    }

    protected <T> Mono<T> runFunctionWithMono(@Nullable Duration duration, Function<Boolean, T> function) {
        if (Objects.isNull(duration) || duration.isNegative()) {
            return Mono.usingWhen(
                    onSupplier(),
                    baseLock -> onResourceFunction(null, baseLock)
                            .map(function),
                    onAsyncComplete(),
                    onAsyncError(),
                    onAsyncCancel()
            );
        }
        return Mono.usingWhen(
                onSupplier(),
                baseLock -> onResourceFunction(duration, baseLock)
                        .map(function),
                onAsyncComplete(),
                onAsyncError(),
                onAsyncCancel()
        );
    }


    protected <T> Mono<T> runFunctionWithMonoMapped(@Nullable Duration duration, Function<Boolean, Mono<T>> function) {
        if (Objects.isNull(duration) || duration.isNegative()) {
            return Mono.usingWhen(
                    onSupplier(),
                    baseLock -> onResourceFunction(null, baseLock)
                            .flatMap(function),
                    onAsyncComplete(),
                    onAsyncError(),
                    onAsyncCancel()
            );
        }
        return Mono.usingWhen(
                onSupplier(),
                baseLock -> onResourceFunction(duration, baseLock)
                        .flatMap(function),
                onAsyncComplete(),
                onAsyncError(),
                onAsyncCancel()
        );
    }

    @NotNull
    private Mono<BaseLocker> onSupplier() {
        return Mono.just(this.getBaseLock());
    }

    @NotNull
    private Function<BaseLocker, Publisher<?>> onAsyncComplete() {
        return baseLock -> release(baseLock)
                .doOnNext(releaseResult -> {
                    loggingUtils.debugIfEnabled(LoggingUtils.releaseDebugMessageLog, lockName, releaseResult);
                    log.trace(LoggingUtils.releaseInfoMessageLog, releaseResult);
                });
    }

    @NotNull
    private BiFunction<BaseLocker, Throwable, Publisher<?>> onAsyncError() {
        return (baseLock, err) -> release(baseLock)
                .doOnNext(releaseResult -> {
                    loggingUtils.debugIfEnabled(LoggingUtils.errorDebugMessageLog, lockName, err.getMessage(), err);
                    log.info(LoggingUtils.errorInfoMessageLog, releaseResult);
                });
    }

    @NotNull
    private Function<BaseLocker, Publisher<?>> onAsyncCancel() {
        return baseLock -> release(baseLock)
                .doOnNext(releaseResult -> {
                    loggingUtils.debugIfEnabled(LoggingUtils.asyncDebugLog, lockName);
                    log.info(LoggingUtils.asyncCancelReleaseLog, releaseResult);
                });
    }

    private Mono<Boolean> obtain(final BaseLocker baseLock) {
        return Mono.fromSupplier(baseLock::tryLock);

    }

    private Mono<Boolean> release(final BaseLocker baseLock) {
        return Mono.fromSupplier(baseLock::releaseLock);
    }

}
