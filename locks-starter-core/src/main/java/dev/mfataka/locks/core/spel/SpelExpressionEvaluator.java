package dev.mfataka.locks.core.spel;

import java.lang.reflect.Method;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;



/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 10.04.2025 12:55
 */
public class SpelExpressionEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer paramDiscoverer = new StandardReflectionParameterNameDiscoverer();

    public String evaluate(final String expression, final Method method, final Object[] args) {
        if (!isSpelExpression(expression)) {
            return expression;
        }
        final var context = new StandardEvaluationContext();

        final var paramNames = paramDiscoverer.getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        final var parsedExpression = parser.parseExpression(expression);
        return parsedExpression.getValue(context, String.class);
    }

    public boolean isSpelExpression(String expression) {
        return expression != null && expression.trim().startsWith("#");
    }
}
