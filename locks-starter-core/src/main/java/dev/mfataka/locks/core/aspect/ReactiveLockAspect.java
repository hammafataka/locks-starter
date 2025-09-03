package dev.mfataka.locks.core.aspect;//package dev.mfataka.locks.aspect;
//
//import java.time.Duration;
//import java.util.function.Function;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import dev.mfataka.locks.api.ReactiveLock;
//import dev.mfataka.locks.core.annotation.ReactiveLocked;
//import dev.mfataka.locks.api.base.LockFactory;
//import dev.mfataka.locks.core.spel.SpelExpressionEvaluator;
//
///**
// * @author HAMMA FATAKA
// */
//@Aspect
//@Component
//public class ReactiveLockAspect extends LockAspectSupport {
//    private final Function<String, ReactiveLock> lockFactory = LockFactory.getReactiveLockFactory()::getOrCreateLock;
//
//    public ReactiveLockAspect(final SpelExpressionEvaluator expressionEvaluator) {
//        super(expressionEvaluator);
//    }
//
//    @Around("@annotation(reactiveLocked)")
//    public Object applyReactiveLock(final ProceedingJoinPoint joinPoint, final ReactiveLocked reactiveLocked) {
//        final var timeout = Duration.of(reactiveLocked.waitFor(), reactiveLocked.timeUnit());
//
//
//        final var signature = (MethodSignature) joinPoint.getSignature();
//        final var method = signature.getMethod();
//        final var returnType = signature.getReturnType();
//
//        final var rawName = reactiveLocked.name();
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
