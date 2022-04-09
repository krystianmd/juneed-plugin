package com.madrakrystian.juneed.action;

import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithIntsSourceAction extends GenerateParametrizedTestMethodAction {
    public GenerateParameterizedTestWithIntsSourceAction(@NotNull String presentationText) {
        super(ParameterizedSourceType.INTS, presentationText);
    }
}
