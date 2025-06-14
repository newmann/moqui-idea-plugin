package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityFacadeElementDescriptor implements XmlElementDescriptor {
    public static EntityFacadeElementDescriptor of(XmlTag xmlTag){
        return  new EntityFacadeElementDescriptor(xmlTag);
    }

    private final XmlTag myXmlTag;
//    private final Field myField;
    EntityFacadeElementDescriptor(XmlTag xmlTag){
        myXmlTag = xmlTag;
    }


    @Override
    public String getQualifiedName() {
        return myXmlTag.getName();
    }

    @Override
    public String getDefaultName() {
        return "";
    }

    @Override
    public XmlElementDescriptor[] getElementsDescriptors(XmlTag xmlTag) {
        return new XmlElementDescriptor[0];
    }

    @Nullable
    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag xmlTag, XmlTag xmlTag1) {
        //xmlTag1为xmlTag的父Tag
        return EntityFacadeElementDescriptor.of(xmlTag);
    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag xmlTag) {
        if(xmlTag == null) return XmlAttributeDescriptor.EMPTY;

        List<XmlAttributeDescriptor> resultList = new ArrayList<>();
        if(EntityFacadeXmlUtils.isEntityFacadeDefineTag(xmlTag)){
            switch (xmlTag.getName()) {
                case EntityFacadeXml.TAG_NAME->{resultList.add(EntityFacadeXmlTypeAttributeDescriptor.of());}
                case SeedData.TAG_NAME ->{}
                default ->{
                    EntityFacadeXmlTagDescriptor descriptor = EntityFacadeXmlTagDescriptor.of(xmlTag);
                    //只有当前tag不是field时，才添加
                    if (!descriptor.getIsField()) {
                        //对主键字段进行控制，如果主键在父级实体中存在，则不显示。注意，父级实体可能有多个
                        List<String> parentEntityPKList = getTagParentsEntityPKList(xmlTag);
                        EntityUtils.getIndexEntityByName(xmlTag.getProject(), descriptor.getEntityName())
                                .ifPresent(indexEntity -> {
                                            indexEntity.getFieldList()
                                                    .stream()
                                                    .filter(field -> !parentEntityPKList.contains(MyDomUtils.getValueOrEmptyString(field.getName())))
                                                    .forEach(
                                                            field -> resultList.add(EntityFacadeFieldAttributeDescriptor.of(field)));
                                            //添加lastUpdatedStamp字段
                                            resultList.add(EntityFacadeLastUpdatedStampFieldAttributeDescriptor.of());
                                        }
                                );

                    }
                    //            String entityName = MyDomUtils.getEntityNameInEntityFacadeXml(xmlTag).orElse(MyStringUtils.EMPTY_STRING);
//            if(MyStringUtils.isNotEmpty(entityName)) {
//                EntityUtils.getIndexEntityByName(xmlTag.getProject(), entityName)
//                        .ifPresent(indexEntity -> indexEntity.getFieldList().forEach(
//                                field -> resultList.add(MoquiEntityFieldAttributeDescriptor.of(field)))
//                        );
//            }
                }
            }
        }

        return resultList.toArray(XmlAttributeDescriptor.EMPTY);
    }

    /**
     * 获取父级实体中不需要再输入的字段，
     * 1、如果relationship中有定义，则根据定义获取过滤的字段名称，
     * 2、如果relationship中没有定义，则取当前实体的PK
     * 3、如果不是relationship则直接取父实体的PK
     * @param xmlTag 待处理的实体
     * @return  List<String> 待处理实体可以不用输入的字符列表
     */
    private List<String> getTagParentsEntityPKList(@NotNull XmlTag xmlTag){
        List<String> resultList = new ArrayList<>();

        Project project = xmlTag.getProject();
        XmlTag parentTag = xmlTag.getParentTag();

        EntityFacadeXmlTagDescriptor curTagDescriptor = EntityFacadeXmlTagDescriptor.of(xmlTag);

        IndexEntity curIndexEntity = EntityUtils.getIndexEntityByName(project,curTagDescriptor.getEntityName()).orElse(null);
        if(curIndexEntity == null) return resultList;

        while(parentTag!= null && EntityFacadeXmlUtils.isNotEntityFacadeRootTag(parentTag)){
            EntityFacadeXmlTagDescriptor  parentTagDescriptor = EntityFacadeXmlTagDescriptor.of(parentTag);
//            EntityUtils.getIndexEntityByName(project, descriptor.getEntityName())
//                    .ifPresent(indexEntity -> indexEntity.getFieldList()
//                            .stream()
//                            .filter(field->MyDomUtils.getValueOrFalseBoolean(field.getIsPk()))
//                            .forEach(
//                            field -> resultList.add(MyDomUtils.getValueOrEmptyString(field.getName())))
//                    );
            IndexEntity parentIndexEntity = EntityUtils.getIndexEntityByName(project,parentTagDescriptor.getEntityName()).orElse(null);
            if(parentIndexEntity == null) break;
            Relationship relationship = parentIndexEntity.getRelationshipByName(curTagDescriptor.getTagName()).orElse(null);

            if(relationship== null) {
                for(Field pkField: parentIndexEntity.getPkFieldList()) {
                    resultList.add(MyDomUtils.getValueOrEmptyString(pkField.getName()));
                }

            }else{
                if(relationship.getKeyMapList().isEmpty()) {
                    //没有配置KeyMap，就是表示当前表的pk和主表中的字段名一样。
                    for(Field pkField: curIndexEntity.getPkFieldList()) {
                        resultList.add(MyDomUtils.getValueOrEmptyString(pkField.getName()));
                    }

                }else {
                    for (KeyMap keyMap : relationship.getKeyMapList()) {
                        String relatedFieldName = MyDomUtils.getValueOrEmptyString(keyMap.getRelated());

                        if (relatedFieldName.equals(MyStringUtils.EMPTY_STRING)) {
                            if (curIndexEntity.getPkFieldList().size() == 1) {
                                resultList.add(MyDomUtils.getValueOrEmptyString(curIndexEntity.getPkFieldList().get(0).getName()));
                            } else {
                                resultList.add(MyDomUtils.getValueOrEmptyString(keyMap.getFieldName()));//这个时候，keyMap的fieldName和PK一样
                            }
                        } else {
                            resultList.add(relatedFieldName);
                        }
                    }
                }
            }

            curIndexEntity = parentIndexEntity;//每一层考虑本层的对应关系
            curTagDescriptor = parentTagDescriptor;
            parentTag = parentTag.getParentTag();
        }
        return resultList;
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(String s, @Nullable XmlTag xmlTag) {
        return Arrays.stream(getAttributesDescriptors(xmlTag))
                .filter(xmlAttributeDescriptor -> xmlAttributeDescriptor.getName().equals(s))
                .findFirst().orElse(null);
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute xmlAttribute) {
        return getAttributeDescriptor(xmlAttribute.getName(),myXmlTag);
    }

    @Override
    public @Nullable XmlNSDescriptor getNSDescriptor() {
        return null;
    }

    @Override
    public @Nullable XmlElementsGroup getTopGroup() {
        return null;
    }

    @Override
    public int getContentType() {
        return 0;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public PsiElement getDeclaration() {
        return myXmlTag;
    }

    @Override
    public String getName(PsiElement psiElement) {
        return myXmlTag.getName();
    }

    @Override
    public @NlsSafe String getName() {
        return myXmlTag.getName();
    }

    @Override
    public void init(PsiElement psiElement) {

    }
}
