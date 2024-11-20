package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;

import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EntityFieldNameConverter extends ResolvingConverter.StringConverter  implements CustomReferenceConverter<String> {
    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue<String> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        final String valueStr = genericDomValue.getStringValue();
        if(MyStringUtils.isEmpty(valueStr)) return PsiReference.EMPTY_ARRAY;
        TextRange textRange;
        String fieldName;
        final String firstChar = valueStr.substring(0,1);
        if(MyStringUtils.FIELD_SORT_CHAR_LIST.contains(firstChar)) {
            fieldName = valueStr.substring(1);
            textRange = new TextRange(2,valueStr.length()+1);
        }else {
            fieldName = valueStr;
            textRange = new TextRange(1,valueStr.length()+1);
        }
        List<PsiReference> resultList = new ArrayList<>();

        IndexAbstractField indexAbstractField = EntityUtils.getIndexAbstractFieldByConvertContext(fieldName,convertContext).orElse(null);
        if (indexAbstractField == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        if(indexAbstractField.getOriginFieldName().equals(fieldName)){
            resultList.add(new PsiRef(psiElement,
                    textRange,
                    indexAbstractField.getAbstractField().getName().getXmlAttributeValue()));
        }else{
            int prefixIndex = valueStr.indexOf(indexAbstractField.getPrefix());
            if(prefixIndex>=0){
                resultList.add(new PsiRef(psiElement,
                        TextRange.create(prefixIndex,prefixIndex+indexAbstractField.getPrefix().length()),
                        indexAbstractField.getAliasAll().getPrefix().getXmlAttributeValue()));
            }
            int originFieldIndex = valueStr.indexOf(indexAbstractField.getOriginFieldName());
            if(originFieldIndex>=0){
                resultList.add(new PsiRef(psiElement,
                        TextRange.create(originFieldIndex,originFieldIndex+indexAbstractField.getOriginFieldName().length()),
                        indexAbstractField.getAbstractField().getName().getXmlAttributeValue()));
            }

        }

        return resultList.toArray(new PsiReference[0]);

    }


    @NotNull
    @Override
    public Collection<? extends String> getVariants(ConvertContext convertContext) {
        return EntityUtils.getIndexAbstractFieldListByConvertContext(convertContext).stream().map(IndexAbstractField::getName).toList();
    }
}
