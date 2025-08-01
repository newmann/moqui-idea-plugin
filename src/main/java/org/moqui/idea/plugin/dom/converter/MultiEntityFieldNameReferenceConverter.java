package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 针对多个字段的处理，比如OrderBy和SelectField
 */

public class MultiEntityFieldNameReferenceConverter implements CustomReferenceConverter<String> {

    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        final String fieldsStr = value.getStringValue();
        if(MyStringUtils.isEmpty(fieldsStr)) return PsiReference.EMPTY_ARRAY;
//        String[] fieldNameArray = EntityUtils.splitFieldString(fieldsStr).orElse(new ArrayList<>()).toArray(new String[0]);

        List<FieldDescriptor> fieldNameList = EntityUtils.extractFieldDescriptorList(fieldsStr,1);


        final String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String secondTagName = MyDomUtils.getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

        List<PsiReference> result = new ArrayList<>();
        String entityName = MyStringUtils.EMPTY_STRING;
        if (
                (secondTagName.equals(EntityFindOne.TAG_NAME) || secondTagName.equals(EntityFind.TAG_NAME) || secondTagName.equals(EntityFindCount.TAG_NAME))
                        && (firstTagName.equals(SelectField.TAG_NAME)|| firstTagName.equals(OrderBy.TAG_NAME) || firstTagName.equals(SearchFormInputs.TAG_NAME))
        ) {
            entityName = switch (secondTagName) {
                case EntityFindOne.TAG_NAME ->
                    ServiceUtils.getCurrentEntityFindOne(context)
                            .map(item->MyDomUtils.getValueOrEmptyString(item.getEntityName()))
                            .orElse(MyStringUtils.EMPTY_STRING);
//                    EntityFindOne entityFindOne = ServiceUtils.getCurrentEntityFindOne(context).orElse(null);
//                    if(entityFindOne == null){
//                        yield MyStringUtils.EMPTY_STRING;
//                    }else {
//                        yield MyDomUtils.getValueOrEmptyString(entityFindOne.getEntityName());
//                    }

                case EntityFind.TAG_NAME ->
                    ServiceUtils.getCurrentEntityFind(context)
                            .map(item->MyDomUtils.getValueOrEmptyString(item.getEntityName()))
                            .orElse(MyStringUtils.EMPTY_STRING);
//                    EntityFind entityFind = ServiceUtils.getCurrentEntityFind(context).orElse(null);
//                    yield MyDomUtils.getValueOrEmptyString(entityFind.getEntityName());

                case EntityFindCount.TAG_NAME ->
                    ServiceUtils.getCurrentEntityFindCount(context)
                            .map(item->MyDomUtils.getValueOrEmptyString(item.getEntityName()))
                            .orElse(MyStringUtils.EMPTY_STRING);
//                    EntityFindCount entityFindCount = ServiceUtils.getCurrentEntityFindCount(context).orElse(null);
//                    yield MyDomUtils.getValueOrEmptyString(entityFindCount.getEntityName());
//                }
                default -> entityName;
            };

        }
        if(entityName.equals(MyStringUtils.EMPTY_STRING)) return PsiReference.EMPTY_ARRAY;


        Collection<IndexAbstractField> fieldList = EntityUtils.getEntityOrViewEntityFields(context.getProject(), entityName);

        for(FieldDescriptor fieldName : fieldNameList) {

            if (fieldName.isContainGroovyVariable() || fieldName.isEmpty()) continue;

            IndexAbstractField field = fieldList.stream().filter(item -> {
                String itemFieldName = MyDomUtils.getValueOrEmptyString(item.getName());
                return itemFieldName.equals(fieldName.getFieldName());
            }).findFirst().orElse(null);

            result.addAll(Arrays.stream(EntityUtils.createFieldNameReference(element, fieldName, field)).toList());

//            if (field != null) {
//                result.add(new PsiRef(element,
//                        new TextRange(1+fieldName.getFieldNameBeginIndex(),1+ fieldName.getFieldNameEndIndex()),
//                        field.getAbstractField().getName().getXmlAttributeValue()));
//
//            }else {
//
//                result.add(new PsiRef(element,
//                        new TextRange( 1 + fieldName.getFieldNameBeginIndex(),1 + fieldName.getFieldNameEndIndex()),
//                        null));
//
//            }
        }


        return result.toArray(new PsiReference[0]);
    }
}
