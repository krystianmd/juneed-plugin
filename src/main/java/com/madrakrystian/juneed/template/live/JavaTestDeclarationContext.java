package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.madrakrystian.juneed.template.live.TestUtils.isWithinTestSources;

/**
 * Provides context of java test declaration.
 * Can be used in the plugin.xml file to provide liveTemplateContext implementation
 */
public class JavaTestDeclarationContext extends JavaCodeContextType.Declaration {

    private static final String CONTEXT_ID = "JAVA_TEST_DECLARATION";
    private static final String CONTEXT_PRESENTABLE_NAME = "Java test declaration";

    /**
     * Checks if given {@link PsiElement} matches all java declaration context conditions
     * and if it's within test sources.
     *
     * @param element part of the PSI tree e.g. Java File
     * @return true if given element fulfills all test declaration conditions, false otherwise
     */
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
        return super.isInContext(element) && isWithinTestSources(element);
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
