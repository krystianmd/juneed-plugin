package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.madrakrystian.juneed.template.live.JavaTestFileValidator.*;

public class JavaTestStatementContext extends JavaCodeContextType.Statement {

    private static final String CONTEXT_ID = "JAVA_TEST_STATEMENT";
    private static final String CONTEXT_PRESENTABLE_NAME = "Java test statement";

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
        return super.isInContext(element) // checks if element is inside java statement
                && isJavaTest(element.getContainingFile().getOriginalFile());
    }

    @Override
    public @NotNull String getPresentableName() {
        return CONTEXT_PRESENTABLE_NAME;
    }

    @Override
    public @NotNull @NonNls String getContextId() {
        return CONTEXT_ID;
    }
}
