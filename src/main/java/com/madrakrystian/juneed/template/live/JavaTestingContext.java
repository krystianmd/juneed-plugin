package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
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
        final PsiFile contextFile = templateActionContext.getFile().getOriginalFile();

        return isJavaTest(contextFile)
                && isInTestSourceContent(contextFile.getProject(), contextFile.getVirtualFile());
    }

    private boolean isJavaTest(@NotNull PsiFile contextFile) {
        final String name = contextFile.getName();
        return name.endsWith("Test.java") || name.endsWith("IT.java");
    }

    private boolean isInTestSourceContent(@NotNull Project project, @NotNull VirtualFile file) {
        return ProjectRootManager.getInstance(project).getFileIndex()
                .isInTestSourceContent(file);
    }
}
