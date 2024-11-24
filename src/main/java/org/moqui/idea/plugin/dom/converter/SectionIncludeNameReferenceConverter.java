package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.Section;
import org.moqui.idea.plugin.dom.model.SectionInclude;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.Optional;

/**
 * section和section-iterator都可以被引用
 */
public class SectionIncludeNameReferenceConverter implements CustomReferenceConverter<String> {
    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        SectionInclude sectionInclude = MyDomUtils.getLocalDomElementByConvertContext(context, SectionInclude.class).orElse(null);
        if(sectionInclude == null) return PsiReference.EMPTY_ARRAY;

        String sectionName = value.getStringValue();
        if(MyStringUtils.isEmpty(sectionName)) return PsiReference.EMPTY_ARRAY;
        DomFileElement<Screen> screenDomFile = ScreenUtils.getScreenFileByLocation(context.getProject(),
                MyDomUtils.getValueOrEmptyString(sectionInclude.getLocation())).orElse(null);
        if(screenDomFile == null) return PsiReference.EMPTY_ARRAY;

        Optional<Section> sectionOptional = ScreenUtils.getSectionFromScreenFileByName(screenDomFile,sectionName);
        PsiReference[] result = new PsiReference[1];
        result[0] = sectionOptional
                .map(section -> new PsiRef(element, TextRange.create(1,1+sectionName.length()), section.getName().getXmlAttributeValue()))
                .orElseGet(() -> new PsiRef(element, TextRange.create(1,1+sectionName.length()), null));
        return result;
    }


}
