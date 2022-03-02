package com.madrakrystian.juneed.utils;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.madrakrystian.juneed.inspection.CommonExceptionAssertionInspection.FQ_ILLEGAL_ARGUMENT_EXCEPTION;
import static com.madrakrystian.juneed.inspection.CommonExceptionAssertionInspection.FQ_ILLEGAL_STATE_EXCEPTION;
import static com.madrakrystian.juneed.inspection.CommonExceptionAssertionInspection.FQ_IO_EXCEPTION;
import static com.madrakrystian.juneed.inspection.CommonExceptionAssertionInspection.FQ_NPE_EXCEPTION;

public final class AssertJUtils {

    @NonNls
    public static final String ASSERTJ_ASSERTIONS_QUALIFIED_IMPORT = "org.assertj.core.api.Assertions";

    @NotNull
    public static String getCommonThrowsAssertion(String exceptionQualifiedName) {
        switch (exceptionQualifiedName) {
            case FQ_IO_EXCEPTION:
                return "assertThatIOException";
            case FQ_NPE_EXCEPTION:
                return "assertThatNullPointerException";
            case FQ_ILLEGAL_STATE_EXCEPTION:
                return "assertThatIllegalStateException";
            case FQ_ILLEGAL_ARGUMENT_EXCEPTION:
                return "assertThatIllegalArgumentException";
            default:
                return "assertThatExceptionOfType";
        }
    }
}
