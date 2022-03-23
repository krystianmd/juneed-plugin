package com.madrakrystian.juneed.utils.expression;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class for building fluent assertion expression text.
 */
public abstract class FluentAssertionExpressionTextBuilder {

    private final StringBuilder expressionBuilder;

    protected FluentAssertionExpressionTextBuilder(@NotNull String fqAssertion) {
        this.expressionBuilder = new StringBuilder();
        this.expressionBuilder.append(fqAssertion);
    }

    /**
     * Adds given parameter with the parentheses.
     *
     * @param parameter a method parameter
     * @return builder reference
     */
    public FluentAssertionExpressionTextBuilder parameter(@NotNull String parameter) {
        expressionBuilder.append("(").append(parameter).append(")");
        return this;
    }

    /**
     * Adds empty parentheses.
     *
     * @return builder reference
     */
    public FluentAssertionExpressionTextBuilder noParameters() {
        expressionBuilder.append("(").append(")");
        return this;
    }

    /**
     * Adds fluent method call in the new line.
     *
     * @param methodCall a method call with no parentheses nor parameters
     * @return builder reference
     */
    public FluentAssertionExpressionTextBuilder methodCall(@NotNull String methodCall) {
        expressionBuilder.append("\n.").append(methodCall);
        return this;
    }

    /**
     * Returns result of the builder steps.
     *
     * @return created expression text
     */
    public String build() {
        return expressionBuilder.toString();
    }
}
