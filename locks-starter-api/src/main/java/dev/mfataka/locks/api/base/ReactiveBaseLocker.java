package dev.mfataka.locks.api.base;

import java.time.Duration;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.enums.LockMode;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 21.03.2023 11:34
 */
public interface ReactiveBaseLocker extends Locker {
    /**
     * "Try to obtain a lock, and if successful, execute the given function and return its result."
     * <p>
     * The function takes a function as an argument. The function takes a boolean as an argument. The boolean indicates
     * whether the lock was obtained. The function returns a value of type T. The function is executed if the lock is
     * obtained. The result of the function is returned
     *
     * @param lockExecutionResult A function that takes a boolean parameter and returns a value of type T. The boolean
     *                            parameter is true if the lock was obtained, false otherwise.
     * @return A Mono<T>
     */
    <T> Mono<T> tryLock(@NotNull final Function<Boolean, T> lockExecutionResult);

    /**
     * "Try to obtain a lock, and if successful, execute the given function and return its result."
     * <p>
     * The function takes a function as an argument. The function takes a boolean as an argument. The function returns
     * a Mono. The function returns a Mono of type T
     *
     * @param lockExecutionResult A function that takes a boolean parameter and returns a Mono. The boolean parameter
     *                            is true if the lock was obtained, false otherwise.
     * @return A Mono<T>
     */
    <T> Mono<T> tryLockMono(@NotNull final Function<Boolean, Mono<T>> lockExecutionResult);

    /**
     * "Obtain a lock for the given duration, and execute the given function with the lock obtained."
     * <p>
     * The function returns a Mono<T> which is a reactive type that represents a single value. The value is the result
     * of the function that is passed in
     *
     * @param duration            The amount of time to wait for the lock.
     * @param lockExecutionResult A function that takes a boolean as a parameter and returns a value of type T. The
     *                            boolean parameter is true if the lock was obtained, false otherwise.
     * @return A Mono<T>
     */
    <T> Mono<T> obtainLock(@NotNull Duration duration, @NotNull final Function<Boolean, T> lockExecutionResult);

    /**
     * "Obtain a lock for the given duration, and execute the given function with the lock obtained."
     * <p>
     * The function takes two parameters:
     * <p>
     * * A Duration object, which represents the duration for which the lock should be obtained.
     * * A Function object, which represents the function to be executed with the lock obtained
     *
     * @param duration            The duration for which the lock will be held.
     * @param lockExecutionResult A function that returns a Mono<T> that will be executed if the lock is obtained.
     * @return A Mono<T>
     */
    <T> Mono<T> obtainLockMono(@NotNull Duration duration, @NotNull final Function<Boolean, Mono<T>> lockExecutionResult);


    /**
     * "Try to obtain a lock, and if successful, execute the given function and return the result as a Flux."
     * <p>
     * The function takes a function as an argument. The function takes a boolean as an argument. The boolean is true
     * if the lock was obtained, and false if the lock was not obtained. The function returns a value of type T. The
     * function is executed if the lock is obtained. The result of the function is returned as a Flux
     *
     * @param lockExecutionResult A function that returns the result of the execution of the code that needs to be
     *                            locked.
     * @return Flux<T>
     */
    <T> Flux<T> tryLockMany(@NotNull final Function<Boolean, T> lockExecutionResult);

    /**
     * "Try to obtain a lock, and if successful, execute the given function and return its result."
     * <p>
     * The function takes a function as an argument. The function takes a boolean as an argument. The boolean indicates
     * whether the lock was obtained. The function returns a Flux. The Flux emits the result of the lock execution
     *
     * @param lockExecutionResult A function that returns a Flux of type T. This function will be executed if the lock
     *                            is obtained.
     * @return Flux<T>
     */
    <T> Flux<T> tryLockManyFlux(@NotNull final Function<Boolean, Flux<T>> lockExecutionResult);

    /**
     * "Obtain a lock for the given duration, and execute the given function if the lock was obtained."
     * <p>
     * The first parameter is the duration for which the lock should be obtained. The second parameter is a function
     * that will be executed if the lock was obtained. The function takes a boolean parameter that indicates whether
     * the lock was obtained. The function should return a Flux that emits the result of the operation
     *
     * @param duration            The duration for which the lock will be held.
     * @param lockExecutionResult A function that returns a Flux of type T. This function is executed when the lock is
     *                            obtained.
     * @return Flux<T>
     */
    <T> Flux<T> obtainLockMany(@NotNull Duration duration, @NotNull final Function<Boolean, T> lockExecutionResult);

    /**
     * "Obtain a lock for the given duration, and execute the given function if the lock was obtained."
     * <p>
     * The function returns a Flux, which is a Reactor type that represents a stream of 0 or more items. The Flux will
     * contain the result of the lockExecutionResult function if the lock was obtained, or an error if the lock was not
     * obtained
     *
     * @param duration            The duration for which the lock will be held.
     * @param lockExecutionResult A function that returns a Flux of type T. This function will be executed if the lock
     *                            is obtained.
     * @return Flux<T>
     */
    <T> Flux<T> obtainLockManyFlux(@NotNull Duration duration, @NotNull final Function<Boolean, Flux<T>> lockExecutionResult);


    @Override
    default LockMode getLockMode() {
        return LockMode.REACTIVE;
    }
}