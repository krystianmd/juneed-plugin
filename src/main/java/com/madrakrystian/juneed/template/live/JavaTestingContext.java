package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class JavaTestingContext extends TemplateContextType {

    public static final String CONTEXT_ID = "JAVA_TESTING";
    public static final String CONTEXT_PRESENTABLE_NAME = "Java testing";

    protected JavaTestingContext() {
        super(CONTEXT_ID, CONTEXT_PRESENTABLE_NAME);
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        final PsiFile contextFile = templateActionContext.getFile();

        // todo check if within test directory
        return contextFile.getName().endsWith("Test.java");
    }
}
