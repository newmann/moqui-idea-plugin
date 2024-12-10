package org.moqui.idea.plugin.provider;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.xml.XmlAttributeDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.util.MyDomUtils;

public class MoquiEntityFieldAttributeDescriptor implements XmlAttributeDescriptor {
    public static MoquiEntityFieldAttributeDescriptor of(@NotNull Field field){
        return  new MoquiEntityFieldAttributeDescriptor(field);
    }

    private final Field myField;
    MoquiEntityFieldAttributeDescriptor(@NotNull Field field){
        myField = field;
    }

    @Override
    public boolean isRequired() {
        //如果进行主键控制，有点复杂，先放开，需要进一步完善
        return false;
//        return MyDomUtils.getValueOrFalseBoolean(myField.getIsPk());
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

    @NonNls
    @Override
    public String getName(PsiElement psiElement) {
        return MyDomUtils.getValueOrEmptyString(myField.getName());
    }

    @Override
    public @NlsSafe String getName() {
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
