package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PsiJavaPatterns.psiMethod;
import static org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns.groovyLiteralExpression;
import static org.moqui.idea.plugin.contributor.GroovyCodeReferenceContributor.ENTITY_FIND_CLASS;

/**

 */
public class GroovyFieldNameCompletionProvider extends AbstractSimpleCompletionProvider {
    public static GroovyFieldNameCompletionProvider of(){
        return new GroovyFieldNameCompletionProvider();
    }
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> CONDITION_PATTERN =
            PlatformPatterns.psiElement().inside(GroovyPatterns.psiElement().andOr(
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("condition").definedInClass(ENTITY_FIND_CLASS))
            ));

    @Override
    public List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement) {

        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();


        return lookupElementBuilders;
    }


}
