package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlTagNameProvider;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.dom.model.Set;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityFacadeTagNameProvider implements XmlTagNameProvider {
    @Override
    public void addTagNameVariants(List<LookupElement> list, @NotNull XmlTag xmlTag, String s) {
        if(MyDomUtils.isMoquiDataDefineTag(xmlTag)) {
            Project project = xmlTag.getProject();
            //添加所在Entity的reference，并且权重更高
            List<String> relationshipEntityName = new ArrayList<>();
            XmlTag parentTag = xmlTag.getParentTag();
            if(parentTag != null) {
                IndexEntity indexEntity = EntityUtils.getIndexEntityByName(project,parentTag.getName()).orElse(null);
                if(indexEntity != null) {
                    indexEntity.getRelationshipList().forEach(
                            relationship -> {
                                String shortAlias = MyDomUtils.getValueOrEmptyString(relationship.getShortAlias());
                                EntityNameDescriptor entityNameDescriptor = EntityNameDescriptor.of(MyDomUtils.getValueOrEmptyString(relationship.getRelated()));

                                relationshipEntityName.add(entityNameDescriptor.getEntityName());

                                if(MyStringUtils.isEmpty(shortAlias)) {
                                    shortAlias = entityNameDescriptor.getEntityName();
                                }

                                LookupElement lookupElement=LookupElementBuilder.create(shortAlias)
                                        .appendTailText("[relationship]",true)
                                        .withCaseSensitivity(true)
                                        .withTypeText(entityNameDescriptor.getFullName());

                                list.add(PrioritizedLookupElement.withPriority(lookupElement,0.1));
                            }
                    );
                }
            }

            //添加所有Entity
            EntityUtils.getAllEntityCollection(project).forEach(
                    entity->{

                        String fullName = EntityUtils.getFullNameFromEntity(entity);
                        //不建议使用Entity的全称，因为无法为pacakgeName创建PsiReference，在修改名称是会有问题，所以推荐直接用简称或EntityName
//                        list.add(LookupElementBuilder.create(fullName)
//                                .withCaseSensitivity(false)
//                        );
                        String entityName = MyDomUtils.getValueOrEmptyString(entity.getEntityName());

                        if(!relationshipEntityName.contains(entityName)) { //添加在Relationship中不存在的Entity
                            list.add(LookupElementBuilder.create(entityName)
                                    .appendTailText("[entity-name]", true)
                                    .withCaseSensitivity(true)
                                    .withTypeText(fullName)
                            );
                            String shortAlias = MyDomUtils.getValueOrEmptyString(entity.getShortAlias());
                            if (MyStringUtils.isNotEmpty(shortAlias)) {
                                list.add(LookupElementBuilder.create(shortAlias)
                                        .appendTailText("[short-alias]", true)
                                        .withCaseSensitivity(true)
                                        .withTypeText(fullName)
                                );
                            }
                        }
                    }
            );
        }
    }
}
