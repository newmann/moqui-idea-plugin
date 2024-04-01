package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;

import static org.moqui.idea.plugin.util.EntityUtils.*;
import static org.moqui.idea.plugin.util.MyDomUtils.*;

public class EntityFieldNameConverter extends AbstractEntityFieldNameConverter{
    @Override
    AbstractField getField(@Nullable String s, ConvertContext context) {
        Collection<AbstractField> fields = getFields(context);


        return fields.stream().filter(item->{
            final String fieldName = MyDomUtils.getXmlAttributeValueString(item.getName())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return fieldName.equals(s);
        }).findFirst().orElse(null);

    }

    @Override
    @NotNull Collection<? extends AbstractField> getFieldVariants(ConvertContext context) {
        return getFields(context);
    }

    private Collection<AbstractField> getFields(ConvertContext context) {
        Collection<AbstractField> result = new ArrayList<>();


        String curAttributeName = getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);

        final String firstTagName = getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String secondTagName = getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String thirdTagName = getThirdParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String fourthTagName = getFourthParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String fifthTagName = getFifthParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

        //Entity-Relationship-keyMap（fieldName）
        //Entity-Index-IndexField(name)
        if (
                (thirdTagName.equals(Entity.TAG_NAME) && secondTagName.equals(Relationship.TAG_NAME)&&
                firstTagName.equals(KeyMap.TAG_NAME) && curAttributeName.equals(KeyMap.ATTR_FIELD_NAME) )
            ||
                (thirdTagName.equals(Entity.TAG_NAME) && secondTagName.equals(Index.TAG_NAME)&&
                firstTagName.equals(IndexField.TAG_NAME) && curAttributeName.equals(IndexField.ATTR_NAME) )
        )
        {
            getCurrentEntity(context).ifPresent(curEntity -> result.addAll(curEntity.getFieldList()));
            return result;
        }
        //Entity-Relationship-keyMap（related）
        //Entity-Relationship-keyValue（related）
        //ExtendEntity-Relationship-keyMap（related）
        //ExtendEntity-Relationship-keyValue（related）

        if (
                (thirdTagName.equals(Entity.TAG_NAME) ||(thirdTagName.equals(ExtendEntity.TAG_NAME)))
                && secondTagName.equals(Relationship.TAG_NAME)
                &&(firstTagName.equals(KeyMap.TAG_NAME) || firstTagName.equals(KeyValue.TAG_NAME))
                && curAttributeName.equals(KeyMap.ATTR_RELATED)
        ){
            Relationship curRelationship = getCurrentRelationship(context).orElse(null);
            if (curRelationship == null) return result;

            String relatedEntityName = curRelationship.getRelated().getStringValue();
            if(MyStringUtils.isEmpty(relatedEntityName)) return result;

            result.addAll(EntityUtils.getEntityFieldList(context.getProject(),relatedEntityName));
            return result;
        }
        //ExtendEntity-Relationship-keyMap（fieldName）
        //ExtendEntity-Index-IndexField(name)
        if (
                (thirdTagName.equals(ExtendEntity.TAG_NAME) && secondTagName.equals(Relationship.TAG_NAME)&&
                        firstTagName.equals(KeyMap.TAG_NAME) && curAttributeName.equals(KeyMap.ATTR_FIELD_NAME) )
                ||
                (thirdTagName.equals(ExtendEntity.TAG_NAME) && secondTagName.equals(Index.TAG_NAME)&&
                        firstTagName.equals(IndexField.TAG_NAME) && curAttributeName.equals(IndexField.ATTR_NAME) )
        )
        {
            ExtendEntity curExtendEntity = getCurrentExtendEntity(context).orElse(null);
            if (curExtendEntity == null) return result;

            result.addAll(curExtendEntity.getFieldList());

            getCurrentExtendEntitySourceEntity(context).ifPresent(sourceEntity -> result.addAll(sourceEntity.getFieldList()));

            return result;
        }

