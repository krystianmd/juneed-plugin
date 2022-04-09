package com.madrakrystian.juneed.action;

import org.jetbrains.annotations.NotNull;

public class GenerateParameterizedTestWithStringsSourceAction extends GenerateParametrizedTestMethodAction {
    public GenerateParameterizedTestWithStringsSourceAction(@NotNull String presentationText) {
        super(ParameterizedSourceType.STRINGS, presentationText);
    }
}
