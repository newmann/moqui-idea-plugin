package org.moqui.idea.plugin.util;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.xml.XmlAttributeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Field;

public class EntityFacadeFieldAttributeDescriptor implements XmlAttributeDescriptor {
    public static EntityFacadeFieldAttributeDescriptor of(@NotNull Field field){
        return  new EntityFacadeFieldAttributeDescriptor(field);
    }

    private final Field myField;
    EntityFacadeFieldAttributeDescriptor(@NotNull Field field){
        myField = field;
    }

    @Override
    public boolean isRequired() {
        return MyDomUtils.getValueOrFalseBoolean(myField.getIsPk());
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
        return myField.getName().getXmlAttributeValue();
    }

    @Override
    public String getName(PsiElement psiElement) {
        return MyDomUtils.getValueOrEmptyString(myField.getName());
    }

    @Override
    public String getName() {
        return MyDomUtils.getValueOrEmptyString(myField.getName());
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