        //ViewEntity-MemberEntity-KeyMap（fieldName）
        //ViewEntity-MemberEntity-KeyMap（related）
        if (
               thirdTagName.equals(ViewEntity.TAG_NAME)
                && secondTagName.equals(MemberEntity.TAG_NAME)
                && firstTagName.equals(KeyMap.TAG_NAME)
                && (curAttributeName.equals(KeyMap.ATTR_FIELD_NAME) || curAttributeName.equals(KeyMap.ATTR_RELATED))
       )
        {
            MemberEntity curMemberEntity = getCurrentMemberEntity(context).orElse(null);
            if (curMemberEntity == null) return result;

            if(curAttributeName.equals(KeyMap.ATTR_FIELD_NAME)) {
                //fieldName，取MemberEntity属性joinFromAlias对应表的字段
                result.addAll(EntityUtils.getFieldListFromMemberEntity(curMemberEntity,MemberEntity.ATTR_JOIN_FROM_ALIAS));
            }else {
                //related，取MemberEntity属性entityName对应表的字段
                result.addAll(EntityUtils.getFieldListFromMemberEntity(curMemberEntity,MemberEntity.ATTR_ENTITY_NAME));
            }
            return result;
        }
        //ViewEntity-EntityCondition-ECondition（fieldName）

