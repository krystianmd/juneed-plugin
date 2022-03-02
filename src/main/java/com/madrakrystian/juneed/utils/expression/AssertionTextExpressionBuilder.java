package com.madrakrystian.juneed.utils.expression;

import static com.madrakrystian.juneed.utils.AssertJUtils.ASSERTJ_ASSERTIONS_QUALIFIED_IMPORT;

public final class AssertionTextExpressionBuilder {

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
