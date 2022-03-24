package com.madrakrystian.juneed.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.siyeh.ig.psiutils.TestUtils.isInTestSourceContent;

public abstract class AssertionConverterIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {

    protected final String assertionName;

    protected AssertionConverterIntentionAction(@NotNull String assertionName) {
        this.assertionName = assertionName;
    }

    /**
     * Checks if the caret is placed on {@link AssertionConverterIntentionAction#assertionName} method reference
     * and if assertion signature and actual argument list is valid.
     *
     * @param project a reference to the Project object being edited.
     * @param editor  a reference to the object editing the project source
     * @param element a reference to the PSI element currently under the caret
     * @return {@code true} then this intention is shown in the available intentions list
     *         {@code false} then this intention is unavailable
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (!(element instanceof PsiJavaToken)) {
            return false;
        }

        final PsiJavaToken token = (PsiJavaToken) element;
        if (!isAssertionNameIdentifier(token)) { // fail-fast test
            return false;
        }

        final PsiMethodCallExpression assertionCall = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (assertionCall == null) {
            return false;
        }
        return isAssertionSignatureValid(assertionCall.resolveMethod()) && isArgumentListValid(assertionCall.getArgumentList());
    }

    private boolean isAssertionNameIdentifier(@NotNull PsiJavaToken token) {
        return token.getTokenType() == JavaTokenType.IDENTIFIER && token.textMatches(assertionName);
    }

    abstract protected boolean isAssertionSignatureValid(@Nullable PsiMethod assertion);

    abstract protected boolean isArgumentListValid(@NotNull PsiExpressionList assertionCallArguments);

    /**
     * Checks whether this intention is available in file, does not run isAvailable if false.
     */
    @Override
    public boolean checkFile(@Nullable PsiFile file) {
        return isInTestSourceContent(file);
    }

    /**
     * Text shown in the intentions list.
     */
    @Override
    public @IntentionName @NotNull String getText() {
        return "Convert to fluent assertion";
    }
}
