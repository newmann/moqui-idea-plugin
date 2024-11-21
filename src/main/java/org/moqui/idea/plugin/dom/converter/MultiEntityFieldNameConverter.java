package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
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
public class MultiEntityFieldNameConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter {
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
        return null;
    }


    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        final String fieldsStr = value.getStringValue();
        if(MyStringUtils.isEmpty(fieldsStr)) return PsiReference.EMPTY_ARRAY;
//        String[] fieldNameArray = EntityUtils.splitFieldString(fieldsStr).orElse(new ArrayList<>()).toArray(new String[0]);

        List<FieldDescriptor> fieldNameList = EntityUtils.extractFieldDescriptorList(fieldsStr,1).orElse(new ArrayList<>());


        final String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String secondTagName = MyDomUtils.getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

//        if (fieldNameArray.length == 0) {
//            fieldNameArray = new String[1];
//            fieldNameArray[0] = fieldsStr;
//        }

//        PsiReference[] result = new PsiReference[fieldNameList.size()];
        List<PsiReference> result = new ArrayList<>();
        //EntityFindOne , SelectField (FieldName)，
        //EntityFind , SelectField (FieldName)，
        //EntityFindCount , SelectField (FieldName)，

        //EntityFind , OrderBy (FieldName)，
        //EntityFindCount , OrderBy (FieldName)，

        //EntityFind, SearchFormInputs(DefaultOrderBy)

        String entityName = MyStringUtils.EMPTY_STRING;
        if (
                (secondTagName.equals(EntityFindOne.TAG_NAME) || secondTagName.equals(EntityFind.TAG_NAME) || secondTagName.equals(EntityFindCount.TAG_NAME))
                        && (firstTagName.equals(SelectField.TAG_NAME)|| firstTagName.equals(OrderBy.TAG_NAME) || firstTagName.equals(SearchFormInputs.TAG_NAME))
        ) {
            switch (secondTagName) {
                case EntityFindOne.TAG_NAME:
                    EntityFindOne entityFindOne= ServiceUtils.getCurrentEntityFindOne(context).orElse(null);
                    entityName = MyDomUtils.getXmlAttributeValueString(entityFindOne.getEntityName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);

                    break;
                case EntityFind.TAG_NAME:
                    EntityFind entityFind= ServiceUtils.getCurrentEntityFind(context).orElse(null);
                    entityName = MyDomUtils.getXmlAttributeValueString(entityFind.getEntityName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);

                    break;
                case EntityFindCount.TAG_NAME:
                    EntityFindCount entityFindCount= ServiceUtils.getCurrentEntityFindCount(context).orElse(null);
                    entityName = MyDomUtils.getXmlAttributeValueString(entityFindCount.getEntityName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);

                        break;
            }

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
