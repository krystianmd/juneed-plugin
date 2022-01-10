package com.madrakrystian.juneed.template.live;


import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.roots.TestSourcesFilter.isTestSources;

/**
 * Utility class for test consistencies on {@link PsiElement}.
 */
final class TestUtils {

    private TestUtils() {}

    /**
     * Checks if given element file is located under test sources root.
     */
    static boolean isWithinTestSources(@NotNull PsiElement element) {
        final PsiFile file = element.getContainingFile().getOriginalFile();
        return isTestSources(file.getVirtualFile(), file.getProject());
    }
}
