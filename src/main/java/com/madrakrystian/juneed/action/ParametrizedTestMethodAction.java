package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.generation.GenerateMembersUtil;
import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.codeInsight.generation.PsiGenerationInfo;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.template.Template;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testIntegration.TestIntegrationUtils;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

import static com.intellij.execution.junit.JUnitUtil.isJUnit5TestClass;

public abstract class ParametrizedTestMethodAction extends BaseGenerateAction {
    private final String presentationText;

    public ParametrizedTestMethodAction(ParametrizedTestMethodHandler handler, String presentationText) {
        super(handler);
        this.presentationText = presentationText;
    }

    /**
     * Enables view of the action in the popup elements list.
     */
    @Override
    protected void update(@NotNull Presentation presentation, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        presentation.setText(getTemplateText());
        presentation.setEnabledAndVisible(isValidForFile(project, editor, file));
    }

    @Override
    public @Nullable @NlsActions.ActionText String getTemplateText() {
        return presentationText;
    }

    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
        return isJUnit5TestClass(targetClass, false);
    }

    static abstract class ParametrizedTestMethodHandler implements CodeInsightActionHandler {
        @NonNls
        protected static final String NAME_VARIABLE = "${NAME}";

        private final ParameterizedSourceType sourceType;

        protected ParametrizedTestMethodHandler(ParameterizedSourceType sourceType) {
            this.sourceType = sourceType;
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
            if (!CommonRefactoringUtil.checkReadOnlyStatus(file)) {
                return;
            }
            final PsiClass targetClass = findTargetClass(editor, file);
            if (targetClass == null) {
                return;
            }
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiDocumentManager.getInstance(project).commitAllDocuments();
                PsiMethod method = generateMethod(editor, file);
                if (method != null) {
                    runMethodTemplate(project, editor, method, targetClass);
                }
            });
        }

        @Nullable
        private PsiClass findTargetClass(@NotNull Editor editor, @NotNull PsiFile file) {
            int offset = editor.getCaretModel().getOffset();
            PsiElement element = file.findElementAt(offset);
            if (element == null) {
                return null;
            }
            PsiClass containingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class, false);
            if (containingClass == null || containingClass.isInterface()) {
                return null;
            }
            return containingClass;
        }

        private PsiMethod generateMethod(@NotNull Editor editor, @NotNull PsiFile file) {
            PsiMethod method = TestIntegrationUtils.createDummyMethod(file);
            final PsiGenerationInfo<PsiMethod> info = OverrideImplementUtil.createGenerationInfo(method);

            int offset = editor.getCaretModel().getOffset();
            GenerateMembersUtil.insertMembersAtOffset(file, offset, Collections.singletonList(info));

            final PsiMethod member = info.getPsiMember();
            return member != null ? CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(member) : null;
        }

        private void runMethodTemplate(@NotNull Project project, @NotNull Editor editor, @NotNull PsiMethod method, @NotNull PsiClass targetClass) {
            try {
                final FileTemplate fileTemplate = FileTemplateManager.getInstance(project).getCodeTemplate(sourceType.getFileTemplateName());
                final Template template = buildTemplateWithTextSegments(project, fileTemplate.getText());
                TestIntegrationUtils.runTestMethodTemplate(editor, targetClass, method, false, template);
            } catch (IncorrectOperationException | IllegalStateException e) {
                final String message = "Couldn't generate method: " + e.getMessage();
                HintManager.getInstance().showErrorHint(editor, message);
            }
        }

        protected abstract Template buildTemplateWithTextSegments(@NotNull Project project, @NotNull String text);

        @Override
        public boolean startInWriteAction() {
            return false;
        }
    }
}
