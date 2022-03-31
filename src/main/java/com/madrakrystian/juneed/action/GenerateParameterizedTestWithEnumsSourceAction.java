package com.madrakrystian.juneed.action;

import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithEnumsSourceAction extends GenerateParametrizedTestMethodAction {
    public GenerateParameterizedTestWithEnumsSourceAction(@NotNull String presentationText) {
        super(ParameterizedSourceType.ENUMS, presentationText);
    }
}
