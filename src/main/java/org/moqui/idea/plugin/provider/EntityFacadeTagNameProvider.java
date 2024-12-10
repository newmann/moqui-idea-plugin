package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlTagNameProvider;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.util.EntityFacadeXmlUtils;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.List;
import java.util.Optional;

public class EntityFacadeTagNameProvider implements XmlTagNameProvider {
    @Override
    public void addTagNameVariants(List<LookupElement> list, @NotNull XmlTag xmlTag, String s) {
        if(MyDomUtils.isMoquiDataDefineTag(xmlTag)) {
            EntityUtils.getAllEntityCollection(xmlTag.getProject()).forEach(
                    entity->{
                        String fullName = EntityUtils.getFullNameFromEntity(entity);
                        list.add(LookupElementBuilder.create(fullName)
                                .withCaseSensitivity(false)
                        );

                        list.add(LookupElementBuilder.create(MyDomUtils.getValueOrEmptyString(entity.getEntityName()))
                                .appendTailText("[entity-name]",true)
                                .withCaseSensitivity(false)
                                .withTypeText(fullName)
                        );
                        String shortAlias = MyDomUtils.getValueOrEmptyString(entity.getShortAlias());
                        if(MyStringUtils.isNotEmpty(shortAlias)) {
                            list.add(LookupElementBuilder.create(shortAlias)
                                    .appendTailText("[short-alias]", true)
                                    .withCaseSensitivity(false)
                                    .withTypeText(fullName)
                            );
                        }

                    }
            );
        }
    }
}
