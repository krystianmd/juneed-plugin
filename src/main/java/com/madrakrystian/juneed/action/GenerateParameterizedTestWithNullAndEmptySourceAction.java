package com.madrakrystian.juneed.action;

import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithNullAndEmptySourceAction extends GenerateParametrizedTestMethodAction {
    public GenerateParameterizedTestWithNullAndEmptySourceAction(@NotNull String presentationText) {
        super(ParameterizedSourceType.NULL_EMPTY, presentationText);
    }
}
