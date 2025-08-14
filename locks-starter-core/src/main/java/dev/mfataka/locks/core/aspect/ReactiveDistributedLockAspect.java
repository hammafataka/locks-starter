package dev.mfataka.locks.core.aspect;//package dev.mfataka.locks.aspect;
//
//import java.time.Duration;
//import java.util.function.Function;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.jetbrains.annotations.NotNull;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import lombok.extern.slf4j.Slf4j;
//
//import dev.mfataka.locks.api.ReactiveDistributedLock;
//import dev.mfataka.locks.core.annotation.ReactiveDistributedLocked;
//import dev.mfataka.locks.api.factory.DistributedLockFactory;
//import dev.mfataka.locks.api.factory.ReactiveDistributedLockFactory;
//import dev.mfataka.locks.core.service.JdbcService;
//import dev.mfataka.locks.core.spel.SpelExpressionEvaluator;
//
///**
// * @author HAMMA FATAKA
// * @project locks-starter
// * @date 14.04.2025 17:10
// */
//@Slf4j
//@Aspect
//public class ReactiveDistributedLockAspect extends LockAspectSupport {
//    private final Function<String, ReactiveDistributedLock> lockFactory;
//
//    public ReactiveDistributedLockAspect(final SpelExpressionEvaluator expressionEvaluator, final JdbcService jdbcService) {
//        super(expressionEvaluator);
//        final var reactiveDistributedLockFactory = ReactiveDistributedLockFactory.of(DistributedLockFactory.of(jdbcService));
//        this.lockFactory = reactiveDistributedLockFactory::getOrCreateLock;
//    }
//
//    @Around("@annotation(reactiveDistributedLocked) && execution(* *(..))")
//    public Object jdbcLocked(@NotNull ProceedingJoinPoint joinPoint, @NotNull ReactiveDistributedLocked reactiveDistributedLocked) throws Throwable {
//        final var timeout = Duration.of(reactiveDistributedLocked.waitFor(), reactiveDistributedLocked.timeUnit());
//
//
//        final var signature = (MethodSignature) joinPoint.getSignature();
//        final var method = signature.getMethod();
//        final var returnType = signature.getReturnType();
//
//        final var rawName = reactiveDistributedLocked.name();
//        final var evaluatedName = expressionEvaluator.evaluate(rawName, method, joinPoint.getArgs());
//
//        final var lock = lockFactory.apply(evaluatedName);
//
//        if (Mono.class.isAssignableFrom(returnType)) {
//            final var execution = getMonoFunction(joinPoint, evaluatedName);
//            if (timeout.isZero()) {
//                return lock.tryLock(execution).doAfterTerminate(lock::removeLockAfter);
//            }
//            return lock.obtainLockMono(timeout, execution).doAfterTerminate(lock::removeLockAfter);
//
//        } else if (Flux.class.isAssignableFrom(returnType)) {
//            final var execution = getFluxFunction(joinPoint, evaluatedName);
//            if (timeout.isZero()) {
//                return lock.tryLock(execution).doAfterTerminate(lock::removeLockAfter);
//            }
//            return lock.obtainLockManyFlux(timeout, execution).doAfterTerminate(lock::removeLockAfter);
//        }
//        throw new IllegalStateException("@ReactiveLocked must be applied to Mono<T> or Flux<T> methods");
//    }
//}
