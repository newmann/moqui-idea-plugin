package org.moqui.idea.plugin.util;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.xml.XmlAttributeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.dom.model.Parameter;
import org.moqui.idea.plugin.service.IndexServiceParameter;

public class EntityFacadeFieldAttributeDescriptor implements XmlAttributeDescriptor {
    public static EntityFacadeFieldAttributeDescriptor ofField(@NotNull Field field){
        return  new EntityFacadeFieldAttributeDescriptor(field,false);
    }

    public static EntityFacadeFieldAttributeDescriptor ofField(@NotNull Field field, boolean forceNormal){
        return  new EntityFacadeFieldAttributeDescriptor(field,forceNormal);
    }
    public static EntityFacadeFieldAttributeDescriptor ofParameter(@NotNull IndexServiceParameter field, boolean forceNormal){
        return  new EntityFacadeFieldAttributeDescriptor(field,forceNormal);
    }
    public static EntityFacadeFieldAttributeDescriptor ofParameter(@NotNull IndexServiceParameter field){
        return  new EntityFacadeFieldAttributeDescriptor(field,false);
    }

    private final EntityFacadeXmlTagType myTagType;
    private final Field myField;
    private  final IndexServiceParameter myParameter;
    private final boolean forceNormal;
    EntityFacadeFieldAttributeDescriptor(@NotNull Field field,boolean forceNormal)
    {
        this.myField = field;
        this.myParameter = null;
        this.forceNormal = forceNormal;
        this.myTagType = EntityFacadeXmlTagType.Entity;
    }
    EntityFacadeFieldAttributeDescriptor(@NotNull IndexServiceParameter parameter,boolean forceNormal)
    {
        this.myField = null;
        this.myParameter = parameter;
        this.forceNormal = forceNormal;
        this.myTagType = EntityFacadeXmlTagType.Service;
    }

    @Override
    public boolean isRequired() {
        if (this.forceNormal) {
            return false;
        }else {
            switch (myTagType){
                case Entity:
                    return MyDomUtils.getValueOrFalseBoolean(myField.getIsPk());
                case Service:
                    if(myParameter.getAbstractField() instanceof Parameter parameter) {
                        return MyDomUtils.getValueOrFalseBoolean(parameter.getRequired());
                    }else {
                        return  false;
                    }
                default:
                    return false;
            }
        }
    }

    @Override
    public boolean hasIdType() {
        return false;
    }

    @Override
    public boolean hasIdRefType() {
        return false;
    }

    @Override
    public boolean isEnumerated() {
        return false;
    }

    @Override
    public PsiElement getDeclaration() {
        switch (myTagType){
            case Entity:
                return myField.getName().getXmlAttributeValue();
            case Service:
                return myParameter.getAbstractField().getName().getXmlAttributeValue();
            default:
                return null;
        }
    }

    @Override
    public String getName(PsiElement psiElement) {
        switch (myTagType){
            case Entity:
                return MyDomUtils.getValueOrEmptyString(myField.getName());
            case Service:
                return myParameter.getParameterName();
            default:
                return "";
        }
//        return MyDomUtils.getValueOrEmptyString(myField.getName());
    }

    @Override
    public String getName() {
        switch (myTagType){
            case Entity:
                return MyDomUtils.getValueOrEmptyString(myField.getName());
            case Service:
                return myParameter.getParameterName();
            default:
                return "";
        }
//        return MyDomUtils.getValueOrEmptyString(myField.getName());
    }

    @Override
    public void init(PsiElement psiElement) {

    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public String[] getEnumeratedValues() {
        return new String[0];
    }

    @Nullable
    @Override
    public @NlsContexts.DetailedDescription String validateValue(XmlElement xmlElement, String s) {
        //验证AttributeValue内容是否正确
        return null;
    }
}
