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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.dom.model.Field;

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


    @NonNls
    @Override
    public String getQualifiedName() {
        return myXmlTag.getName();
    }

    @NonNls
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
        if(MyDomUtils.isMoquiDataDefineTag(xmlTag)){
            if(xmlTag.getName().equals(EntityFacadeXml.TAG_NAME)){
                resultList.add(EntityFacadeXmlTypeAttributeDescriptor.of());
            }else {
                EntityFacadeXmlTagDescriptor descriptor = EntityFacadeXmlTagDescriptor.of(xmlTag);
                //只有当前tag不是field时，才添加
                if(!descriptor.getIsField()) {
                    //对主键字段进行控制，如果主键在父级实体中存在，则不显示。注意，父级实体可能有多个
                    List<String> parentEntityPKList = getTagParentsEntityPKList(xmlTag);
                    EntityUtils.getIndexEntityByName(xmlTag.getProject(), descriptor.getEntityName())
                            .ifPresent(indexEntity -> indexEntity.getFieldList()
                                    .stream()
                                    .filter(field -> !parentEntityPKList.contains(MyDomUtils.getValueOrEmptyString(field.getName())))
                                    .forEach(
                                            field -> resultList.add(EntityFacadeFieldAttributeDescriptor.of(field)))
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

        return resultList.toArray(XmlAttributeDescriptor.EMPTY);
    }

    private List<String> getTagParentsEntityPKList(@NotNull XmlTag xmlTag){
        List<String> resultList = new ArrayList<>();
        Project project = xmlTag.getProject();
        XmlTag parentTag = xmlTag.getParentTag();

        while(parentTag!= null && !parentTag.getName().equals(EntityFacadeXml.TAG_NAME)){
            EntityFacadeXmlTagDescriptor descriptor = EntityFacadeXmlTagDescriptor.of(parentTag);
            EntityUtils.getIndexEntityByName(project, descriptor.getEntityName())
                    .ifPresent(indexEntity -> indexEntity.getFieldList()
                            .stream()
                            .filter(field->MyDomUtils.getValueOrFalseBoolean(field.getIsPk()))
                            .forEach(
                            field -> resultList.add(MyDomUtils.getValueOrEmptyString(field.getName())))
                    );

            parentTag = parentTag.getParentTag();
        }
        return resultList;
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(@NonNls String s, @Nullable XmlTag xmlTag) {
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

    @NonNls
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
