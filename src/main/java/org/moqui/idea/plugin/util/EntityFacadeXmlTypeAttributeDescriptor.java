package org.moqui.idea.plugin.util;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.xml.XmlAttributeDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Field;

public class EntityFacadeXmlTypeAttributeDescriptor implements XmlAttributeDescriptor {
    public static EntityFacadeXmlTypeAttributeDescriptor of(){
        return  new EntityFacadeXmlTypeAttributeDescriptor();
    }

    @Override
    public boolean isRequired() {
        return true;
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
        return null;
    }

    @NonNls
    @Override
    public String getName(PsiElement psiElement) {
        return getName();
    }

    @Override
    public @NlsSafe String getName() {
        return "type";
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
        return new String[]{"install","seed-initial","seed","demo"};
    }

    @Nullable
    @Override
    public @NlsContexts.DetailedDescription String validateValue(XmlElement xmlElement, String s) {
        //验证AttributeValue内容是否正确
        return null;
    }
}
