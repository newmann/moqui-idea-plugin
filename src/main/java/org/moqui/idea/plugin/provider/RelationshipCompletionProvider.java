package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.ArrayList;
import java.util.List;

public class RelationshipCompletionProvider extends CompletionProvider<CompletionParameters> {
//    private final  static Logger LOGGER = Logger.getInstance(ServiceCallCompletionProvider.class);
    public static RelationshipCompletionProvider of(){
        return new RelationshipCompletionProvider() ;
    }
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> RELATIONSHIP_PATTERN =
            PlatformPatterns.psiElement()
                    .inside(
                            XmlPatterns.xmlAttributeValue().andOr(
                                XmlPatterns.xmlAttributeValue(Detail.ATTR_RELATIONSHIP)
                                        .inside(
                                                XmlPatterns.xmlTag().withLocalName(Detail.TAG_NAME)
                                        ),
                                XmlPatterns.xmlAttributeValue(MemberRelationship.ATTR_RELATIONSHIP)
                                        .inside(
                                                XmlPatterns.xmlTag().withLocalName(MemberRelationship.TAG_NAME)
                                        )
                            )
                    );

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet result) {

        PsiElement psiElement = parameters.getPosition();
        if(! MyDomUtils.isMoquiProject(psiElement.getProject())) return;
        lookupRelationship(psiElement,result);
    }


    private void lookupRelationship(@NotNull PsiElement psiElement, @NotNull CompletionResultSet result){

        EntityUtils.getRelationshipListByRelationshipElement(psiElement).forEach(item->{
            EntityUtils.getRelatedNameFromRelationship(item).ifPresent(name ->{
                result.addElement(LookupElementBuilder.create(name)
                        .withCaseSensitivity(true)
                        .withIcon(MyIcons.RelationshipTag));

            });

        });
    }

}
