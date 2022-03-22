package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.template.Template;
import com.intellij.openapi.project.Project;
import com.madrakrystian.juneed.template.TemplateBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithMethodSourceAction extends ParametrizedTestMethodAction {
    public GenerateParameterizedTestWithMethodSourceAction() {
        super(new MethodSourceHandler());
    }

    private static class MethodSourceHandler extends ParametrizedTestMethodHandler {
        @NonNls
        private static final String PREDEFINED_METHOD_VARIABLE = "${METHOD}";
        @NonNls
        private static final String PREDEFINED_PARAM_TYPE_VARIABLE = "${PARAM_TYPE}";

        public MethodSourceHandler() {
            super(ParameterizedSourceType.METHOD);
        }

        @Override
        protected Template buildTemplateWithTextSegments(@NotNull Project project, @NotNull String text) {
            return TemplateBuilder.builder(project, text)
                    .predefinedVariable(PREDEFINED_METHOD_VARIABLE)
                    .predefinedVariable(NAME_VARIABLE, "test")
                    .predefinedVariable(PREDEFINED_PARAM_TYPE_VARIABLE)
                    .build();
        }
    }
}
