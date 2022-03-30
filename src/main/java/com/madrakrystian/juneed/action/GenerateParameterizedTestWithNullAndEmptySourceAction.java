package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.template.Template;
import com.intellij.openapi.project.Project;
import com.madrakrystian.juneed.template.TemplateBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithNullAndEmptySourceAction extends ParametrizedTestMethodAction {
    public GenerateParameterizedTestWithNullAndEmptySourceAction() {
        super(new NullAndEmptySourceHandler(), "Null And Empty Source");
    }

    private static class NullAndEmptySourceHandler extends ParametrizedTestMethodHandler {
        @NonNls
        private static final String PREDEFINED_PARAM_TYPE_VARIABLE = "${PARAM_TYPE}";

        public NullAndEmptySourceHandler() {
            super(ParameterizedSourceType.NULL_EMPTY);
        }

        @Override
        protected Template buildTemplateWithTextSegments(@NotNull Project project, @NotNull String text) {
            return TemplateBuilder.builder(project, text)
                    .predefinedVariable(NAME_VARIABLE, "test")
                    .predefinedVariable(PREDEFINED_PARAM_TYPE_VARIABLE)
                    .build();
        }
    }
}
