package com.madrakrystian.juneed.utils.method;

import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

/**
 * Helper interface for {@link String} assertions names.
 */
public interface AssertionMethodQualifier extends UnaryOperator<String> {

    /**
     * Adds static import for fully qualified method name or qualifies method name if the import was not successful.
     *
     * @param fqClass a fully qualified assertion class
     * @param originalMethodCall a context of the method
     * @return fully qualified assertion method
     */
    static AssertionMethodQualifier qualifyWith(@NotNull String fqClass, @NotNull PsiMethodCallExpression originalMethodCall) {
        return assertion -> {
            if (ImportUtils.addStaticImport(fqClass, assertion, originalMethodCall)) {
                return assertion;
            }
            return qualifyWith(fqClass).apply(assertion);
        };
    }

    /**
     * Qualifies method with given fully qualified assertion class.
     *
     * @param fqClass a fully qualified assertion class
     * @return fully qualified assertion method
     */
    static AssertionMethodQualifier qualifyWith(@NotNull String fqClass) {
        return assertion -> fqClass + "." + assertion;
    }
}
