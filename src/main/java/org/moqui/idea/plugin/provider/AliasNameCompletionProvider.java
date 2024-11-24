package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.insertHandler.AliasInsertObject;
import org.moqui.idea.plugin.dom.converter.insertHandler.AliasNameInsertionHandler;
import org.moqui.idea.plugin.dom.model.AbstractMemberEntity;
import org.moqui.idea.plugin.dom.model.Alias;
import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.service.AbstractIndexEntity;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByPsiElement;
/**
 * 处理viewEntity定义中的alias的名称，一般情况下都是对应到某个成员表的字段，但还是可以自定义，然后通过field来匹配
 * 所以只需要提供Auto Complete，无须创建Reference
 * 选定字段后，自动将对应表的别名插入
 *
 */
public class AliasNameCompletionProvider extends AbstractSimpleCompletionProvider {
    public static AliasNameCompletionProvider of(){
        return new AliasNameCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> ALIAS_NAME_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue(Alias.ATTR_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(ViewEntity.TAG_NAME).inside(
                                    XmlPatterns.xmlTag().withLocalName(Entities.TAG_NAME)
                            )
                    )
            );
    @Override
    public List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement) {
        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

        ViewEntity viewEntity = getLocalDomElementByPsiElement(psiElement, ViewEntity.class).orElse(null);
        if (viewEntity == null) return lookupElementBuilders;
        Collection<String> enteredAliasNameCollection = viewEntity.getAliasList().stream()
                .map(Alias::getName).map(MyDomUtils::getValueOrEmptyString).filter(MyStringUtils::isNotEmpty)
                .toList();

        Collection<AbstractMemberEntity> abstractMemberEntityCollection = EntityUtils.getViewEntityAbstractMemberEntity(viewEntity);

        String alias;
        AbstractIndexEntity abstractIndexEntity;
        Icon icon;
        List<IndexAbstractField> indexAbstractFieldList;

        for (AbstractMemberEntity abstractMemberEntity : abstractMemberEntityCollection) {
            alias = MyDomUtils.getValueOrEmptyString(abstractMemberEntity.getEntityAlias());

            abstractIndexEntity = EntityUtils.getViewEntityAbstractIndexEntityByAlias(viewEntity, alias).orElse(null);

            if (abstractIndexEntity != null) {
                indexAbstractFieldList = abstractIndexEntity.getIndexAbstractFieldList().orElse(null);
                if (indexAbstractFieldList != null) {
                    for (IndexAbstractField indexAbstractField : indexAbstractFieldList) {
                        String fieldName = MyDomUtils.getValueOrEmptyString(indexAbstractField.getName());
                        if (!enteredAliasNameCollection.contains(fieldName)) { //跳过已经输入的alias
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

                }
                ;
            }
        }
        return lookupElementBuilders;
    }
}
