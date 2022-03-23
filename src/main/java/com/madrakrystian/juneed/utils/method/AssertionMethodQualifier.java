package com.madrakrystian.juneed.utils.method;

import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

/**
 * Helper interface for {@link String} assertions names.
 */
public interface AssertionMethodQualifier extends UnaryOperator<String> {

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
