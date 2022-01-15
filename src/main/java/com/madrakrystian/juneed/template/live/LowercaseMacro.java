package com.madrakrystian.juneed.template.live;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Converts a string into lowercase.
 *
 * For example, lowercase("A Hobbit From Shire") returns "a hobbit from shire"
 */
public class LowercaseMacro extends MacroBase {

    public LowercaseMacro() {
        super("lowercase", "lowercase(String)");
    }

    @Override
    protected @Nullable Result calculateResult(Expression @NotNull [] params, ExpressionContext context, boolean quick) {
        String text = getTextResult(params, context, true);
        if (text == null) {
            return null;
        }
        if (!text.isBlank()) {
            text = text.toLowerCase(Locale.ROOT);
        }
        return new TextResult(text);
    }

    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        return context instanceof JavaStringContextType;
    }
}
