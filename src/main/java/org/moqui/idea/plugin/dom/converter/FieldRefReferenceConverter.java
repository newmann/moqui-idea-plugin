package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FieldRefReferenceConverter implements CustomReferenceConverter<String> {
//    @Override
//    public @Nullable AbstractField fromString(@Nullable String s, ConvertContext context) {
//        if(s == null) return null;
//        return getField(s,context)
//                .orElse(null);
//
//    }
//
//    @Override
//    public @Nullable String toString(@Nullable AbstractField field, ConvertContext context) {
//        if (field == null) return null;
//        return MyDomUtils.getValueOrEmptyString(field.getName());
//    }
//
//    @Override
//    public @Nullable LookupElement createLookupElement(AbstractField field) {
//        if(field == null) {
//            return super.createLookupElement(field);
//        }else {
//            String s = MyDomUtils.getValueOrEmptyString(field.getName());
//            return LookupElementBuilder.create(s)
//                    .withCaseSensitivity(false);
//
//        }
//    }


    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue genericDomValue, PsiElement psiElement, ConvertContext convertContext) {

        String valueStr = genericDomValue.getStringValue();
        if(valueStr == null) return PsiReference.EMPTY_ARRAY;
        AbstractField abstractField = getField(valueStr,convertContext).orElse(null);

        if(abstractField == null) {
            return MoquiBaseReference.createNullRefArray(psiElement,
                    new TextRange(1,
                            valueStr.length()+1));
        }else {

            return MoquiBaseReference.createOneRefArray(psiElement,
                    new TextRange(1,
                            valueStr.length()+1),
                    abstractField.getName().getXmlAttributeValue());

        }
    }

    /**
     * 根据当前位置找到所有可用的Field
     *
     * @param context 当前的ConvertContext
     * @return List<AbstractField>
     */
    private List<AbstractField> getFieldList(ConvertContext context) {

        List<AbstractField> result = new ArrayList<>();

        ScreenUtils.getCurrentAbstractForm(context).ifPresent(abstractForm->{
           result.addAll(ScreenUtils.getFieldListFromForm(abstractForm));
        });


        return result;
    }
    /**
     * 根据当前位置对应的Field
     * @param related 带查找的Field 名称
     * @param context 当前的  ConvertContext
     * @return Optional<AbstractField>
     */
    private Optional<AbstractField> getField(String related, ConvertContext context) {
        List<AbstractField> fieldList = getFieldList(context);
        return fieldList.stream().filter(
                item->{
                    String str = MyDomUtils.getValueOrEmptyString(item.getName());
                    return str.equals(related);
                    }
        ).findFirst();

    }


}
