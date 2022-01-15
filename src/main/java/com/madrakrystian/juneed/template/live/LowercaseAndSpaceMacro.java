package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroBase;
import com.intellij.psi.codeStyle.NameUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Converts a string into lowercase.
 *
 * For example, lowercaseAndSpace("AHobbitFromShire") returns "a hobbit from shire"
 */
public class LowercaseAndSpaceMacro extends MacroBase {

    public LowercaseAndSpaceMacro() {
        super("lowercaseAndSpace", "lowercaseAndSpace(String)");
    }

    @Override
    protected @Nullable Result calculateResult(Expression @NotNull [] params, ExpressionContext context, boolean quick) {
        String text = getTextResult(params, context, true);
        return StringUtils.isBlank(text) ? new TextResult("")
                : new TextResult(NameUtil.splitWords(text, ' ', String::toLowerCase));
    }

    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        return context instanceof JavaStringContextType;
    }
}
