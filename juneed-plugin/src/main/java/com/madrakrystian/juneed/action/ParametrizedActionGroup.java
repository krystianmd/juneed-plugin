package com.madrakrystian.juneed.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom action group for parameterized method actions.
 * <p>
 * For some reason {@link com.intellij.openapi.actionSystem.DefaultActionGroup} cannot show popup in GenerateGroup
 * simply via xml attribute, thus one of the solutions is to include the actions in that strange dynamic-static way
 */
public class ParametrizedActionGroup extends ActionGroup {

    @NonNls
    private final static AnAction INTS_SOURCE_ACTION = new GenerateParameterizedTestWithIntsSourceAction("Ints Source");
    @NonNls
    private final static AnAction STRINGS_SOURCE_ACTION = new GenerateParameterizedTestWithStringsSourceAction("Strings Source");
    @NonNls
    private final static AnAction NULL_AND_EMPTY_SOURCE_ACTION = new GenerateParameterizedTestWithNullAndEmptySourceAction("Null And Empty Source");
    @NonNls
    private final static AnAction ENUM_SOURCE_ACTION = new GenerateParameterizedTestWithEnumsSourceAction("Enums Source");
    @NonNls
    private final static AnAction METHOD_SOURCE_ACTION = new GenerateParameterizedTestWithMethodSourceAction("Method Source");

    @NonNls
    private final static AnAction[] ACTIONS = {INTS_SOURCE_ACTION, STRINGS_SOURCE_ACTION, NULL_AND_EMPTY_SOURCE_ACTION,
            ENUM_SOURCE_ACTION, METHOD_SOURCE_ACTION,};

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return ACTIONS;
    }
}
