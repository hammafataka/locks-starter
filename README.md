# Locks Starter

A robust, flexible locking framework for Spring Boot, supporting both annotation-based and manual lock management for
distributed, reactive, and local locks.

---

## 🚀 Features

- **Annotations:** Just add `@SimpleLocked`, `@DistributedLocked`, `@ReactiveLocked`, or `@ReactiveDistributedLocked` to
  protect your methods.
- **Unified Aspect:** One AOP aspect covers all lock annotations—no duplicated logic.
- **Manual API:** Obtain and operate on locks/lockers directly from Java, for advanced scenarios.
- **AutoHandler:** Automatic lock-acquire, run, and release pattern for any code block.
- **SpEL Support:** Dynamic lock names using Spring Expression Language.
- **Spring Native:** Distributed locks are injectable beans; local locks are accessible statically.
- **Annotation Processor:** Contains annotation processor for easier use of the library and catching common mistakes on
  compile time instead of runtime surprises.

---

## 📦 Installation locks-starter-core

Add the starter to your project dependencies:

```kotlin
implementation("dev.mfataka:locks-starter-core:1.0.0")
```

Or, in a monorepo setup:

```kotlin
implementation(project(":locks-starter-core"))
```

---

## 🏷️ Annotation-Based Usage

### `@SimpleLocked`

Local, non-distributed lock (static, JVM-wide):

```java

@SimpleLocked(name = "#user.id + '-local-lock'", waitFor = 30)
public void processLocal(User user) {
    // critical section (local)
}
```

---

### `@DistributedLocked`

Distributed (e.g., JDBC-backed) lock for cluster-wide safety:

```java

@DistributedLocked(name = "#orderId + '-distributed-lock'", waitFor = 60)
public void processDistributed(String orderId) {
    // critical section (distributed)
}
```

---

### `@ReactiveLocked`

Non-blocking/reactive local lock (JVM scope):

```java

@ReactiveLocked(name = "'reactive-' + #jobId")
public Mono<Void> processLocalReactive(String jobId) {
    // critical section (reactive, local)
    return Mono.empty();
}
```

---

### `@ReactiveDistributedLocked`

Non-blocking/reactive distributed lock:

```java

@ReactiveDistributedLocked(name = "'rdist-' + #itemId")
public Mono<Void> processDistributedReactive(String itemId) {
    // critical section (reactive, distributed)
    return Mono.empty();
}
```

**Parameters for all:**

- `name`: Lock name (SpEL supported, e.g. `#param`)
- `waitFor`: Lock timeout (default: `60`)
- `timeUnit`: Timeout unit (default: `SECONDS`)

---

## 🛠️ Manual Lock/Locker Usage

### Obtain Locks via Registry (for non-distributed/simple/reacive locks)

```java
import factory.dev.mfataka.locks.core.LockRegistry;
import base.dev.mfataka.locks.api.Lock;
import simple.locker.dev.mfataka.locks.core.SimpleLocker;
import dev.mfataka.locks.core.locker.reactive.ReactiveLocker;

public class SomeService {

    public Object someMethod() {

        // Obtain local simple lock
        Lock<SimpleLocker> simpleLockFactory = LockRegistry.simpleLock();
        SimpleLocker locker = simple LockFactory.get("my-critical-section");
        boolean locked = locker.tryLock();
        if (locked) {
            try {
                // critical section
            } finally {
                locker.releaseLock();
            }
        }

        // Obtain local reactive lock
        Lock<ReactiveLocker> reactiveLockFactory = LockRegistry.reactiveLock();
        ReactiveLocker reactiveLocker = reactiveLockFactory.get("my-reactive-section");
        // Use with Project Reactor, etc.
    }
}
```

### Obtain Distributed Locks via Spring DI

```java
import factory.dev.mfataka.locks.api.DistributedLock;
import factory.dev.mfataka.locks.api.ReactiveDistributedLock;

public class SomeService {

    @Autowired
    private DistributedLock distributedLockFactory;

    @Autowired
    private ReactiveDistributedLock reactiveDistributedLockFactory;

    public Object someMethod() {
        // Use distributedLocker
        final var locker = distributedLockFactory.get("shared-resource");
        if (locker.tryLock()) {
            try {
                // cluster-wide safe section
            } finally {
                locker.releaseLock();
            }
        }
    }
}
```

