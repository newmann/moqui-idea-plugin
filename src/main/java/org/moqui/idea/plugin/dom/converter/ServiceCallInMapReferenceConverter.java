package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.util.*;

import java.util.*;

/**
 * 处理ServiceCall 的in-map属性中匹配到service的inParameter
 */
public class ServiceCallInMapReferenceConverter implements CustomReferenceConverter<String> {


    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String fieldsStr = value.getStringValue();
        if(MyStringUtils.isEmpty(fieldsStr)) return PsiReference.EMPTY_ARRAY;

        List<FieldDescriptor> fieldNameList = ServiceUtils.extractMapFieldDescriptorList(fieldsStr,1);
        if(fieldNameList.isEmpty()) return PsiReference.EMPTY_ARRAY;


        ServiceCall serviceCall = MyDomUtils.getLocalDomElementByConvertContext(context,ServiceCall.class).orElse(null);
        if(serviceCall == null) return PsiReference.EMPTY_ARRAY;

        final String serviceCallName = MyDomUtils.getValueOrEmptyString(serviceCall.getName());

        if(serviceCallName.equals(MyStringUtils.EMPTY_STRING)) return PsiReference.EMPTY_ARRAY;

        List<PsiReference> result = new ArrayList<>();
        ServiceCallDescriptor serviceCallDescriptor = ServiceCallDescriptor.of(serviceCallName);
        //如果是直接处理entity，则需要匹配到entity的Field上

        IndexService indexService = ServiceUtils.getIndexService(context.getProject(), serviceCallName).orElse(null);
        if(indexService == null) return PsiReference.EMPTY_ARRAY;

        for(FieldDescriptor fieldName : fieldNameList) {

            IndexServiceParameter parameter = indexService.getInParametersByName(fieldName.getFieldName()).orElse(null);

            result.addAll(Arrays.stream(ServiceUtils.createMapNameReference(element, fieldName, parameter)).toList());

        }


        return result.toArray(new PsiReference[0]);
    }
}
