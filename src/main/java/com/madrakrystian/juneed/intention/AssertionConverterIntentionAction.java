package com.madrakrystian.juneed.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.madrakrystian.juneed.TestUtils.isWithinTestSources;

public abstract class AssertionConverterIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {

    protected final String assertionName;

    protected AssertionConverterIntentionAction(@NotNull String assertionName) {
        this.assertionName = assertionName;
    }

    /**
     * Checks if the caret is placed on {@link AssertionConverterIntentionAction#assertionName} method reference.
     * If this is true then this intention is shown in the available intentions list
     *
     * @param project a reference to the Project object being edited.
     * @param editor  a reference to the object editing the project source
     * @param element a reference to the PSI element currently under the caret
     * @return {@code true} if the caret is placed on the correct assertion or {@code false} otherwise
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (!(element instanceof PsiJavaToken)) {
            return false;
        }

        final PsiJavaToken token = (PsiJavaToken) element;
        if (token.getTokenType() != JavaTokenType.IDENTIFIER && !token.textMatches(assertionName)) {
            return false;
        }

        final PsiElement parent = token.getParent();
        if (parent instanceof PsiReferenceExpression) {

            final PsiElement grandparent = parent.getParent();
            if (grandparent instanceof PsiMethodCallExpression) {
                return isArgumentListValid(((PsiMethodCallExpression) grandparent).getArgumentList());
            }
        }
        return false;
    }

    abstract boolean isArgumentListValid(PsiExpressionList expressionList);

    /**
     * Checks whether this intention is available in file.
     */
    @Override
    public boolean checkFile(@Nullable PsiFile file) {
        return file != null && isWithinTestSources(file);
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "AssertionConverterIntention";
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "Convert to AssertJ fluent assertion";
    }
}
