package dev.mfataka.locks.core.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.condition.LocksEnabledCondition;
import dev.mfataka.locks.api.factory.DistributedLock;
import dev.mfataka.locks.api.factory.ReactiveDistributedLock;
import dev.mfataka.locks.api.service.JdbcService;
import dev.mfataka.locks.core.aspect.UnifiedLockAspect;
import dev.mfataka.locks.core.factory.StandardReliableLocks;
import dev.mfataka.locks.core.factory.StandardReliableLocksImp;
import dev.mfataka.locks.core.resolver.LockMetadataResolver;
import dev.mfataka.locks.core.service.LockCleanerService;
import dev.mfataka.locks.core.service.SimpleJdbcService;
import dev.mfataka.locks.core.spel.SpelExpressionEvaluator;
import dev.mfataka.locks.core.strategy.BlockingLockExecutor;
import dev.mfataka.locks.core.strategy.LockExecutionStrategy;
import dev.mfataka.locks.core.strategy.ReactiveLockExecutor;

/**
 * @author HAMMA FATAKA
 */
@Slf4j
@Configuration
@Conditional(LocksEnabledCondition.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureAfter({ValidationAutoConfiguration.class})
public class DistributedLockAutoConfigurer {


    @Bean
    public DistributedLock distributedLock(final StandardReliableLocks reliableLocks) {
        return reliableLocks.distributedLock();
    }

    @Bean
    public ReactiveDistributedLock reactiveDistributedLock(final StandardReliableLocks reliableLocks) {
        return reliableLocks.reactiveDistributedLock();
    }

    @Bean
    public StandardReliableLocks standardReliableFactories(final JdbcService jdbcService, final LocksCorePropertyConfig lockPropertyConfig) {
        return new StandardReliableLocksImp(jdbcService, lockPropertyConfig);
    }

    @Bean
    public TaskScheduler locksTaskScheduler() {
        final var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.setThreadNamePrefix("DistributedLock-");
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        return threadPoolTaskScheduler;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockCleanerService lockCleanerService(final LocksCorePropertyConfig lockPropertyConfig, final StandardReliableLocks reliableLocks, final TaskScheduler locksTaskScheduler) {
        return new LockCleanerService(lockPropertyConfig, reliableLocks, locksTaskScheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    public BlockingLockExecutor blockingLockExecutor(final StandardReliableLocks reliableFactories) {
        return new BlockingLockExecutor(reliableFactories);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveLockExecutor reactiveLockExecutor(final StandardReliableLocks reliableFactories) {
        return new ReactiveLockExecutor(reliableFactories);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpelExpressionEvaluator evaluator() {
        return new SpelExpressionEvaluator();
    }

    @Bean
    @ConditionalOnMissingBean
    public LockMetadataResolver lockMetadataResolver(final SpelExpressionEvaluator spelExpressionEvaluator) {
        return new LockMetadataResolver(spelExpressionEvaluator);
    }

    @Bean
    @ConditionalOnMissingBean
    public LockExecutionStrategy lockExecutionStrategy(final BlockingLockExecutor blockingLockExecutor, final ReactiveLockExecutor reactiveLockExecutor) {
        return new LockExecutionStrategy(blockingLockExecutor, reactiveLockExecutor);
    }


    @Bean
    @ConditionalOnMissingBean
    public UnifiedLockAspect unifiedLockAspect(final LockExecutionStrategy strategy, final LockMetadataResolver resolver) {
        return new UnifiedLockAspect(strategy, resolver);
    }


    @Bean
    @ConditionalOnMissingBean
    public JdbcService jdbcService(final NamedParameterJdbcTemplate jdbcTemplate, final DataSource dataSource) {
        return new SimpleJdbcService(jdbcTemplate, dataSource);
    }


    @Bean
    @ConditionalOnMissingBean
    public LocksCorePropertyConfig locksCorePropertyConfig() {
        log.info("Using default lock core property config");
        return LocksCorePropertyConfig.defaults();
    }

}
