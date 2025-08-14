package dev.mfataka.locks.core.spel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpelExpressionEvaluatorTest {

    private SpelExpressionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new SpelExpressionEvaluator();
    }

    // Dummy method to use in reflection
    public void dummyMethod(final String param1, int param2) {
    }

    @Test
    void testEvaluateSimpleNonSpelExpression() throws NoSuchMethodException {
        final var method = getClass().getMethod("dummyMethod", String.class, int.class);

        final var expression = "fixed-value";
        final Object[] args = {"value1", 42};

        final var result = evaluator.evaluate(expression, method, args);
        assertEquals("fixed-value", result);
    }

    @Test
    void testEvaluateSpelExpressionUsingSingleParameter() throws NoSuchMethodException {
        final var method = getClass().getMethod("dummyMethod", String.class, int.class);

        final var expression = "#param1"; // referring to first parameter by name
        final Object[] args = {"testValue", 99};

        final var result = evaluator.evaluate(expression, method, args);
        assertEquals("testValue", result);
    }

    @Test
    void testEvaluateSpelExpressionUsingSecondParameter() throws NoSuchMethodException {
        final var method = getClass().getMethod("dummyMethod", String.class, int.class);

        final var expression = "#param2";
        final Object[] args = {"irrelevant", 123};

        final var result = evaluator.evaluate(expression, method, args);
        assertEquals("123", result);
    }

    @Test
    void testIsSpelExpression() {
        assertTrue(evaluator.isSpelExpression("#something"));
        assertFalse(evaluator.isSpelExpression("not-spel"));
        assertFalse(evaluator.isSpelExpression(null));
        assertFalse(evaluator.isSpelExpression("  "));
    }

    @Test
    void testEvaluateComplexExpression() throws NoSuchMethodException {
        final var method = getClass().getMethod("dummyMethod", String.class, int.class);

        final var expression = "#param1 + '-' + #param2";
        final Object[] args = {"valueX", 55};

        final var result = evaluator.evaluate(expression, method, args);
        assertEquals("valueX-55", result);
    }
}