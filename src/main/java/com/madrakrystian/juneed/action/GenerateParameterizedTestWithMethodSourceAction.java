package com.madrakrystian.juneed.action;

import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithMethodSourceAction extends GenerateParametrizedTestMethodAction {
    public GenerateParameterizedTestWithMethodSourceAction(@NotNull String presentationText) {
        super(ParameterizedSourceType.METHOD, presentationText);
    }
}
