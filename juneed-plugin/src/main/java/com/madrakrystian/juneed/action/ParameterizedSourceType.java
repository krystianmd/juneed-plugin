package com.madrakrystian.juneed.action;

import com.intellij.codeInsight.template.Template;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.madrakrystian.juneed.template.TemplateBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Type of parametrized test method with metadata about its template.
 */
public enum ParameterizedSourceType {
    INTS {
        @NonNls
        private static final String PREDEFINED_INTS_VARIABLE = "${INTS}";

        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Ints Source.java";
        }

        @Override
        protected Template getFileTemplate(@NotNull Project project) {
            return TemplateBuilder.builder(project, getTemplateText(project))
                    .predefinedVariable(PREDEFINED_INTS_VARIABLE)
                    .predefinedVariable(NAME_VARIABLE, DEFAULT_NAME_VARIABLE_VALUE)
                    .build();
        }
    },
    STRINGS {
        @NonNls
        private static final String PREDEFINED_STRINGS_VARIABLE = "${STRINGS}";

        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Strings Source.java";
        }

        @Override
        protected Template getFileTemplate(@NotNull Project project) {
            return TemplateBuilder.builder(project, getTemplateText(project))
                    .predefinedVariable(PREDEFINED_STRINGS_VARIABLE)
                    .predefinedVariable(NAME_VARIABLE, DEFAULT_NAME_VARIABLE_VALUE)
                    .build();
        }
    },
    ENUMS {
        @NonNls
        private static final String PREDEFINED_ENUMS_VARIABLE = "${ENUM}";

        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Enums Source.java";
        }

        @Override
        protected Template getFileTemplate(@NotNull Project project) {
            return TemplateBuilder.builder(project, getTemplateText(project))
                    .predefinedVariable(PREDEFINED_ENUMS_VARIABLE) // in the annotation
                    .predefinedVariable(NAME_VARIABLE, DEFAULT_NAME_VARIABLE_VALUE)
                    .predefinedVariable(PREDEFINED_ENUMS_VARIABLE) // as the parameter type
                    .build();
        }
    },
    NULL_EMPTY {
        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With NullAndEmpty Source.java";
        }

        @Override
        protected Template getFileTemplate(@NotNull Project project) {
            return TemplateBuilder.builder(project, getTemplateText(project))
                    .predefinedVariable(NAME_VARIABLE, DEFAULT_NAME_VARIABLE_VALUE)
                    .predefinedVariable(PREDEFINED_PARAM_TYPE_VARIABLE)
                    .build();
        }
    },
    METHOD {
        @NonNls
        private static final String PREDEFINED_METHOD_VARIABLE = "${METHOD}";

        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Method Source.java";
        }

        @Override
        protected Template getFileTemplate(@NotNull Project project) {
            return TemplateBuilder.builder(project, getTemplateText(project))
                    .predefinedVariable(PREDEFINED_METHOD_VARIABLE)
                    .predefinedVariable(NAME_VARIABLE, DEFAULT_NAME_VARIABLE_VALUE)
                    .predefinedVariable(PREDEFINED_PARAM_TYPE_VARIABLE)
                    .build();
        }
    };

    @NonNls
    private static final String NAME_VARIABLE = "${NAME}";
    @NonNls
    private static final String DEFAULT_NAME_VARIABLE_VALUE = "test";

    @NonNls
    private static final String PREDEFINED_PARAM_TYPE_VARIABLE = "${PARAM_TYPE}";

    protected abstract String getFileTemplateName();

    protected abstract Template getFileTemplate(@NotNull Project project);

    protected String getTemplateText(@NotNull Project project) {
        return FileTemplateManager.getInstance(project).getCodeTemplate(getFileTemplateName()).getText();
    }
}
