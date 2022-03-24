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
    private static final Logger LOG = Logger.getInstance("#com.madrakrystian.juneed.inspection.ComparingReferencesInspection");

    /**
     * Identifier of the Assertions.assertThatExceptionOfType method used for asserting an exception occurrence.
     */
    @NonNls
    private static final String OLD_ASSERTION_QUALIFIED_NAME = "org.assertj.core.api.Assertions.assertThatExceptionOfType";

    @NonNls
    public static final String FQ_ILLEGAL_ARGUMENT_EXCEPTION = "java.lang.IllegalArgumentException";
    @NonNls
    public static final String FQ_ILLEGAL_STATE_EXCEPTION = "java.lang.IllegalStateException";
    @NonNls
    public static final String FQ_NPE_EXCEPTION = "java.lang.NullPointerException";
    @NonNls
    public static final String FQ_IO_EXCEPTION = "java.io.IOException";

    /**
     * List of common exceptions that were chosen for enriched syntax of throw assertions.
     */
    @NonNls
    private static final List<String> FQ_COMMON_EXCEPTIONS =
            List.of(FQ_ILLEGAL_ARGUMENT_EXCEPTION, FQ_ILLEGAL_STATE_EXCEPTION, FQ_NPE_EXCEPTION, FQ_IO_EXCEPTION);


    private final LocalQuickFix quickFix = new AssertionQuickFix();

    /**
     * Inspects method call expression of the {@value #OLD_ASSERTION_QUALIFIED_NAME} assertion
     * with one of the common exceptions as an argument.
     *
     * The visitor must not be recursive and must be thread-safe.
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
            private static final String DESCRIPTION_TEMPLATE = "Found an old syntax, use assertThat%s instead";

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
     * Provides a solution to inspection problem by manipulating the PSI tree to use an appropriate latter syntax instead.
     */
    private static class AssertionQuickFix implements LocalQuickFix {

        /**
         * Returns a partially localized string for the quick fix intention.
         * Used by the test code for this plugin.
         *
         * @return Quick fix short name.
         */
        @NotNull
        @Override
        public String getName() {
            return "Replace with latter enriched AssertJ syntax";
        }

        /**
         * This method manipulates the PSI tree to replace the assertion with a more verbose one.
         * <p>
         * e.g. before applying fix
         * Assertions.assertThatExceptionOfType(NullPointerException.class)
         * ...
         * <p>
         * after applying fix
         * Assertions.assertThatNullPointerException()
         * ...
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
                final String expressionText = AssertJUtils.expressionTextBuilder(commonAssertion).noParameters()
                        .build();

                final PsiMethodCallExpression enrichedAssertion = AssertionFactory.create(project, expressionText);
                assertionCall.replace(enrichedAssertion);
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        @NotNull
        public String getFamilyName() {
            return getName();
        }
    }
}
