package com.madrakrystian.juneed.utils.method;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Helper interface for {@link PsiMethod} assertion validations.
 */
public interface AssertionSignatureVerifier extends Function<PsiMethod, Boolean> {

    static AssertionSignatureVerifier isNotNull() {
        return Objects::nonNull;
    }

    /**
     * Checks if computed fully qualified assertion method name equals given.
     */
    static AssertionSignatureVerifier fullyQualifiedNameEquals(@NotNull String fqName) {
        return assertion -> {
            final PsiClass methodClass = assertion.getContainingClass();
            if (methodClass == null) {
                return false;
            }
            final String qualifiedClassName = methodClass.getQualifiedName();
            if (qualifiedClassName == null) {
                return false;
            }
            final String qualifiedName = AssertionMethodQualifier.qualifyWith(qualifiedClassName).apply(assertion.getName());
            return fqName.equals(qualifiedName);
        };
    }

    /**
     * Checks if method parameters number equals given.
     */
    static AssertionSignatureVerifier parametersCountEquals(int count) {
        return assertion -> assertion.getParameterList().getParametersCount() == count;
    }

    /**
     * Helper method for combining {@link AssertionSignatureVerifier} conditions.
     */
    default AssertionSignatureVerifier and(AssertionSignatureVerifier other) {
        return assertion -> {
            Boolean result = this.apply(assertion);
            return result.equals(Boolean.TRUE) ? other.apply(assertion) : result;
        };
    }
}
