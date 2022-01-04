package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.madrakrystian.juneed.template.live.JavaTestSourcePredicates.HAS_TEST_SUFFIX;
import static com.madrakrystian.juneed.template.live.JavaTestSourcePredicates.IS_WITHIN_TEST_SOURCES;

/**
 * Provides context of java test statement.
 * Can be used in the plugin.xml to provide liveTemplateContext implementation
 */
public class JavaTestStatementContext extends JavaCodeContextType.Statement {

    private static final String CONTEXT_ID = "JAVA_TEST_STATEMENT";
    private static final String CONTEXT_PRESENTABLE_NAME = "Java test statement";

    /**
     * Checks if given {@link PsiElement} is in java test statement context.
     *
     * @param element part of the PSI tree e.g. Java File
     * @return true if given element fulfills all statement and java tests conditions, false otherwise
     */
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
        return super.isInContext(element) &&
                HAS_TEST_SUFFIX.and(IS_WITHIN_TEST_SOURCES).test(element.getContainingFile().getOriginalFile());
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
