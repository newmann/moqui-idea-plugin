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
import org.moqui.idea.plugin.util.FieldDescriptor;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class EntityFieldNameConverter extends ResolvingConverter.StringConverter  implements CustomReferenceConverter<String> {
    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue<String> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        final String valueStr = MyDomUtils.getValueOrEmptyString(genericDomValue.getStringValue());
        FieldDescriptor fieldDescriptor = FieldDescriptor.of(valueStr,1,valueStr.length()+1);
//        Matcher matcher = MyStringUtils.FIELD_NAME_PATTERN.matcher(valueStr);
//
//        if(!matcher.find()) return PsiReference.EMPTY_ARRAY;
//
//        String fieldName = matcher.group(1);


//        List<PsiReference> resultList = new ArrayList<>();

        IndexAbstractField indexAbstractField = EntityUtils.getIndexAbstractFieldByConvertContext(fieldDescriptor.getFieldName(),convertContext).orElse(null);
        if (indexAbstractField == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        return EntityUtils.createFieldNameReference(psiElement,fieldDescriptor,indexAbstractField);

//        if(indexAbstractField.getOriginFieldName().equals(fieldName)){
//            int fieldIndex = valueStr.indexOf(indexAbstractField.getOriginFieldName());
//            resultList.add(new PsiRef(psiElement,
//                    TextRange.create(fieldIndex+1,fieldIndex+1+indexAbstractField.getOriginFieldName().length()),
//                    indexAbstractField.getAbstractField().getName().getXmlAttributeValue()));
//        }else{
//            int prefixIndex = valueStr.indexOf(indexAbstractField.getPrefix());
//            if(prefixIndex>=0){
//                resultList.add(new PsiRef(psiElement,
//                        TextRange.create(prefixIndex+1,prefixIndex+1+indexAbstractField.getPrefix().length()),
//                        indexAbstractField.getAliasAll().getPrefix().getXmlAttributeValue()));
//            }
//            int originFieldIndex = valueStr.indexOf(MyStringUtils.upperCaseFirstChar(indexAbstractField.getOriginFieldName()));
//            if(originFieldIndex>=0){
//                resultList.add(new PsiRef(psiElement,
//                        TextRange.create(originFieldIndex+1,originFieldIndex+1+indexAbstractField.getOriginFieldName().length()),
//                        indexAbstractField.getAbstractField().getName().getXmlAttributeValue()));
//            }
//
//        }
//
//        return resultList.toArray(new PsiReference[0]);

    }


    @NotNull
    @Override
    public Collection<? extends String> getVariants(ConvertContext convertContext) {
        return EntityUtils.getIndexAbstractFieldListByConvertContext(convertContext).stream().map(IndexAbstractField::getName).toList();
    }
}