---

## 🤖 Using `AutoHandler` for Automatic Lock Handling

No more manual try-finally! Just use `handler()`:

```java
public class SomeService {

    public Object someMethod() {
        SimpleLocker locker = LockRegistry.simpleLock().get("autohandle");
        locker.handler()
                .then(locked -> {
                    if (locked) {
                        // safely do work
                    }
                });

        // Or return a result with thenReturn
        int result = locker.handler().thenReturn(locked -> {
            if (locked) return 1;
            return 0;
        });
    }
}
```

With timeout:

```java
public class SomeService {

    public Object someMethod() {
        locker.handler()
                .thenReturn(Duration.ofSeconds(10), locked -> doSomething());
    }
}
```

For distributed or reactive distributed lockers, call `handler()` as well.

---

## 🏗️ How It Works

1. Annotate or acquire a lock/locker.
2. Aspect or manual code obtains/release lock as needed.
3. AutoHandler provides safe, functional execution pattern.
4. Distributed locks are safe across the cluster; local locks are JVM-wide.

---

## 📚 Core API Example

```java
// Create and use simple locker manually
public class SomeService {

    SimpleLocker locker = LockRegistry.simpleLock().get("demo-lock");

    public Object someMethod() {
        if (locker.tryLock()) {
            try {
                // locked section
            } finally {
                locker.releaseLock();
            }
        }

        // Clean up old locks
        LockRegistry.simpleLock().clearAllLocks(Duration.ofHours(1));

        // List all lock contexts and lockers
        List<LockContext> allContexts = LockRegistry.existingLocks();
        List<Locker> allLockers = LockRegistry.existingLockers();
    }
}
```

---

## 🪝 Spring Boot Distributed Example

```java

@Service
public class MyService {
    private final DistributedLock distributedLock;

    public MyService(DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    public void doCritical() {
        var locker = distributedLock.get("db-backed-critical");
        if (locker.tryLock()) {
            try {
                // safe cluster-wide!
            } finally {
                locker.releaseLock();
            }
        }
    }
}
```

---

## 📝 Annotation Quick Reference

| Annotation                   | Scope       | Blocking/Reactive | Distributed | How to Use             |
|------------------------------|-------------|-------------------|-------------|------------------------|
| `@SimpleLocked`              | Local (JVM) | Blocking          | No          | Annotation, Static     |
| `@ReactiveLocked`            | Local (JVM) | Reactive          | No          | Annotation, Static     |
| `@DistributedLocked`         | Cluster     | Blocking          | Yes         | Annotation, @Autowired |
| `@ReactiveDistributedLocked` | Cluster     | Reactive          | Yes         | Annotation, @Autowired |

---

# LocksCorePropertyConfig – Configuration Properties for Locks Starter

`LocksCorePropertyConfig` is a Spring Boot configuration class that provides properties for enabling, tuning, and
securing the Locks Starter.

This class allows you to:

- Enable or disable the lock library
- Enable debug mode for lock operations
- Control automatic lock cleanup and age limits
- Secure the lock management HTTP endpoints

---

## ⚙️ Usage

### 1. Register as Configuration Properties

This bean is automatically registered if you include the starter and use Spring Boot.

```java

@ConfigurationProperties(prefix = "locks.starter")
@EnableConfigurationProperties(LocksCorePropertyConfig.class)
public class LocksCorePropertyConfig {
    // ...fields as described below
}
```

---

### 2. Properties

| Property                          | Type     | Default  | Description                                            |
|-----------------------------------|----------|----------|--------------------------------------------------------|
| `locks.starter.enabled`           | boolean  | `true`   | Enable/disable the lock library.                       |
| `locks.starter.debug-enabled`     | boolean  | `true`   | Enable debug logs for lock operations.                 |
| `locks.starter.max-age`           | duration | `5m`     | Max age for a lock; locks older than this are removed. |
| `locks.starter.cleanup-interval`  | duration | `4m`     | Interval between automatic lock cleanups.              |
| `locks.starter.endpoint.base-url` | string   | `/locks` | Base URL for lock management endpoints.                |
| `locks.starter.endpoint.secure`   | boolean  | `true`   | Require authentication for lock endpoints.             |
| `locks.starter.endpoint.username` | string   | `lock`   | Management endpoint username.                          |
| `locks.starter.endpoint.password` | string   | `lock`   | Management endpoint password.                          |

