package com.madrakrystian.juneed.utils.methodCall;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;

public final class AssertionFactory {

    /**
     * Creates assertion {@link PsiMethodCallExpression} from expression text and formats it according to the the {@link Project} style.
     */
    public static @NotNull PsiMethodCallExpression create(@NotNull Project project, @NotNull String expressionText) {
        return formatAssertion(project, createAssertion(project, expressionText));
    }

    @NotNull
    private static PsiMethodCallExpression createAssertion(@NotNull Project project, @NotNull String expressionText) {
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        return (PsiMethodCallExpression) factory.createExpressionFromText(expressionText, null);
    }

    @NotNull
    private static PsiMethodCallExpression formatAssertion(@NotNull Project project, @NotNull PsiMethodCallExpression assertion) {
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);
        return (PsiMethodCallExpression) codeStylist
                .reformat(JavaCodeStyleManager.getInstance(project).shortenClassReferences(assertion));
    }
}
