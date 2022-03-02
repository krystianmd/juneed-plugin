package com.madrakrystian.juneed.utils.expression;

public final class AssertionTextExpressionBuilder {

    private static final String ASSERTJ_ASSERTIONS_QUALIFIED_IMPORT = "org.assertj.core.api.Assertions";

    private String expression;

    private AssertionTextExpressionBuilder(String qualifiedAssertion) {
        this.expression = qualifiedAssertion;
    }

    public static AssertionTextExpressionBuilder assertJAssertion(String assertion) {
        return new AssertionTextExpressionBuilder(ASSERTJ_ASSERTIONS_QUALIFIED_IMPORT + "." + assertion);
    }

    public AssertionTextExpressionBuilder parameter(String parameter) {
        expression += "(" + parameter + ")";
        return this;
    }

    public AssertionTextExpressionBuilder noParameters() {
        expression += "()";
        return this;
    }

    public AssertionTextExpressionBuilder fluentMethodCall(String methodCall) {
        expression += "\n." + methodCall;
        return this;
    }

    public String build() {
        return expression;
    }
}
