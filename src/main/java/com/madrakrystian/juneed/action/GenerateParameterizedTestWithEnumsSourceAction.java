package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.template.Template;
import com.intellij.openapi.project.Project;
import com.madrakrystian.juneed.template.TemplateBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithEnumsSourceAction extends ParametrizedTestMethodAction {
    public GenerateParameterizedTestWithEnumsSourceAction() {
        super(new EnumSourceHandler());
    }

    private static class EnumSourceHandler extends ParametrizedTestMethodHandler {
        @NonNls
        private static final String PREDEFINED_ENUMS_VARIABLE = "${ENUM}";

        public EnumSourceHandler() {
            super(ParameterizedSourceType.ENUMS);
        }

        @Override
        protected Template buildTemplateWithTextSegments(@NotNull Project project, @NotNull String text) {
            return TemplateBuilder.builder(project, text)
                    .predefinedVariable(PREDEFINED_ENUMS_VARIABLE) // in the annotation
                    .predefinedVariable(NAME_VARIABLE, "test")
                    .predefinedVariable(PREDEFINED_ENUMS_VARIABLE) // as the parameter type
                    .build();
        }
    }
}
