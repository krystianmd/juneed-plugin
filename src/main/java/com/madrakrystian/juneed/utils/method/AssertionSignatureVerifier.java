package com.madrakrystian.juneed.utils.method;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Helper class for {@link PsiMethod} assertion validations.
 */
public interface AssertionSignatureVerifier extends Function<PsiMethod, Boolean> {

    static AssertionSignatureVerifier isNotNull() {
        return Objects::nonNull;
    }

    static AssertionSignatureVerifier fullyQualifiedNameEquals(@NotNull String fqName) {
        return assertion -> {
            final PsiClass methodClass = assertion.getContainingClass();
            if (methodClass == null) {
                return false;
            }
            final String qualifiedName = methodClass.getQualifiedName() + "." + assertion.getName();
            return fqName.equals(qualifiedName);
        };
    }

    static AssertionSignatureVerifier parametersCountEquals(int count) {
        return assertion -> assertion.getParameterList().getParametersCount() == count;
    }

    default AssertionSignatureVerifier and(AssertionSignatureVerifier other) {
        return assertion -> {
            Boolean result = this.apply(assertion);
            return result.equals(Boolean.TRUE) ? other.apply(assertion) : result;
        };
    }
}
