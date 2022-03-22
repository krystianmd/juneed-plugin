package com.madrakrystian.juneed.template;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TemplateBuilder {

    private int currentIndex = 0;

    private final Template liveTemplate;
    private final String templateText;

    private final Set<String> variables;

    private TemplateBuilder(Template liveTemplate, String templateText) {
        this.liveTemplate = liveTemplate;
        this.templateText = templateText;
        this.variables = new HashSet<>();
    }

    public static TemplateBuilder builder(@NotNull Project project, @NotNull String templateText) {
        return new TemplateBuilder(TemplateManager.getInstance(project).createTemplate("", ""), templateText);
    }

    public TemplateBuilder predefinedVariable(String variable) {
        return predefinedVariable(variable, "");
    }

    public TemplateBuilder predefinedVariable(String variable, String defaultValue) {
        int variableIndex = findVariableIndex(variable);

        if (variables.contains(variable)){
            liveTemplate.addVariableSegment(removeVariableSyntax(variable));
        } else {
            variables.add(variable);

            Expression expression = new ConstantNode(defaultValue);
            liveTemplate.addTextSegment(templateText.substring(currentIndex, variableIndex));
            liveTemplate.addVariable(removeVariableSyntax(variable), expression, expression, true);
        }

        currentIndex = variableIndex + variable.length();
        return this;
    }

    private int findVariableIndex(String variable) {
        int variableIndex = templateText.indexOf(variable, currentIndex);
        if (variableIndex == -1) {
            throw new IllegalStateException("Variable=" + variable + " does not exist in the template");
        }
        return variableIndex;
    }

    @NotNull
    private String removeVariableSyntax(String variable) {
        return variable.replace("\\{|\\}", "");
    }

    public Template build() {
        liveTemplate.addTextSegment(templateText.substring(currentIndex));
        liveTemplate.setToIndent(true);
        liveTemplate.setToReformat(true);
        liveTemplate.setToShortenLongNames(true);
        return liveTemplate;
    }
}
