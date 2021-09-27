package com.madrakrystian.juneed.template.live;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public interface JavaTestFileValidator {

    static boolean isJavaTest(@NotNull PsiFile file) {
        return hasCorrectSuffix(file.getName()) && isInTestSourceContent(file.getProject(), file.getVirtualFile());
    }

    private static boolean hasCorrectSuffix(@NotNull String fileName) {
        return fileName.endsWith("Test.java") || fileName.endsWith("IT.java");
    }

    private static boolean isInTestSourceContent(@NotNull Project project, @NotNull VirtualFile file) {
        return ProjectRootManager.getInstance(project).getFileIndex()
                .isInTestSourceContent(file);
    }
}
