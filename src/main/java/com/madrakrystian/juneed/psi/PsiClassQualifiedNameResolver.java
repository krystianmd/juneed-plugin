package com.madrakrystian.juneed.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class PsiClassQualifiedNameResolver {

    /**
     * Resolves the qualified name of the {@link PsiTypeElement} class.
     */
    public static Optional<String> resolveClassName(@NotNull PsiTypeElement typeElement) {
        final PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(typeElement.getType());
        return Optional.ofNullable(psiClass).map(PsiClass::getQualifiedName);
    }
}
