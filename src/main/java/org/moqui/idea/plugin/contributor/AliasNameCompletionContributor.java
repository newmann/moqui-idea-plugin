package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.insert.AliasInsertObject;
import org.moqui.idea.plugin.dom.converter.insert.AliasNameInsertionHandler;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.AbstractIndexEntity;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByPsiElement;

/**
 * 处理viewEntity定义中的alias的名称，一般情况下都是对应到某个成员表的字段，但还是可以自定义，然后通过field来匹配
 * 所以只需要提供Auto Complete，无须创建Reference
 * 选定字段后，自动将对应表的别名插入
 *
 */
public class AliasNameCompletionContributor extends CompletionContributor {

  AliasNameCompletionContributor(){
    extend(CompletionType.BASIC, getCapture(), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();
        result.addAllElements(findCompletionItem(psiElement));
      }
    });
  }
  private static PsiElementPattern.Capture<PsiElement> getCapture() {
    return psiElement().inside(
            XmlPatterns.xmlAttributeValue(Alias.ATTR_NAME).inside(
                    XmlPatterns.xmlTag().withLocalName(ViewEntity.TAG_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(Entities.TAG_NAME)
                    )
            )
    );
  }

  private List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement){
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

    ViewEntity viewEntity = getLocalDomElementByPsiElement(psiElement,ViewEntity.class).orElse(null);
    if (viewEntity == null) return lookupElementBuilders;
    Collection<String> enteredAliasNameCollection = viewEntity.getAliasList().stream()
            .map(Alias::getName).map(MyDomUtils::getValueOrEmptyString).filter(MyStringUtils::isNotEmpty)
            .toList();

    Collection<AbstractMemberEntity> abstractMemberEntityCollection = EntityUtils.getViewEntityAbstractMemberEntity(viewEntity);

    String alias;
    AbstractIndexEntity abstractIndexEntity;
    Icon icon;
    List<IndexAbstractField> indexAbstractFieldList;

    for(AbstractMemberEntity abstractMemberEntity: abstractMemberEntityCollection) {
      alias =MyDomUtils.getValueOrEmptyString(abstractMemberEntity.getEntityAlias());

      abstractIndexEntity = EntityUtils.getViewEntityAbstractIndexEntityByAlias(viewEntity, alias).orElse(null);

      if(abstractIndexEntity != null){
        indexAbstractFieldList = abstractIndexEntity.getIndexAbstractFieldList().orElse(null);
        if(indexAbstractFieldList != null){
          for (IndexAbstractField indexAbstractField : indexAbstractFieldList) {
            String fieldName = MyDomUtils.getValueOrEmptyString(indexAbstractField.getName());
            if(!enteredAliasNameCollection.contains(fieldName)) { //跳过已经输入的alias
              if (abstractIndexEntity instanceof IndexEntity) {
                icon = MoquiIcons.EntityTag;
              } else {
                icon = MoquiIcons.ViewEntityTag;
              }
              AliasInsertObject aliasInsertObject = AliasInsertObject.of(alias);
              lookupElementBuilders.add(
                      LookupElementBuilder.create(aliasInsertObject, fieldName)
                              .withInsertHandler(AliasNameInsertionHandler.INSTANCE)
                              .withCaseSensitivity(false)
                              .withIcon(icon)
                              .withTypeText(alias)
              );
            }
          }

        };
      }
    }
    return lookupElementBuilders;
  }
}
