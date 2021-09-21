package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class JavaTestingContext extends TemplateContextType {

    private static final String CONTEXT_ID = "JAVA_TESTING";
    private static final String CONTEXT_PRESENTABLE_NAME = "Java testing";

    protected JavaTestingContext() {
        super(CONTEXT_ID, CONTEXT_PRESENTABLE_NAME);
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        final PsiFile contextFile = templateActionContext.getFile();

        return isAJavaTest(contextFile) && isWithinTestModule(contextFile.getOriginalFile());
    }

    private boolean isAJavaTest(PsiFile contextFile) {
        return contextFile.getName().endsWith("Test.java");
    }

    private boolean isWithinTestModule(@NotNull PsiFile originalFile) {
        final Module module = ModuleUtil.findModuleForFile(originalFile);

        if (module != null) {
            return module.getName().endsWith("test");
        }
        return false;
    }
}
