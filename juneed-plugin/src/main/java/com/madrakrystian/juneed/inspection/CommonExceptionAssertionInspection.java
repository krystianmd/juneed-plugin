package com.madrakrystian.juneed.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import com.madrakrystian.juneed.utils.method.AssertionSignatureVerifier;
import com.madrakrystian.juneed.utils.methodCall.AssertionFactory;
import com.madrakrystian.juneed.utils.AssertJUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommonExceptionAssertionInspection extends AbstractBaseJavaLocalInspectionTool {
    private static final Logger LOG = Logger.getInstance("#com.madrakrystian.juneed.inspection.CommonExceptionAssertionInspection");

    @NonNls
    public static final String FQ_ILLEGAL_ARGUMENT_EXCEPTION = "java.lang.IllegalArgumentException";
    @NonNls
    public static final String FQ_ILLEGAL_STATE_EXCEPTION = "java.lang.IllegalStateException";
    @NonNls
    public static final String FQ_NPE_EXCEPTION = "java.lang.NullPointerException";
    @NonNls
    public static final String FQ_IO_EXCEPTION = "java.io.IOException";

    @NonNls
    private static final String OLD_ASSERTION_QUALIFIED_NAME = "org.assertj.core.api.Assertions.assertThatExceptionOfType";

    /**
     * Common exceptions that were chosen for enriched syntax of throw assertion.
     */
    @NonNls
    private static final List<String> FQ_COMMON_EXCEPTIONS =
            List.of(FQ_ILLEGAL_ARGUMENT_EXCEPTION, FQ_ILLEGAL_STATE_EXCEPTION, FQ_NPE_EXCEPTION, FQ_IO_EXCEPTION);


    private final LocalQuickFix quickFix = new AssertionQuickFix();

    /**
     * Inspects method call expression of the "assertThatExceptionOfType" assertion with one of the common exceptions as an argument.
     *
     * @param holder     object for visitor to register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return non-null visitor for this inspection.
     * @see JavaElementVisitor
     */
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            /**
             * Defines a short message shown to a user signaling the inspection found a problem.
             */
            @NonNls
            private static final String DESCRIPTION_TEMPLATE = "Assertion can be replaced with 'assertThat%s' instead";

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);

                boolean isAssertionSignatureValid = AssertionSignatureVerifier.isNotNull()
                        .and(AssertionSignatureVerifier.fullyQualifiedNameEquals(OLD_ASSERTION_QUALIFIED_NAME))
                        .and(AssertionSignatureVerifier.parametersCountEquals(1))
                        .apply(expression.resolveMethod());
                if (!isAssertionSignatureValid) {
                    return;
                }

                final PsiExpressionList arguments = expression.getArgumentList();
                if (arguments.getExpressionCount() != 1) {
                    return;
                }
                final PsiExpression argument = arguments.getExpressions()[0];
                if (argument instanceof PsiClassObjectAccessExpression) {
                    final PsiTypeElement operand = ((PsiClassObjectAccessExpression) argument).getOperand();
                    final PsiClass exceptionClass = PsiUtil.resolveClassInClassTypeOnly(operand.getType());
                    if (exceptionClass == null) {
                        return;
                    }
                    final String exceptionQualifiedName = exceptionClass.getQualifiedName();
                    if (FQ_COMMON_EXCEPTIONS.contains(exceptionQualifiedName)) {
                        registerAssertionProblem(expression, exceptionClass.getName());
                    }
                }
            }

            private void registerAssertionProblem(PsiMethodCallExpression assertionCall, String exceptionName) {
                final String formattedDescription = String.format(DESCRIPTION_TEMPLATE, exceptionName);
                holder.registerProblem(assertionCall, formattedDescription, quickFix);
            }
        };
    }

    /**
     * Quick fix for the assertion registered problem.
     */
    private static class AssertionQuickFix implements LocalQuickFix {

        /**
         * This method manipulates the PSI tree to replace the assertion with a more verbose one.
         * <p>
         * e.g. before applying fix
         * assertThatExceptionOfType(NullPointerException.class)
         * <p>
         * after applying fix
         * assertThatNullPointerException()
         *
         * @param project    The project that contains the file being edited.
         * @param descriptor A problem found by this inspection.
         */
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                PsiMethodCallExpression assertionCall = (PsiMethodCallExpression) descriptor.getPsiElement();

                final PsiExpressionList arguments = assertionCall.getArgumentList();
                final PsiTypeElement operand = ((PsiClassObjectAccessExpression) arguments.getExpressions()[0]).getOperand();
                final PsiClass exceptionClass = PsiUtil.resolveClassInClassTypeOnly(operand.getType());
                if (exceptionClass == null) {
                    return;
                }
                final String exceptionQualifiedName = exceptionClass.getQualifiedName();
                if (exceptionQualifiedName == null) {
                    return;
                }
                final String commonAssertion = AssertJUtils.getCommonThrowsAssertion(exceptionQualifiedName);
                final String expressionText = AssertJUtils.expressionTextBuilder(commonAssertion, assertionCall).noParameters()
                        .build();

                final PsiMethodCallExpression enrichedAssertion = AssertionFactory.create(project, expressionText);
                assertionCall.replace(enrichedAssertion);
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        /**
         * Text that appears in the "Apply Fix" popup.
         */
        @NotNull
        @Override
        public String getFamilyName() {
            return "Replace with enriched assertion";
        }
    }
}
