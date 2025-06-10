package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Optional;

/**
 * 处理ServiceInclude的verb和nonu的reference问题
 */
public class ServiceIncludeReferenceConverter implements CustomReferenceConverter<String> {
    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        if(element == null || element.getParent() == null) return PsiReference.EMPTY_ARRAY;

        ServiceInclude serviceInclude = MyDomUtils.getLocalDomElementByConvertContext(context, ServiceInclude.class).orElse(null);
        if(serviceInclude == null) return PsiReference.EMPTY_ARRAY;

        Service service = ServiceUtils.getServiceByServiceInclude(context.getProject(),serviceInclude).orElse(null);
        if(service == null) return PsiReference.EMPTY_ARRAY;

//        XmlAttribute attribute = MyDomUtils.getCurrentAttribute(element).orElse(null);
//        if(attribute == null) return PsiReference.EMPTY_ARRAY;
        XmlAttribute attribute = (XmlAttribute) element.getParent();

        PsiElement resolve;
        switch (attribute.getName()) {
            case ServiceInclude.ATTR_VERB -> resolve = service.getVerb().getXmlAttributeValue();
            case ServiceInclude.ATTR_NOUN -> resolve = service.getNoun().getXmlAttributeValue();
            default -> resolve = null;
        }
        PsiReference[] result = new PsiReference[1];
        result[0] = MoquiBaseReference.of(element, attribute.getValueTextRange(), resolve);
        return result;
    }


}
