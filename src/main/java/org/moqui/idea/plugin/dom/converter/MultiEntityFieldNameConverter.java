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
 * 所有已经定义的package，包含Entity和ViewEntity
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
        //EntityFindOne , SelectField (FieldName)，

        if (
                (secondTagName.equals(EntityFindOne.TAG_NAME) || secondTagName.equals(EntityFind.TAG_NAME))
                        && firstTagName.equals(SelectField.TAG_NAME)
        ) {
            String entityName;
            if(secondTagName.equals(EntityFindOne.TAG_NAME)){
                EntityFindOne entityFindOne= ServiceUtils.getCurrentEntityFindOne(context).orElse(null);
                entityName = MyDomUtils.getXmlAttributeValueString(entityFindOne.getEntityName().getXmlAttributeValue())
                        .orElse(MyStringUtils.EMPTY_STRING);
            }else{
                EntityFind entityFind= ServiceUtils.getCurrentEntityFind(context).orElse(null);
                entityName = MyDomUtils.getXmlAttributeValueString(entityFind.getEntityName().getXmlAttributeValue())
                        .orElse(MyStringUtils.EMPTY_STRING);
            }
            Collection<AbstractField> fieldList = EntityUtils.getEntityOrViewEntityFields(context.getProject(), entityName);



            int startIndex = 1;

            for(int i = 0; i< fieldNameArray.length; i++) {
                String fieldName = fieldNameArray[i];
                AbstractField field = fieldList.stream().filter(item->{
                    return item.getName().getXmlAttributeValue().getValue().equals(fieldName);
                }).findFirst().orElse(null);
                if (field != null) {

                    result[i] = new PsiRef(element,
                            new TextRange(startIndex,startIndex+fieldName.length()),
                            field.getName().getXmlAttributeValue());

                }
                startIndex = startIndex + fieldName.length() +1;

            }
        }

        return result;
    }
}
