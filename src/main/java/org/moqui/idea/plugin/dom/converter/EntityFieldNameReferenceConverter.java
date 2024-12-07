package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.FilterMapList;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.FieldDescriptor;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.Collection;
import java.util.Collections;

public class EntityFieldNameReferenceConverter implements CustomReferenceConverter<String> {
    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue<String> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        final String valueStr = MyDomUtils.getValueOrEmptyString(genericDomValue.getStringValue());
        if(valueStr.isEmpty()) return PsiReference.EMPTY_ARRAY;
        //判断是否需要创建Reference，
        if(needNotCreatePsiReference(psiElement)) return PsiReference.EMPTY_ARRAY;

        FieldDescriptor fieldDescriptor = FieldDescriptor.of(valueStr,1,valueStr.length()+1);

        IndexAbstractField indexAbstractField = EntityUtils.getIndexAbstractFieldByConvertContext(fieldDescriptor.getFieldName(),convertContext).orElse(null);

        return EntityUtils.createFieldNameReference(psiElement,fieldDescriptor,indexAbstractField);


    }

    private boolean needNotCreatePsiReference(@NotNull PsiElement psiElement){
        //如果是在filter-map-list下面，由于很难判断是否正确，
        FilterMapList filterMapList = DomUtil.findDomElement(psiElement,FilterMapList.class);
        return filterMapList != null;

    }

}
