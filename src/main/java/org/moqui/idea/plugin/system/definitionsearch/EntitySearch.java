package org.moqui.idea.plugin.system.definitionsearch;

import org.moqui.idea.plugin.util.CustomNotifier;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * 定义Entity的转跳
 */
public class EntitySearch extends QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters> {

    public EntitySearch(){
        super(true);
    }

    @Override
    public void processQuery(DefinitionsScopedSearch.@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiElement> consumer) {
        final PsiElement element = queryParameters.getElement();

        CustomNotifier.info(element.getProject(),"current PsiElement class is: " + element.getClass().toString() );

        System.out.println("current PsiElement class is: " + element.getClass().toString() );

    }
}
