package dev.mfataka.locks.core.aspect;//package dev.mfataka.locks.aspect;
//
//import java.time.Duration;
//import java.util.function.Function;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.jetbrains.annotations.NotNull;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import dev.mfataka.locks.core.base.AutoHandler;
//import dev.mfataka.locks.core.spel.SpelExpressionEvaluator;
//
///**
// * @author HAMMA FATAKA
// */
//@Slf4j
//@RequiredArgsConstructor
//public abstract class LockAspectSupport {
//    protected final SpelExpressionEvaluator expressionEvaluator;
//
//    protected static Object executeCriticalSection(final ProceedingJoinPoint joinPoint,
//                                                   final String lockName,
//                                                   final Duration waitTime,
//                                                   final AutoHandler handler) throws InterruptedException {
//        if (waitTime.isZero()) {
//            return handler.thenReturn(locked -> {
//                if (locked) {
//                    try {
//                        return joinPoint.proceed();
//                    } catch (final Throwable e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                throw new IllegalStateException("Could not obtain lock: " + lockName);
//            });
//        }
//        return handler.thenReturn(waitTime, lockedResult -> {
//            if (lockedResult) {
//                try {
//                    return joinPoint.proceed();
//                } catch (final Throwable e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            throw new IllegalStateException("Could not obtain lock: " + lockName);
//        });
//    }
//
//    protected static @NotNull Function<Boolean, Mono<Object>> getMonoFunction(final ProceedingJoinPoint joinPoint, final String name) {
//        return success -> {
//            try {
//                return success ? ((Mono<Object>) joinPoint.proceed()) : Mono.error(new IllegalStateException("Reactive lock not acquired: " + name));
//            } catch (Throwable e) {
//                return Mono.error(e);
//            }
//        };
//    }
//
//    protected static @NotNull Function<Boolean, Flux<Object>> getFluxFunction(final ProceedingJoinPoint joinPoint, String name) {
//        return success -> {
//            try {
//                return success ? ((Flux<Object>) joinPoint.proceed()) : Flux.error(new IllegalStateException("Reactive lock not acquired: " + name));
//            } catch (Throwable e) {
//                return Flux.error(e);
//            }
//        };
//    }
//}
