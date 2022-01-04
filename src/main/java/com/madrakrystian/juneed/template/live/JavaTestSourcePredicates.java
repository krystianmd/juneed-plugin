package com.madrakrystian.juneed.template.live;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static com.intellij.openapi.roots.TestSourcesFilter.isTestSources;

/**
 * Basic predicates used to determine java test consistencies on {@link PsiFile}.
 */
class JavaTestSourcePredicates {

    /**
     * Checks if given file is located under test sources root.
     */
    static final Predicate<@NotNull PsiFile> IS_WITHIN_TEST_SOURCES = file ->
            isTestSources(file.getVirtualFile(), file.getProject());

    /**
     * Checks if given file ends with appropriate test suffix.
     */
    static final Predicate<@NotNull PsiFile> HAS_TEST_SUFFIX = file -> {
        final String fileName = file.getName();
        return fileName.endsWith("Test.java") || fileName.contains("IT.java");
    };
}