        if (
                thirdTagName.equals(ViewEntity.TAG_NAME)
                        && secondTagName.equals(EntityCondition.TAG_NAME)
                        && firstTagName.equals(ECondition.TAG_NAME)
                        && curAttributeName.equals(ECondition.ATTR_FIELD_NAME)
        )
        {
            ECondition curECondition = getCurrentECondition(context).orElse(null);
            if (curECondition == null) return result;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return result;
            String alias ;
            if(curECondition.getEntityAlias().getXmlAttributeValue() == null) {
                alias = MyStringUtils.EMPTY_STRING;
            }else {
                alias= curECondition.getEntityAlias().getXmlAttributeValue().getValue();
            }

            if(MyStringUtils.isEmpty(alias)) {
                //没有alias，就去当前ViewEntity的所有Fields
                result.addAll(EntityUtils.getViewEntityFieldList(context.getProject(),viewEntity));
            }else {
                MemberEntity memberEntity = EntityUtils.getDefinedMemberEntityByAlias(viewEntity,alias);
                if(memberEntity != null) {
                    result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity, MemberEntity.ATTR_ENTITY_NAME));
                }

            }

            return result;
        }

        //ViewEntity-MemberEntity-EntityCondition-ECondition（fieldName）
        //ViewEntity-MemberEntity-EntityCondition-ECondition（toFieldName）
        //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（fieldName）
        //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（toFieldName）

        if (  (firstTagName.equals(ECondition.TAG_NAME)
                && (curAttributeName.equals(ECondition.ATTR_FIELD_NAME) || curAttributeName.equals(ECondition.ATTR_TO_FIELD_NAME)))
                &&
                (
                        (fourthTagName.equals(ViewEntity.TAG_NAME)
                            && thirdTagName.equals(MemberEntity.TAG_NAME)
                                    && secondTagName.equals(EntityCondition.TAG_NAME))
                        ||
                        (fifthTagName.equals(ViewEntity.TAG_NAME)
                            &&fourthTagName.equals(MemberEntity.TAG_NAME)
                            && thirdTagName.equals(EntityCondition.TAG_NAME)
                            && secondTagName.equals(EConditions.TAG_NAME))
                )
        )
        {
            ECondition curECondition = getCurrentECondition(context).orElse(null);
            if (curECondition == null) return result;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return result;
            MemberEntity memberEntity;
            if(curAttributeName.equals(ECondition.ATTR_FIELD_NAME)) {
                memberEntity = EntityUtils.getDefinedMemberEntityByAlias(viewEntity
                        ,MyDomUtils.getXmlAttributeValueString(curECondition.getEntityAlias()).orElse(MyStringUtils.EMPTY_STRING));

            }else {
                memberEntity = EntityUtils.getDefinedMemberEntityByAlias(viewEntity
                        ,MyDomUtils.getXmlAttributeValueString(curECondition.getToEntityAlias()).orElse(MyStringUtils.EMPTY_STRING));


            }
            if(memberEntity != null) {
                result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity, MemberEntity.ATTR_ENTITY_NAME));
            }

            return result;
        }

        //ViewEntity-Alias(field)
        if (
                secondTagName.equals(ViewEntity.TAG_NAME)
                        && firstTagName.equals(Alias.TAG_NAME)
                        && curAttributeName.equals(Alias.ATTR_FIELD)
        )
        {
            Alias curAlias = getCurrentAlias(context).orElse(null);
            if(curAlias == null) return result;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return result;

            AbstractMemberEntity memberEntity = EntityUtils.getViewEntityAbstractMemberEntityByAlias(
                    viewEntity,
                    MyDomUtils.getXmlAttributeValueString(curAlias.getEntityAlias()).orElse(MyStringUtils.EMPTY_STRING)
            ).orElse(null);
            if(memberEntity == null) return result;

            result.addAll(EntityUtils.getFieldListFromAbstractMemberEntity(memberEntity));

            return result;
        }
        //ViewEntity-AliasAll-Exclude（Field）
        if (
                thirdTagName.equals(ViewEntity.TAG_NAME)
                && secondTagName.equals(AliasAll.TAG_NAME)
                        && firstTagName.equals(Exclude.TAG_NAME)
                        && curAttributeName.equals(Exclude.ATTR_FIELD)
        )
        {
            AliasAll curAliasAll = getCurrentAliasAll(context).orElse(null);
            if(curAliasAll == null) return result;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return result;

            AbstractMemberEntity memberEntity = EntityUtils.getViewEntityAbstractMemberEntityByAlias(
                    viewEntity,
                    MyDomUtils.getXmlAttributeValueString(curAliasAll.getEntityAlias()).orElse(MyStringUtils.EMPTY_STRING)
            ).orElse(null);
            if(memberEntity == null) return result;

            result.addAll(EntityUtils.getFieldListFromAbstractMemberEntity(memberEntity));

            return result;
        }
        //ViewEntity-Alias-ComplexAlias-ComplexAliasField(field)
        if (
                fourthTagName.equals(ViewEntity.TAG_NAME)
                        && thirdTagName.equals(Alias.TAG_NAME)
                        && secondTagName.equals(ComplexAlias.TAG_NAME)
                        && firstTagName.equals(ComplexAliasField.TAG_NAME)
                        && curAttributeName.equals(ComplexAliasField.ATTR_FIELD)
        )
        {
            ComplexAliasField curComplexAliasField = getCurrentComplexAliasField(context).orElse(null);
            if (curComplexAliasField == null) return result;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return result;
            MemberEntity memberEntity;
            memberEntity = EntityUtils.getDefinedMemberEntityByAlias(viewEntity
                    ,MyDomUtils.getXmlAttributeValueString(curComplexAliasField.getEntityAlias()).orElse(MyStringUtils.EMPTY_STRING));

            if(memberEntity != null) {
                result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity,MemberEntity.ATTR_ENTITY_NAME));
            }


            return result;
        }

        //Service下的AutoParameters-Exclude（FieldName）
        if (
                        secondTagName.equals(AutoParameters.TAG_NAME)
                        && firstTagName.equals(Exclude.TAG_NAME)
                        && curAttributeName.equals(Exclude.ATTR_FIELD_NAME)
        )
        {
            AutoParameters curAutoParameters = EntityUtils.getCurrentAutoParameters(context).orElse(null);

            if(curAutoParameters == null) return result;
            String entityName = MyDomUtils.getXmlAttributeValueString(curAutoParameters.getEntityName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            if(MyStringUtils.EMPTY_STRING.equals(entityName)) {
                //如果当前的AutoParameters中没有定义entityName，则有可能是在Service的inParameters中，这时，service的noun就是EntityName
                Service service = ServiceUtils.getCurrentService(context).orElse(null);
                if(service == null) return result;
                entityName = MyDomUtils.getXmlAttributeValueString(service.getNoun().getXmlAttributeValue())
                        .orElse(MyStringUtils.EMPTY_STRING);

                if(entityName.equals(MyStringUtils.EMPTY_STRING)) return result;
            }




            result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(),entityName));
            return result;

        }
        //todo：Service下的 ServiceCall-FieldMap（FieldName）


        //Service下的EntityFindOne , FieldMap (FieldName)
        if (
                secondTagName.equals(EntityFindOne.TAG_NAME)
                        && firstTagName.equals(FieldMap.TAG_NAME)
                        && curAttributeName.equals(FieldMap.ATTR_FIELD_NAME)
        )
        {
            EntityFindOne curEntityFindOne = ServiceUtils.getCurrentEntityFindOne(context).orElse(null);

            if(curEntityFindOne == null) return result;
            XmlAttributeValue attributeValue = curEntityFindOne.getEntityName().getXmlAttributeValue();
            if(attributeValue == null) return result;
            final String entityName = attributeValue.getValue();

            result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(),entityName));
            return result;

        }

        //ECondition的处理：
        if(firstTagName.equals(ECondition.TAG_NAME)) {

            AbstractEntityName nameAbstract = null;
            //Service下的EntityFind , ECondition (FieldName)
            //Service下的EntityFind , EConditions，ECondition (FieldName)
            //Service下的EntityFind , HavingEConditions，ECondition (FieldName)
            if (
                    (secondTagName.equals(EntityFind.TAG_NAME)
                            && curAttributeName.equals(ECondition.ATTR_FIELD_NAME))
                            ||
                    (thirdTagName.equals(EntityFind.TAG_NAME)
                            && secondTagName.equals(EConditions.TAG_NAME)
                            && curAttributeName.equals(ECondition.ATTR_FIELD_NAME))
                        ||
                    (thirdTagName.equals(EntityFind.TAG_NAME)
                            && secondTagName.equals(HavingEConditions.TAG_NAME)
                            && curAttributeName.equals(ECondition.ATTR_FIELD_NAME))

            ) {
                nameAbstract  = ServiceUtils.getCurrentEntityFind(context).orElse(null);



            }
            //Service下的EntityDeleteByCondition，ECondition（FieldName）
            if (
                    secondTagName.equals(EntityDeleteByCondition.TAG_NAME)
                            && curAttributeName.equals(ECondition.ATTR_FIELD_NAME)
            ) {
                nameAbstract  = ServiceUtils.getCurrentEntityDeleteByCondition(context).orElse(null);



            }
            //Service下的EntityFindCount - ECondition (FieldName)
            //Service下的EntityFindCount -EConditions - ECondition (FieldName)
            if (
                    (secondTagName.equals(EntityFindCount.TAG_NAME)
                            && curAttributeName.equals(ECondition.ATTR_FIELD_NAME))
                            ||
                    (thirdTagName.equals(EntityFindCount.TAG_NAME)
                            && secondTagName.equals(EConditions.TAG_NAME)
                            && curAttributeName.equals(ECondition.ATTR_FIELD_NAME))
            ) {
                nameAbstract  = ServiceUtils.getCurrentEntityFindCount(context).orElse(null);


            }
            if (nameAbstract == null) return result;
            final String entityName = MyDomUtils.getXmlAttributeValueString(nameAbstract.getEntityName().getXmlAttributeValue())
                        .orElse(MyStringUtils.EMPTY_STRING);
            if (MyStringUtils.EMPTY_STRING.equals(entityName)) return result;

            result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(), entityName));
            return result;

        }


        return result;

    }
}
