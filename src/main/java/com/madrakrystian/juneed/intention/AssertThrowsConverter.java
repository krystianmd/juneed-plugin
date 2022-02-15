package com.madrakrystian.juneed.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLambdaExpressionType;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiImmediateClassType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

import static com.madrakrystian.juneed.intention.TextExpressionUtils.expressionLine;

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
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);

        final PsiMethodCallExpression methodCallExpression = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (methodCallExpression == null) {
            return;
        }

        PsiMethodCallExpression newAssertionCall = createAssertionCall(factory, methodCallExpression);

        newAssertionCall = (PsiMethodCallExpression) codeStylist.reformat(newAssertionCall);
        methodCallExpression.replace(newAssertionCall);
    }

    @NotNull
    private PsiMethodCallExpression createAssertionCall(PsiElementFactory factory, PsiMethodCallExpression originalAssertionCall) {
        final PsiExpression[] expressions = originalAssertionCall.getArgumentList().getExpressions();
        return (PsiMethodCallExpression) factory
                .createExpressionFromText(createAssertionTextExpression(expressions), originalAssertionCall);
    }

    private String createAssertionTextExpression(PsiExpression[] expressions) {
        return new StringJoiner("\n")
                .add(expressionLine("Assertions.assertThatExceptionOfType", expressions[0].getText()))
                .add(expressionLine(".isThrownBy", expressions[1].getText()))
                .add(expressionLine(".withMessageContaining", expressions[2].getText()))
                .toString();
    }

    @Override
    boolean isArgumentListValid(PsiExpressionList argumentList) {
        return argumentList.getExpressionCount() == 3
                && argumentList.getExpressionTypes()[0] instanceof PsiImmediateClassType
                && argumentList.getExpressionTypes()[1] instanceof PsiLambdaExpressionType
                && argumentList.getExpressionTypes()[2] instanceof PsiClassReferenceType;
    }
}