---

### 3. Example application.yml

```yaml
locks:
  starter:
    enabled: true
    debug-enabled: false
    max-age: 10m
    cleanup-interval: 5m
    endpoint:
      base-url: /locks
      secure: true
      username: admin
      password: secret
```

---

### 4. What Does Each Property Do?

- **enabled**: Globally turn the locks library on/off.
- **debugEnabled**: Enables verbose logging for troubleshooting.
- **maxAge**: Maximum allowed age for any lock. Expired locks will be deleted during automatic cleanup.
- **cleanupInterval**: How often the system checks for expired locks and cleans them up.
- **endpoint**: Controls the HTTP endpoints for lock management (path, security, credentials).
    - `baseUrl`: Path to access the management endpoints (e.g., `/locks`).
    - `secure`: If true, requires username/password authentication.
    - `username`/`password`: Credentials for securing management endpoints.

---

### 5. Default Values in Code

You can obtain the default property config using:

```
LocksCorePropertyConfig.defaults();
```

Which yields:

```java
public class AppConfig {

    public LocksCorePropertyConfig locksCorePropertyConfig() {
        new LocksCorePropertyConfig(
                true,                  // enabled
                true,                  // debugEnabled
                Duration.ofMinutes(5), // maxAge
                Duration.ofMinutes(4), // cleanupInterval
                new EndpointProperties("/locks", true, "lock", "lock")
        );
    }
}
```

---

### 6. Endpoint Security

The class includes support for securing management endpoints using a Spring Security `User` object:

```java
public User asUser() {
    return new User(username, password, Collections.emptySet());
}
```

This ensures only authorized users can access lock management HTTP endpoints.

---

### 7. Automatic Lock Cleanup

The `maxAge` and `cleanupInterval` properties control how old locks are allowed to remain before they are automatically
purged.  
During cleanup, any lock older than `maxAge` is forcibly removed.


---

## 🔒 Summary

- **Easily control lock library activation, debugging, cleanup, and endpoint security.**
- **Supports safe, cluster-ready management for your locks.**

## 🛡️ Troubleshooting

- **Aspect not triggered?**
    - Ensure beans are managed by Spring.
    - Avoid calling locked methods from inside the same class.
- **Lock name not dynamic?**
    - Compile with `-parameters` for parameter name support.
- **Reactive methods not working?**
    - Use `@ReactiveLocked` or `@ReactiveDistributedLocked` for `Mono`, `Flux`, etc.

---

# JDBC Locks Service and Table

## SimpleJdbcService: JDBC-Powered Distributed Locking

The `SimpleJdbcService` provides distributed locking using a relational database as the backing store.
It is responsible for:

- Creating and maintaining the lock table
- Inserting, checking, and removing lock records
- Automatically cleaning up expired locks

---

## ⚡ Why Do You Need a Table?

To coordinate locks across a distributed system (many app nodes), the service stores locks in a single database table.  
This ensures that only one owner holds a lock at any time, cluster-wide.

---

## 🗄️ Required Table: `distributed_locks`

This table is **created automatically** if missing, but you can also create it manually.  
It should be available to the application’s configured datasource.

### Schema (MySQL/PostgreSQL syntax):

```sql
CREATE TABLE IF NOT EXISTS distributed_locks
(
    name      VARCHAR(100) PRIMARY KEY, -- Unique lock name
    owner     VARCHAR(100),             -- Identifier for who holds the lock
    locked_at INT(11)                   -- Timestamp (UNIX seconds) when lock was acquired
);
```

**Columns:**

- `name`: Unique string for each lock
- `owner`: String identifying the owner (e.g., hostname, thread, node, etc.)
- `locked_at`: UNIX timestamp, used for cleanup and age-based expiry

---

## 🚦 Service Responsibilities

The `SimpleJdbcService` class is responsible for:

