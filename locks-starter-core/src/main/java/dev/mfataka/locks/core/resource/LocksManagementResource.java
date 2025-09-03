package dev.mfataka.locks.core.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.condition.LocksEnabledCondition;
import dev.mfataka.locks.api.context.LockContext;
import dev.mfataka.locks.api.enums.LockMode;
import dev.mfataka.locks.api.enums.LockType;
import dev.mfataka.locks.core.descriptor.LockDescriptor;
import dev.mfataka.locks.core.factory.LockRegistry;
import dev.mfataka.locks.core.service.LockCleanerService;

/**
 * @author HAMMA FATAKA
 */
@RestController
@RequestMapping("${locks.starter.endpoint.base-url}")
@Conditional(LocksEnabledCondition.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LocksManagementResource {
    private final LockCleanerService lockCleanerService;

    @GetMapping(path = "/existing/locks")
    public ResponseEntity<List<LockContext>> existingLocks() {
        return ResponseEntity.ok(LockRegistry.existingLocks());
    }

    @GetMapping(path = "/existing/lockers")
    public ResponseEntity<List<Locker>> existingLockers() {
        return ResponseEntity.ok(LockRegistry.existingLockers());
    }

    @GetMapping(path = "/clean/reactive/")
    public ResponseEntity<String> cleanReactive() {
        final var descriptor = LockDescriptor.forClean(LockType.LOCAL, LockMode.REACTIVE);
        final var cleaned = lockCleanerService.clean(descriptor);
        return ResponseEntity.ok("Cleaned: " + cleaned);
    }

    @GetMapping(path = "/clean/blocking/")
    public ResponseEntity<String> cleanBlocking() {
        final var descriptor = LockDescriptor.forClean(LockType.LOCAL, LockMode.BLOCKING);
        final var cleaned = lockCleanerService.clean(descriptor);
        return ResponseEntity.ok("Cleaned: " + cleaned);
    }


    @GetMapping(path = "/clean/distributed/reactive/")
    public ResponseEntity<String> cleanDistributedReactive() {
        final var descriptor = LockDescriptor.forClean(LockType.DISTRIBUTED, LockMode.REACTIVE);
        final var cleaned = lockCleanerService.clean(descriptor);
        return ResponseEntity.ok("Cleaned: " + cleaned);
    }

    @GetMapping(path = "/clean/distributed/blocking/")
    public ResponseEntity<String> cleanDistributedBlocking() {
        final var descriptor = LockDescriptor.forClean(LockType.DISTRIBUTED, LockMode.BLOCKING);
        final var cleaned = lockCleanerService.clean(descriptor);
        return ResponseEntity.ok("Cleaned: " + cleaned);
    }

    @GetMapping(path = "/clean/all/")
    public ResponseEntity<String> cleanAll() {
        final var cleaned = lockCleanerService.cleanAll();
        return ResponseEntity.ok("Cleaned: " + cleaned);
    }

}
