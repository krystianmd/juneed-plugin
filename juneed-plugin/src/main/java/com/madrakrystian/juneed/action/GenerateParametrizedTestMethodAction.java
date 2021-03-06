package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.codeInsight.hint.HintManager;
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
import com.madrakrystian.juneed.utils.method.MethodFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.siyeh.ig.psiutils.TestUtils.isInTestSourceContent;

public abstract class GenerateParametrizedTestMethodAction extends BaseGenerateAction {
    private final String presentationText;

    /**
     * The only action constructor which should be used for appropriate action creation.
     *
     * @param sourceType       a type of parameterized method source along with its template metadata
     * @param presentationText text which is shown in the generation popup
     */
    public GenerateParametrizedTestMethodAction(@NotNull ParameterizedSourceType sourceType, @NotNull String presentationText) {
        super(new ParametrizedTestMethodHandler(sourceType));
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

    /**
     * Checks whether this action is available in the class.
     */
    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
        return isInTestSourceContent(targetClass);
    }

    static class ParametrizedTestMethodHandler implements CodeInsightActionHandler {
        private final ParameterizedSourceType sourceType;

        protected ParametrizedTestMethodHandler(ParameterizedSourceType sourceType) {
            this.sourceType = sourceType;
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
            if (!CommonRefactoringUtil.checkReadOnlyStatus(file)) {
                return;
            }

            int offset = editor.getCaretModel().getOffset();
            final PsiClass targetClass = findTargetClass(file, offset);
            if (targetClass == null) {
                return;
            }
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiDocumentManager.getInstance(project).commitAllDocuments();
                PsiMethod method = MethodFactory.createMethod("testMethod", file, offset);
                if (method != null) {
                    runMethodTemplate(project, editor, method, targetClass);
                }
            });
        }

        @Nullable
        private PsiClass findTargetClass(@NotNull PsiFile file, int offset) {
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

        private void runMethodTemplate(@NotNull Project project, @NotNull Editor editor, @NotNull PsiMethod method, @NotNull PsiClass targetClass) {
            try {
                TestIntegrationUtils.runTestMethodTemplate(editor, targetClass, method, false, sourceType.getFileTemplate(project));
            } catch (IncorrectOperationException | IllegalStateException e) {
                final String message = "Couldn't generate method: " + e.getMessage();
                HintManager.getInstance().showErrorHint(editor, message);
            }
        }

        @Override
        public boolean startInWriteAction() {
            return false;
        }
    }
}
