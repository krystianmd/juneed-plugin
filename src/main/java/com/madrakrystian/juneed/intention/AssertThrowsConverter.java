package com.madrakrystian.juneed.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.LambdaUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import com.madrakrystian.juneed.utils.methodCall.AssertionFactory;
import com.madrakrystian.juneed.utils.AssertJUtils;
import com.madrakrystian.juneed.utils.expression.AssertionTextExpressionBuilder;
import com.madrakrystian.juneed.utils.expression.AssertionArgumentsValidator;
import com.madrakrystian.juneed.utils.method.AssertionSignatureValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implements an intention action to replace JUnit assertThrows() assertion with AssertJ fluent one.
 * <p>
 * e.g. before applying intention
 * assertThrows(FoobarException.class, () -> fooBarService.apply(), "message");
 * <p>
 * after applying intention
 * Assertions.assertThatExceptionOfType(FoobarException.class)
 *      .isThrownBy(() -> fooBarService.apply())
 *      .withMessageContaining("message");
 */
public class AssertThrowsConverter extends AssertionConverterIntentionAction {

    protected AssertThrowsConverter() {
        super("assertThrows");
    }

    /**
     * Modifies the Psi to change a JUnit assertThrows() assertion to an AssertJ assertThatExceptionOfType() one.
     *
     * @param project a reference to the Project object being edited.
     * @param editor  a reference to the object editing the project source
     * @param element a reference to the PSI element currently under the caret
     * @throws IncorrectOperationException Thrown by underlying (Psi model) write action context
     *                                     when manipulation of the psi tree fails.
     */
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        final PsiMethodCallExpression methodCallExpression = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (methodCallExpression == null) {
            return;
        }
        final PsiExpression[] expressions = methodCallExpression.getArgumentList().getExpressions();
        final PsiTypeElement operand = ((PsiClassObjectAccessExpression) expressions[0]).getOperand();
        final PsiClass exceptionClass = PsiUtil.resolveClassInClassTypeOnly(operand.getType());
        if (exceptionClass == null) {
            return;
        }
        final String exceptionQualifiedName = exceptionClass.getQualifiedName();
        if (exceptionQualifiedName == null) {
            return;
        }
        final PsiLambdaExpression lambda = ((PsiLambdaExpression) expressions[1]);
        final PsiExpression lambdaExpression = LambdaUtil.extractSingleExpressionFromBody(lambda.getBody());
        if (lambdaExpression == null) {
            return;
        }
        final PsiLiteralExpression literal = ((PsiLiteralExpression) expressions[2]);

        final String assertionTextExpression = createAssertionTextExpression(exceptionQualifiedName, exceptionClass.getName(), lambdaExpression.getText(), literal.getText());
        final PsiMethodCallExpression newAssertionCall = AssertionFactory.createFormatted(project, assertionTextExpression);
        methodCallExpression.replace(newAssertionCall);
    }

    @NotNull
    private String createAssertionTextExpression(String exceptionQualifiedName, String exceptionName, String lambdaExpression, String message) {
        final AssertionTextExpressionBuilder assertionTextExpressionBuilder;

        final String assertionMethod = AssertJUtils.getCommonThrowsAssertion(exceptionQualifiedName);
        if ("assertThatExceptionOfType".equals(assertionMethod)) {
            assertionTextExpressionBuilder = AssertionTextExpressionBuilder.assertJAssertion(assertionMethod).parameter(exceptionName + ".class");
        } else {
            assertionTextExpressionBuilder = AssertionTextExpressionBuilder.assertJAssertion(assertionMethod).noParameters();
        }
        return assertionTextExpressionBuilder
                .fluentMethodCall("isThrownBy").parameter("() -> " + lambdaExpression)
                .fluentMethodCall("withMessageContaining").parameter(message)
                .build();
    }

    @Override
    protected boolean isAssertionSignatureValid(@Nullable PsiMethod assertion) {
        return AssertionSignatureValidator.isNotNull()
                .and(AssertionSignatureValidator.fullyQualifiedNameEquals("org.junit.jupiter.api.Assertions.assertThrows"))
                .apply(assertion);
    }

    @Override
    protected boolean isArgumentListValid(@NotNull PsiExpressionList assertionCallArguments) {
        final PsiExpression[] expressions = assertionCallArguments.getExpressions();
        return assertionCallArguments.getExpressionCount() == 3
                && AssertionArgumentsValidator.isExceptionClass(expressions[0])
                && AssertionArgumentsValidator.isExecutableLambdaExpression(expressions[1])
                && AssertionArgumentsValidator.isLiteralString(expressions[2]);
    }
}
