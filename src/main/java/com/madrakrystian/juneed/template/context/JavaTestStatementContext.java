package com.madrakrystian.juneed.template.context;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.siyeh.ig.psiutils.TestUtils.isInTestCode;

/**
 * Provides context for assertions and fluent assertions in live templates.
 */
public class JavaTestStatementContext extends JavaCodeContextType.Statement {

    /**
     * Checks if given {@link PsiElement} matches java statement context conditions and if it's within test sources.
     */
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
        return super.isInContext(element) && isInTestCode(element);
    }

    /**
     * To override context description which is provided in {@link Statement} constructor.
     */
    @Override
    public @NotNull String getPresentableName() {
        return "Java test statement";
    }

    /**
     * To override context id which is provided in {@link Statement} constructor.
     */
    @Override
    public @NotNull @NonNls String getContextId() {
        return "JAVA_TEST_STATEMENT";
    }
}
