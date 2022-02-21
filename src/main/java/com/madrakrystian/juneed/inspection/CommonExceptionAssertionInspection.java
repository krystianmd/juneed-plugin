package com.madrakrystian.juneed.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiTypeElement;
import com.intellij.util.IncorrectOperationException;
import com.madrakrystian.juneed.psi.PsiMethodCallExpressionFactory;
import com.madrakrystian.juneed.psi.PsiClassQualifiedNameResolver;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommonExceptionAssertionInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final Logger LOG = Logger.getInstance("#com.madrakrystian.juneed.inspection.ComparingReferencesInspection");

    /**
     * Identifier of the Assertions.assertThatExceptionOfType method used for asserting an exception occurrence.
     */
    @NonNls
    private static final String ASSERT_THAT_EXCEPTION_OF_TYPE = "assertThatExceptionOfType";

    /**
     * List of common exceptions that were chosen for enriched syntax of throw assertions.
     */
    @NonNls
    private static final List<String> COMMON_EXCEPTIONS =
            List.of("java.lang.IllegalArgumentException", "java.lang.IllegalStateException", "java.lang.NullPointerException", "java.io.IOException");


    private final LocalQuickFix quickFix = new AssertionQuickFix();


    /**
     * Inspects method call expression of the {@value #ASSERT_THAT_EXCEPTION_OF_TYPE} assertion
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

                final PsiReferenceExpression methodExpression = expression.getMethodExpression();
                final PsiExpressionList arguments = expression.getArgumentList();
                if (!isMethodSignatureEligibleForInspection(methodExpression.getReferenceName(), arguments)) {
                    return;
                }

                final PsiExpression argument = arguments.getExpressions()[0];
                if (argument instanceof PsiClassObjectAccessExpression) {
                    final PsiTypeElement operand = ((PsiClassObjectAccessExpression) argument).getOperand();

                    PsiClassQualifiedNameResolver.resolveClassName(operand)
                            .filter(COMMON_EXCEPTIONS::contains)
                            .map(CommonExceptionAssertionInspection::getExceptionName)
                            .ifPresent(exceptionName -> registerAssertionProblem(expression, exceptionName));
                }
            }

            private boolean isMethodSignatureEligibleForInspection(String methodName, PsiExpressionList arguments) {
                return ASSERT_THAT_EXCEPTION_OF_TYPE.equals(methodName) && arguments.getExpressionCount() == 1;
            }

            private void registerAssertionProblem(PsiMethodCallExpression assertion, String exceptionName) {
                final String formattedDescription = String.format(DESCRIPTION_TEMPLATE, exceptionName);
                holder.registerProblem(assertion, formattedDescription, quickFix);
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
                PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) descriptor.getPsiElement();

                final PsiExpressionList arguments = methodCallExpression.getArgumentList();
                final PsiTypeElement operand = ((PsiClassObjectAccessExpression) arguments.getExpressions()[0]).getOperand();

                PsiClassQualifiedNameResolver.resolveClassName(operand)
                        .map(this::createExpressionText)
                        .map(expressionText -> PsiMethodCallExpressionFactory.createFormatted(project, expressionText))
                        .ifPresent(methodCallExpression::replace);
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        @NotNull
        private String createExpressionText(String qualifiedExceptionClassName) {
            final String exceptionName = getExceptionName(qualifiedExceptionClassName);
            return "org.assertj.core.api.Assertions.assertThat" + exceptionName + "()";
        }

        @NotNull
        public String getFamilyName() {
            return getName();
        }

    }

    @NotNull
    private static String getExceptionName(String exceptionQualifiedName) {
        return exceptionQualifiedName.substring(exceptionQualifiedName.lastIndexOf('.') + 1);
    }
}
