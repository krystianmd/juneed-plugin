package com.madrakrystian.juneed.intention;

import com.intellij.codeInspection.util.IntentionFamilyName;
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
import com.madrakrystian.juneed.utils.expression.FluentAssertionExpressionTextBuilder;
import com.madrakrystian.juneed.utils.methodCall.AssertionFactory;
import com.madrakrystian.juneed.utils.AssertJUtils;
import com.madrakrystian.juneed.utils.expression.AssertionArgumentsValidator;
import com.madrakrystian.juneed.utils.method.AssertionSignatureVerifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implements an intention action to replace JUnit assertThrows() assertion with appropriate AssertJ one.
 * <p>
 * e.g. before applying intention
 * assertThrows(FoobarException.class, () -> fooBarService.apply(), "message");
 * <p>
 * after applying intention
 * Assertions.assertThatExceptionOfType(FoobarException.class)
 *      .isThrownBy(() -> fooBarService.apply())
 *      .withMessageContaining("message");
 * <p>
 * Includes also enriched ones.
 * <p>
 * e.g. before applying intention
 * assertThrows(IllegalArgumentException.class, () -> fooBarService.apply(), "message");
 * <p>
 * after applying intention
 * Assertions.assertThatIllegalException()
 *      .isThrownBy(() -> fooBarService.apply())
 *      .withMessageContaining("message");
 */
public class AssertThrowsConverter extends AssertionConverterIntentionAction {

    protected AssertThrowsConverter() {
        super("assertThrows");
    }

    /**
     * Invokes the intention.
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

        final String assertionTextExpression = createAssertionTextExpression(exceptionQualifiedName, lambdaExpression.getText(), literal.getText());
        final PsiMethodCallExpression newAssertionCall = AssertionFactory.create(project, assertionTextExpression);
        methodCallExpression.replace(newAssertionCall);
    }

    @NotNull
    private String createAssertionTextExpression(@NotNull String exceptionQualifiedName, @NotNull String lambdaExpression, @NotNull String message) {
        final FluentAssertionExpressionTextBuilder textExpressionBuilder;

        final String assertionMethod = AssertJUtils.getCommonThrowsAssertion(exceptionQualifiedName);
        if ("assertThatExceptionOfType".equals(assertionMethod)) {
            textExpressionBuilder = AssertJUtils.expressionTextBuilder(assertionMethod).parameter(exceptionQualifiedName + ".class");
        } else {
            textExpressionBuilder = AssertJUtils.expressionTextBuilder(assertionMethod).noParameters();
        }
        return textExpressionBuilder
                .methodCall("isThrownBy").parameter("() -> " + lambdaExpression)
                .methodCall("withMessageContaining").parameter(message)
                .build();
    }

    @Override
    protected boolean isAssertionSignatureValid(@Nullable PsiMethod assertion) {
        return AssertionSignatureVerifier.isNotNull()
                .and(AssertionSignatureVerifier.fullyQualifiedNameEquals("org.junit.jupiter.api.Assertions.assertThrows"))
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

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Convert assertThrows assertion";
    }
}
