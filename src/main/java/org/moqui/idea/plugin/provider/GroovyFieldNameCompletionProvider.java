package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns;
import org.moqui.idea.plugin.dom.converter.insertHandler.AliasNameInsertionHandler;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

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

        lookupElementBuilders.add(
            LookupElementBuilder.create("fieldName1")
                    .withCaseSensitivity(false)
                    .withTailText("Trail1")
                    .withTypeText("Type1")
                    .withIcon(MoquiIcons.EntityTag)
        );

        lookupElementBuilders.add(
            LookupElementBuilder.create("fieldName2")
                    .withCaseSensitivity(false)
                    .withTailText("Trail2")
                    .withTypeText("Type2")
                    .withIcon(MoquiIcons.ViewEntityTag)
        );

        return lookupElementBuilders;
    }


}
