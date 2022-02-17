package com.madrakrystian.juneed;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;

public final class AssertionFormatter {

    /**
     * Formats assertion {@link PsiMethodCallExpression} with style of given {@link Project}.
     */
    public static PsiMethodCallExpression format(@NotNull Project project, @NotNull PsiMethodCallExpression assertion) {
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);
        return (PsiMethodCallExpression) codeStylist
                .reformat(JavaCodeStyleManager.getInstance(project).shortenClassReferences(assertion));
    }
}
