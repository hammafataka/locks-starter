package dev.mfataka.locks.api.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 20.05.2025 14:14
 */
public class LocksEnabledCondition implements Condition {
    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("locks.starter.enabled", Boolean.class, false);
    }
}
