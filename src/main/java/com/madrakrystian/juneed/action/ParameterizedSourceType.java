package com.madrakrystian.juneed.action;

/**
 * Type of parametrized test method with name of its code template file.
 */
public enum ParameterizedSourceType {
    INTS {
        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Ints Source.java";
        }
    },
    STRINGS {
        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Strings Source.java";
        }
    },
    ENUMS {
        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Enums Source.java";
        }
    },
    NULL_EMPTY {
        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With NullAndEmpty Source.java";
        }
    },
    METHOD {
        @Override
        public String getFileTemplateName() {
            return "JUnit5 Parametrized Test Method With Method Source.java";
        }
    };

    protected abstract String getFileTemplateName();
}
