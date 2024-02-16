package org.moqui.idea.plugin.service.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.service.EditCompletionService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EntitiesPackageEditCompletionService implements EditCompletionService {
    public static final EntitiesPackageEditCompletionService INSTANCE = new EntitiesPackageEditCompletionService();

    private EntitiesPackageEditCompletionService(){}

    private final Set<String> allowedTags = Set.of(Entity.TAG_NAME, ViewEntity.TAG_NAME, ExtendEntity.TAG_NAME);

    @Override
    public boolean isSupport(PsiElement psiElement) {
        if(MyDomUtils.isNotAttributeValue(psiElement)) return  false;

        Optional<String> attributeName = MyDomUtils.getCurrentAttributeName(psiElement);
        if (attributeName.isEmpty()) return false;
        if(!Entity.ATTR_PACKAGE.equals(attributeName.get())) return false;


        Optional<String> tagName = MyDomUtils.getCurrentTagName(psiElement);
        if(tagName.isEmpty()) return false;

        if(!allowedTags.contains(tagName.get())) return false;

        if(!(EntityUtils.isEntitiesFile(psiElement.getContainingFile()))) return false;

        return true;
    }

    @Override
    public List<LookupElementBuilder> findCompletionItem(PsiElement psiElement) {
        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
        Set<String> packageSet = EntityUtils.getEntityAttributes(psiElement.getProject(),Entity.ATTR_PACKAGE,"");
        lookupElementBuilders.addAll(
          packageSet.stream().map(item->{
              return LookupElementBuilder.create(item).withCaseSensitivity(true);
          }).collect(Collectors.toList())
        );

        return lookupElementBuilders;
    }
}
