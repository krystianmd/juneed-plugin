package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.template.Template;
import com.intellij.openapi.project.Project;
import com.madrakrystian.juneed.template.TemplateBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithStringsSourceAction extends ParametrizedTestMethodAction {
    public GenerateParameterizedTestWithStringsSourceAction() {
        super(new StringsSourceHandler());
    }

    private static class StringsSourceHandler extends ParametrizedTestMethodHandler {
        @NonNls
        private static final String PREDEFINED_STRINGS_VARIABLE = "${STRINGS}";

        public StringsSourceHandler() {
            super(ParameterizedSourceType.STRINGS);
        }

        @Override
        protected Template buildTemplateWithTextSegments(@NotNull Project project, @NotNull String text) {
            return TemplateBuilder.builder(project, text)
                    .predefinedVariable(PREDEFINED_STRINGS_VARIABLE)
                    .predefinedVariable(NAME_VARIABLE, "test")
                    .build();
        }
    }
}
