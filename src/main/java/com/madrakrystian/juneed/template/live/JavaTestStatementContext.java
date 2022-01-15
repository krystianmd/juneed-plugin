package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.madrakrystian.juneed.template.live.TestUtils.isWithinTestSources;

/**
 * Provides context of java test statement.
 * Can be used in the plugin.xml file to provide liveTemplateContext implementation
 */
public class JavaTestStatementContext extends JavaCodeContextType.Statement {

    /**
     * Checks if given {@link PsiElement} matches java statement context conditions and if it's within test sources.
     */
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
        return super.isInContext(element) && isWithinTestSources(element);
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
