package dev.mfataka.locks.core.locks;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import dev.mfataka.locks.api.service.JdbcService;
import dev.mfataka.locks.core.AbstractLockTest;
import dev.mfataka.locks.core.config.LocksCorePropertyConfig;
import dev.mfataka.locks.core.factory.StandardReliableLocks;
import dev.mfataka.locks.core.factory.StandardReliableLocksImp;

/**
 * @author HAMMA FATAKA
 */

public class ReactiveDistributedLockTest extends AbstractLockTest {

    private final StandardReliableLocks locks;

    @Autowired
    public ReactiveDistributedLockTest(final JdbcService jdbcService, final LocksCorePropertyConfig locksCorePropertyConfig) {
        this.locks = new StandardReliableLocksImp(jdbcService, locksCorePropertyConfig);
    }


    @Test
    public void testReactiveDistributedLock() {
        final var locker1 = locks.createReactiveDistributed("test");
        final var locker2 = locks.createReactiveDistributed("test");

        final var order = new StringBuilder();

        // Chain 1: simulate work for 300ms
        final Mono<Void> chain1 = locker1.tryLockMono(locked -> {
            if (locked) {
                order.append("A");
                // do not hold the chain
                return Mono.delay(Duration.ZERO)
                        .then();
            }
            return Mono.empty();
        });

        // Chain 2: simulate work for 100ms (should not start before 1 is done)
        final Mono<Void> chain2 = locker2.obtainLockMono(Duration.ofMillis(100), locked -> {
            if (locked) {
                order.append("B");
                return Mono.delay(Duration.ZERO)
                        .then();
            }
            return Mono.empty();
        });

        // Start both chains as simultaneously as possible
        StepVerifier.create(Mono.when(chain1, chain2))
                .expectSubscription()
                .verifyComplete();


        // Only one should run at a time, so order is "AB"
        Assertions.assertEquals("AB", order.toString());
    }


    @Test
    public void shouldLockAndNotRelease() {
        final var locker1 = locks.createReactiveDistributed("test");
        final var locker2 = locks.createReactiveDistributed("test");

        final var inCriticalSection = new AtomicInteger(0);
        final var overlapped = new AtomicBoolean(false);
        final var order = new StringBuilder();

        // Chain 1: simulate work for 300ms
        final var chain1 = locker1.tryLockMono(locked -> {
            if (locked) {
                order.append("A");
                if (inCriticalSection.incrementAndGet() > 1) {
                    overlapped.set(true);
                }
                // hold the chain for 300 millis
                return Mono.delay(Duration.ofMillis(300))
                        .doFinally(sig -> inCriticalSection.decrementAndGet())
                        .then();
            }
            return Mono.empty();
        });

        // Chain 2: simulate work for 100ms (should not start before 1 is done)
        final var chain2 = locker2.tryLockMono(locked -> {
            if (locked) {
                order.append("B");
                if (inCriticalSection.incrementAndGet() > 1) {
                    overlapped.set(true);
                }
                return Mono.delay(Duration.ofMillis(100))
                        .doFinally(sig -> inCriticalSection.decrementAndGet())
                        .then();
            }
            return Mono.empty();
        });

        // Start both chains as simultaneously as possible
        StepVerifier.create(Mono.when(chain1, chain2))
                .expectSubscription()
                .verifyComplete();


        // Only one should run at a time, so order is "AB"
        Assertions.assertEquals("A", order.toString());
        Assertions.assertFalse(overlapped.get());

    }

}
