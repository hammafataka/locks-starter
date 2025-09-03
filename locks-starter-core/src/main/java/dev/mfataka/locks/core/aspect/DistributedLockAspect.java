package dev.mfataka.locks.core.aspect;//package dev.mfataka.locks.aspect;
//
//import java.time.Duration;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.jetbrains.annotations.NotNull;
//
//import lombok.extern.slf4j.Slf4j;
//
//
//import dev.mfataka.locks.core.annotation.DistributedLocked;
//import dev.mfataka.locks.api.factory.DistributedLockFactory;
//import dev.mfataka.locks.core.service.JdbcService;
//import dev.mfataka.locks.core.spel.SpelExpressionEvaluator;
//
///**
// * @author HAMMA FATAKA
// */
//@Slf4j
//@Aspect
//public class DistributedLockAspect extends LockAspectSupport {
//
//    private final DistributedLockFactory distributedLockFactory;
//
//    public DistributedLockAspect(final SpelExpressionEvaluator expressionEvaluator, final JdbcService jdbcService) {
//        super(expressionEvaluator);
//        distributedLockFactory = DistributedLockFactory.of(jdbcService);
//        log.info("JdbcLockAspect created");
//    }
//
//    @Around("@annotation(distributedLocked) && execution(* *(..))")
//    public Object jdbcLocked(@NotNull ProceedingJoinPoint joinPoint, @NotNull DistributedLocked distributedLocked) throws Throwable {
//        log.info("aspect withJdbcLocked");
//
//        final var signature = (MethodSignature) joinPoint.getSignature();
//        final var method = signature.getMethod();
//
//        final var lockName = expressionEvaluator.evaluate(distributedLocked.name(), method, joinPoint.getArgs());
//        final var waitTime = Duration.of(distributedLocked.waitFor(), distributedLocked.timeUnit());
//        final var lock = distributedLockFactory.getOrCreateLock(lockName);
//        return executeCriticalSection(joinPoint, lockName, waitTime, lock.handler());
//    }
//
//}
