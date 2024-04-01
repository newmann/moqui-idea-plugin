package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Collection;

/**
 * 针对多个字段的处理，比如OrderBy和SelectField
 */
public class MultiEntityFieldNameConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter {
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
        return null;
    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        final String fieldsStr = value.getStringValue();
        if(MyStringUtils.isEmpty(fieldsStr)) return PsiReference.EMPTY_ARRAY;

        String[] fieldNameArray = fieldsStr.split(",");
        final String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String secondTagName = MyDomUtils.getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        if (fieldNameArray.length == 0) {
            fieldNameArray = new String[1];
            fieldNameArray[0] = fieldsStr;
        }

        PsiReference[] result = new PsiReference[fieldNameArray.length];

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


        Collection<AbstractField> fieldList = EntityUtils.getEntityOrViewEntityFields(context.getProject(), entityName);



        int startIndex = 1;

        for(int i = 0; i< fieldNameArray.length; i++) {
            if(MyStringUtils.isEmpty(fieldNameArray[i])) {
                startIndex = startIndex + 1;
                continue;
            }

            String fieldName;
            //判断fieldName的第一个字符是否为控制字符，如果是，则跳过第一个字符
            if(ServiceUtils.ORDER_BY_COMMANDER.contains(fieldNameArray[i].substring(0,1))){
                fieldName = fieldNameArray[i].substring(1);
                startIndex = startIndex +1;
            } else {
                fieldName = fieldNameArray[i];
            }

            AbstractField field = fieldList.stream().filter(item->{
                String itemFieldName = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                        .orElse(MyStringUtils.EMPTY_STRING);
                if(MyStringUtils.isEmpty(itemFieldName)) return false;

                return itemFieldName.equals(fieldName);
            }).findFirst().orElse(null);
            if (field != null) {

                result[i] = new PsiRef(element,
                        new TextRange(startIndex,startIndex+fieldName.length()),
                        field.getName().getXmlAttributeValue());

            }
            startIndex = startIndex + fieldName.length() +1;

        }


        return result;
    }
}
