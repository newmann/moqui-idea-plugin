package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.FilterMapList;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.util.*;

public class EntityFieldNameReferenceConverter implements CustomReferenceConverter<String> {
    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue<String> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        final String valueStr = MyDomUtils.getValueOrEmptyString(genericDomValue.getStringValue());
        if(valueStr.isEmpty()) return PsiReference.EMPTY_ARRAY;
        FieldDescriptor fieldDescriptor = FieldDescriptor.of(valueStr, 1, valueStr.length() + 1);
        //判断是否需要创建Reference，
        if(needNotCreatePsiReference(psiElement)) return PsiReference.EMPTY_ARRAY;
        //判断ServiceCall
        ServiceCall serviceCall = MyDomUtils.getLocalDomElementByConvertContext(convertContext, ServiceCall.class).orElse(null);
        if (serviceCall != null) {
            ServiceCallDescriptor serviceCallDescriptor = ServiceCallDescriptor.of(MyDomUtils.getValueOrEmptyString(serviceCall.getName()));
            if (!serviceCallDescriptor.isCRUD()) {
                //这里好像很难优化,暂时不处理
                IndexService indexService = ServiceUtils.getIndexService(convertContext.getProject(), serviceCallDescriptor.getServiceCallString()).orElse(null);
                if (indexService != null) {
                    IndexServiceParameter serviceParameter = indexService.getInParametersByName(fieldDescriptor.getFieldName()).orElse(null);
                    return ServiceUtils.createMapNameReference(psiElement, fieldDescriptor, serviceParameter);
                }
            }
        }

        IndexAbstractField indexAbstractField = EntityUtils.getIndexAbstractFieldByFieldContext(fieldDescriptor.getFieldName(), convertContext).orElse(null);

        return EntityUtils.createFieldNameReference(psiElement,fieldDescriptor,indexAbstractField);


    }

    private boolean needNotCreatePsiReference(@NotNull PsiElement psiElement){
        //如果是在filter-map-list下面，由于很难判断是否正确，
        FilterMapList filterMapList = DomUtil.findDomElement(psiElement,FilterMapList.class);
        return filterMapList != null;

    }

}
