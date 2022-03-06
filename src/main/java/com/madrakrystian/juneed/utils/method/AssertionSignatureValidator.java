package com.madrakrystian.juneed.utils.method;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for {@link PsiMethod} assertion validations.
 */
public final class AssertionSignatureValidator {

    private AssertionSignatureValidator() {}

    public static boolean fullyQualifiedNameEquals(@Nullable PsiMethod assertion, @NotNull String fqName) {
        if (assertion == null) {
            return false;
        }

        final PsiClass methodClass = assertion.getContainingClass();
        if (methodClass == null) {
            return false;
        }
        final String qualifiedName = methodClass.getQualifiedName() + "." + assertion.getName();
        return fqName.equals(qualifiedName);
    }

    public static boolean hasParametersCount(@Nullable PsiMethod assertion, int count) {
        if (assertion == null) {
            return false;
        }
        return assertion.getParameterList().getParametersCount() == count;
    }
}
