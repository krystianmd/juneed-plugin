package com.madrakrystian.juneed.utils.method;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.generation.GenerateMembersUtil;
import com.intellij.codeInsight.generation.PsiGenerationInfo;
import com.intellij.psi.JVMElementFactories;
import com.intellij.psi.JVMElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public final class MethodFactory {

    private MethodFactory() {
    }

    /**
     * Creates an empty void method.
     *
     * @param methodName name of the created method
     * @param file       file of the method
     * @param offset     position of the created method
     * @return method reference
     */
    @Nullable
    public static PsiMethod createMethod(@NotNull String methodName, @NotNull PsiFile file, int offset) {
        final PsiMethod method = createVoidMethod(methodName, file);
        if (method == null) return null;

        final PsiGenerationInfo<PsiMethod> info = new PsiGenerationInfo<>(method);
        GenerateMembersUtil.insertMembersAtOffset(file, offset, Collections.singletonList(info));

        final PsiMethod member = info.getPsiMember();
        return member != null ? CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(member) : null;
    }

    @Nullable
    private static PsiMethod createVoidMethod(@NotNull String methodName, @NotNull PsiFile file) {
        JVMElementFactory factory = JVMElementFactories.getFactory(file.getLanguage(), file.getProject());
        if (factory == null) {
            return null;
        }
        return factory.createMethod(methodName, PsiType.VOID);
    }
}
