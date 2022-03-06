package com.madrakrystian.juneed.utils.expression;

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

public final class AssertionArgumentsValidator {

    private AssertionArgumentsValidator() {}

    public static boolean isExceptionClass(@NotNull PsiExpression expression) {
        if(expression instanceof PsiClassObjectAccessExpression) {
            final PsiTypeElement operand = ((PsiClassObjectAccessExpression) expression).getOperand();
            return isThrowableInheritorClass(operand);
        }
        return false;
    }

    private static boolean isThrowableInheritorClass(@NotNull PsiTypeElement operand) {
        final PsiClass expressionClass = PsiUtil.resolveClassInClassTypeOnly(operand.getType());
        return InheritanceUtil.isInheritor(expressionClass, CommonClassNames.JAVA_LANG_THROWABLE);
    }

    public static boolean isLambdaExpression(@NotNull PsiExpression expression) {
        return expression instanceof PsiLambdaExpression &&
                ((PsiLambdaExpression) expression).getParameterList().getParametersCount() == 0;
    }

    public static boolean isLiteralString(@NotNull PsiExpression expression) {
        return expression instanceof PsiLiteralExpression
                && ((PsiLiteralExpression) expression).getValue() instanceof String;
    }
}
