package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.template.Template;
import com.intellij.openapi.project.Project;
import com.madrakrystian.juneed.template.TemplateBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithIntsSourceAction extends ParametrizedTestMethodAction {
    public GenerateParameterizedTestWithIntsSourceAction() {
        super(new IntsSourceHandler());
    }

    private static class IntsSourceHandler extends ParametrizedTestMethodHandler {
        @NonNls
        private static final String PREDEFINED_INTS_VARIABLE = "${INTS}";

        public IntsSourceHandler() {
            super(ParameterizedSourceType.INTS);
        }

        @Override
        protected Template buildTemplateWithTextSegments(@NotNull Project project, @NotNull String text) {
            return TemplateBuilder.builder(project, text)
                    .predefinedVariable(PREDEFINED_INTS_VARIABLE)
                    .predefinedVariable(NAME_VARIABLE, "test")
                    .build();
        }
    }
}
