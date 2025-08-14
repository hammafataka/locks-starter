package dev.mfataka.locks.core.aspect;//package dev.mfataka.locks.aspect;
//
//import java.time.Duration;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import dev.mfataka.locks.core.annotation.Locked;
//import dev.mfataka.locks.core.defaults.DefaultLock;
//import dev.mfataka.locks.api.base.LockFactory;
//import dev.mfataka.locks.core.spel.SpelExpressionEvaluator;
//
///**
// * @author HAMMA FATAKA
// * @project locks-starter
// * @date 10.04.2025 12:23
// */
//@Aspect
//@Component
//public class LockAspect extends LockAspectSupport {
//    private final LockFactory<DefaultLock> lockFactory = LockFactory.getDefaultLockFactory();
//
//    public LockAspect(final SpelExpressionEvaluator expressionEvaluator) {
//        super(expressionEvaluator);
//    }
//
//
//    @Around("@annotation(locked)")
//    public Object withJdbcLocked(final ProceedingJoinPoint joinPoint, final Locked locked) throws Throwable {
//        final var signature = (MethodSignature) joinPoint.getSignature();
//        final var method = signature.getMethod();
//
//        final var lockName = expressionEvaluator.evaluate(locked.name(), method, joinPoint.getArgs());
//        final var waitTime = Duration.of(locked.waitFor(), locked.timeUnit());
//        final var lock = lockFactory.getOrCreateLock(lockName);
//        return executeCriticalSection(joinPoint, lockName, waitTime, lock.handler());
//    }
//
//
//}
