package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.List;

public abstract class AbstractSimpleCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();

        if(MyDomUtils.isMoquiProject(psiElement.getProject())) {
            result.addAllElements(findCompletionItem(psiElement));
        }

    }

    public abstract List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement);
}