- **Table creation**: Ensures `distributed_locks` exists at startup (`@PostConstruct`)
- **Insert lock**: Tries to insert a lock row (`insertLock`)
- **Check lock**: Verifies if a lock is held or held by specific owner
- **Delete lock**: Deletes locks by name, by owner, or all
- **Cleanup expired locks**: Removes locks older than a configurable max age (`cleanExpiredLocks`)
- **List/find**: Query all existing distributed locks

---

## 🛡️ Lock Expiration & Cleanup

- The `locked_at` field is a UNIX timestamp of when the lock was acquired.
- The library periodically runs a cleanup:
    - Deletes any lock where `locked_at < (now - maxAge)`
    - `maxAge` is settable via configuration (see your main README)
    - This prevents zombie/abandoned locks from blocking your system.

---

## ⚠️ Notes

- The default table name is `distributed_locks`.
- Ensure your database user has privileges to create tables if you rely on auto-creation.
- For best resilience, use InnoDB or a transactional table type.
- Lock cleanup frequency and maxAge are configurable (`locks.starter.max-age`, `locks.starter.cleanup-interval`).

---

## 🔑 Security & Reliability

- All lock changes are performed in new transactions (see `@Transactional` settings).
- SQL exceptions are wrapped in `LockOperationException`.
- Concurrency is handled at the database level with a primary key constraint.

---

## 📋 Recommended Index

If you have a lot of locks or high churn, consider indexing on `locked_at` for fast expiry checks.

```sql
CREATE INDEX idx_locked_at ON distributed_locks (locked_at);
```

---

# Locks Annotation Processor

This module provides an annotation processor for compile-time validation of method signatures using lock annotations
from the Locks library.

---

## ✨ Purpose

- **Catch common mistakes early:** Ensure that methods using lock annotations follow required conventions for exception
  handling and method signatures.
- **Fail fast:** Get clear compile-time error messages, not runtime surprises.
- **Improve code quality and reliability** in distributed/concurrent code.

---

## 🚦 What Does It Do?

The module inspects any method annotated with one of the following:

- `@SimpleLocked`
- `@DistributedLocked`
- `@ReactiveLocked`
- `@ReactiveDistributedLocked`

It enforces that such methods:

1. **Must be methods** (not constructors or fields)
2. **Must not be empty** (must have a body)
3. **Must handle lock-acquisition failures** by:
    - Either declaring `throws LockAlreadyAcquiredException`
    - Or catching `LockAlreadyAcquiredException` in a try-catch block

This ensures your codebase safely handles scenarios where a lock cannot be acquired (due to another node/process holding
the lock).

---

## 🛑 Example: What the Processor Catches

### ❌ Invalid — missing `throws` and not catching exception

```java

@DistributedLocked(name = "myLock")
public void criticalSection() {
    // ERROR: Must declare throws or catch LockAlreadyAcquiredException
}
```

### ❌ Invalid — no method body

```java

@SimpleLocked(name = "foo")
public void noImpl();
```

### ✅ Valid — method declares throws

```java

@DistributedLocked(name = "job")
public void runJob() throws LockAlreadyAcquiredException {
    // safe: method may throw lock exception
}
```

### ✅ Valid — catches exception

```java

@ReactiveLocked(name = "reactive")
public void process() {
    try {
        // do work
    } catch (LockAlreadyAcquiredException ex) {
        // handle or log
    }
}
```

---

## ⚙️ How to Use

Add `locks-starter-processor` as an `annotationProcessor` dependency in your project:

<details>
<summary>Gradle</summary>

```kotlin
dependencies {
    implementation("dev.mfataka:locks-starter-core:1.0.0")
    annotationProcessor("dev.mfataka:locks-starter-processor:1.0.0")
}
```

</details>

<details>
<summary>Maven</summary>

```xml

<dependency>
    <groupId>dev.mfataka</groupId>
    <artifactId>locks-starter-processor</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

</details>

No configuration needed—your IDE or build tool will run the processor automatically.

## 🔒 Exception Required

All methods using a lock annotation **must** either:

- Declare `throws LockAlreadyAcquiredException`
- Or handle the exception with a try-catch

This ensures your code can safely respond to cases when a lock is not acquired.

## 🙋 Need Help?

Open an issue or contact the maintainer for support.

## 📜 License

MIT

---

## 🙋 Need Help?

Open an issue or contact the maintainer.

