package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.roots.TestSourcesFilter.isTestSources;

/**
 * Provides context of java test statement.
 * Can be used in the plugin.xml file to provide liveTemplateContext implementation
 */
public class JavaTestStatementContext extends JavaCodeContextType.Statement {

    private static final String CONTEXT_ID = "JAVA_TEST_STATEMENT";
    private static final String CONTEXT_PRESENTABLE_NAME = "Java test statement";

    /**
     * Checks if given {@link PsiElement} is in java test statement context and is located under test sources root.
     *
     * @param element part of the PSI tree e.g. Java File
     * @return true if given element fulfills all test statement conditions, false otherwise
     */
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
        PsiFile file = element.getContainingFile().getOriginalFile();
        return super.isInContext(element) && isTestSources(file.getVirtualFile(), file.getProject());
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
