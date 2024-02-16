package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
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
            return item.getName().getXmlAttributeValue().getValue().equals(s);
        }).findFirst().orElse(null);

    }

    @Override
    @NotNull Collection<? extends AbstractField> getFieldVariants(ConvertContext context) {
        return getFields(context);
    }

    private Collection<AbstractField> getFields(ConvertContext context) {
//        Set<String> AllowedFourthTagName = new HashSet<>();
//        Set<String> AllowedThirdTagName = new HashSet<>();
//        Set<String> AllowedSecondTagName = new HashSet<>();
//        Set<String> AllowedFirstTagName = new HashSet<>();
//        Set<String> AllowedAttributeName = new HashSet<>();
//
//        Set.of(Entity.TAG_NAME, ExtendEntity.TAG_NAME,ViewEntity.TAG_NAME,MemberEntity.TAG_NAME);
//        Set.of(Relationship.TAG_NAME, Index.TAG_NAME,MemberEntity.TAG_NAME,EntityCondition.TAG_NAME);
//        Set.of(KeyMap.TAG_NAME, IndexField.TAG_NAME,KeyValue.TAG_NAME,ECondition.TAG_NAME);
//        Set.of(KeyMap.ATTR_RELATED, KeyMap.ATTR_FIELD_NAME,IndexField.ATTR_NAME,ECondition.ATTR_TO_FIELD_NAME);//KeyValue的Related名称和keyMap相同

        Collection<AbstractField> result = new ArrayList<AbstractField>();


        String curAttributeName = getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);

//        if(!AllowedAttributeName.contains(curAttributeName)) return Collections.EMPTY_LIST;

        final String firstTagName = getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//        if(!AllowedFirstTagName.contains(firstTagName)) return Collections.EMPTY_LIST;

        final String secondTagName = getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//        if(!AllowedSecondTagName.contains(secondTagName)) return Collections.EMPTY_LIST;

        final String thirdTagName = getThirdParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//        if(!AllowedThirdTagName.contains(thirdTagName)) return Collections.EMPTY_LIST;
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
            Entity curEntity = getCurrentEntity(context).orElse(null);
            result.addAll(curEntity.getFieldList());
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
            if (curRelationship == null) return Collections.EMPTY_LIST;

            String relatedEntityName = curRelationship.getRelated().getStringValue();
            if(MyStringUtils.isEmpty(relatedEntityName)) return Collections.EMPTY_LIST;

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
            if (curExtendEntity == null) return Collections.EMPTY_LIST;

            result.addAll(curExtendEntity.getFieldList());

            Entity sourceEntity = getCurrentExtendEntitySourceEntity(context).orElse(null);
            if (sourceEntity != null) result.addAll(sourceEntity.getFieldList());

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
            if (curMemberEntity == null) return Collections.EMPTY_LIST;

            if(curAttributeName.equals(KeyMap.ATTR_FIELD_NAME)) {
                //fieldName，取MembrEntity属性joinFromAlias对应表的字段
                result.addAll(EntityUtils.getFieldListFromMemberEntity(curMemberEntity,MemberEntity.ATTR_JOIN_FROM_ALIAS));
            }else {
                //related，取MembrEntity属性entityName对应表的字段
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
            if (curECondition == null) return Collections.EMPTY_LIST;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return Collections.EMPTY_LIST;
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
                MemberEntity memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity,alias);
                result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity,MemberEntity.ATTR_ENTITY_NAME));

            }

            return result;
        }
        //ViewEntity-EntityCondition-OrderBy（fieldName）
//        if (
//                thirdTagName.equals(ViewEntity.TAG_NAME)
//                        && secondTagName.equals(EntityCondition.TAG_NAME)
//                        && firstTagName.equals(OrderBy.TAG_NAME)
//                        && curAttributeName.equals(OrderBy.ATTR_FIELD_NAME)
//        )
//        {
//            ECondition curECondition = getCurrentECondition(context).orElse(null);
//            if (curECondition == null) return Collections.EMPTY_LIST;
//            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
//            if(viewEntity == null) return Collections.EMPTY_LIST;
//            String alias ;
//            if(curECondition.getEntityAlias().getXmlAttributeValue() == null) {
//                alias = MyStringUtils.EMPTY_STRING;
//            }else {
//                alias= curECondition.getEntityAlias().getXmlAttributeValue().getValue();
//            }
//
//            if(MyStringUtils.isEmpty(alias)) {
//                //没有alias，就去当前ViewEntity的所有Fields
//                result.addAll(EntityUtils.getViewEntityFieldList(context.getProject(),viewEntity));
//            }else {
//                MemberEntity memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity,alias);
//                result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity,MemberEntity.ATTR_ENTITY_NAME));
//
//            }
//
//            return result;
//        }
        //ViewEntity-MemberEntity-EntityCondition-ECondition（fieldName）
        //ViewEntity-MemberEntity-EntityCondition-ECondition（toFieldName）
        //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（fieldName）
        //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（toFieldName）

        if ((
                fourthTagName.equals(ViewEntity.TAG_NAME)
                && thirdTagName.equals(MemberEntity.TAG_NAME)
                        && secondTagName.equals(EntityCondition.TAG_NAME)
                        && firstTagName.equals(ECondition.TAG_NAME)
                        && (curAttributeName.equals(ECondition.ATTR_FIELD_NAME) || curAttributeName.equals(ECondition.ATTR_TO_FIELD_NAME) )
        )||(
                fifthTagName.equals(ViewEntity.TAG_NAME)
                        &&fourthTagName.equals(MemberEntity.TAG_NAME)
                        && thirdTagName.equals(EntityCondition.TAG_NAME)
                        && secondTagName.equals(EConditions.TAG_NAME)
                        && firstTagName.equals(ECondition.TAG_NAME)
                        && (curAttributeName.equals(ECondition.ATTR_FIELD_NAME) || curAttributeName.equals(ECondition.ATTR_TO_FIELD_NAME) )

        ))
        {
            ECondition curECondition = getCurrentECondition(context).orElse(null);
            if (curECondition == null) return Collections.EMPTY_LIST;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return Collections.EMPTY_LIST;
            MemberEntity memberEntity;
            if(curAttributeName.equals(ECondition.ATTR_FIELD_NAME)) {
                memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity
                        ,curECondition.getEntityAlias().getXmlAttributeValue().getValue());

            }else {
                memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity
                        ,curECondition.getToEntityAlias().getXmlAttributeValue().getValue());

            }

            result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity,MemberEntity.ATTR_ENTITY_NAME));

            return result;
        }
        //ViewEntity-EntityCondition-OrderBy（fieldName）
        if (
                thirdTagName.equals(ViewEntity.TAG_NAME)
                        && secondTagName.equals(EntityCondition.TAG_NAME)
                        && firstTagName.equals(OrderBy.TAG_NAME)
                        && curAttributeName.equals(OrderBy.ATTR_FIELD_NAME)
        )
        {
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return Collections.EMPTY_LIST;
            result.addAll(EntityUtils.getViewEntityFieldList(context.getProject(),viewEntity));
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
            if(curAlias == null) return Collections.EMPTY_LIST;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return Collections.EMPTY_LIST;

            AbstractMemberEntity memberEntity = EntityUtils.getViewEntityAbstractMemberEntityByAlias(
                    viewEntity,
                    curAlias.getEntityAlias().getXmlAttributeValue().getValue()
            ).orElse(null);
            if(memberEntity == null) return Collections.EMPTY_LIST;

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
            if (curComplexAliasField == null) return Collections.EMPTY_LIST;
            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
            if(viewEntity == null) return Collections.EMPTY_LIST;
            MemberEntity memberEntity;
            memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity
                    ,curComplexAliasField.getEntityAlias().getXmlAttributeValue().getValue());

            result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity,MemberEntity.ATTR_ENTITY_NAME));

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

            if(curAutoParameters == null) return Collections.EMPTY_LIST;
            XmlAttributeValue attributeValue = curAutoParameters.getEntityName().getXmlAttributeValue();
            if(attributeValue == null) return Collections.EMPTY_LIST;
            final String entityName = attributeValue.getValue();

            result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(),entityName));
            return result;

        }

        //Service下的EntityFindOne , FieldMap (FieldName)
        if (
                secondTagName.equals(EntityFindOne.TAG_NAME)
                        && firstTagName.equals(FieldMap.TAG_NAME)
                        && curAttributeName.equals(FieldMap.ATTR_FIELD_NAME)
        )
        {
            EntityFindOne curEntityFindOne = ServiceUtils.getCurrentEntityFindOne(context).orElse(null);

            if(curEntityFindOne == null) return Collections.EMPTY_LIST;
            XmlAttributeValue attributeValue = curEntityFindOne.getEntityName().getXmlAttributeValue();
            if(attributeValue == null) return Collections.EMPTY_LIST;
            final String entityName = attributeValue.getValue();

            result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(),entityName));
            return result;

        }

        //Service下的EntityFind , Econdition (FieldName)
        if (
                secondTagName.equals(EntityFind.TAG_NAME)
                        && firstTagName.equals(ECondition.TAG_NAME)
                        && curAttributeName.equals(ECondition.ATTR_FIELD_NAME)
        )
        {
            EntityFind curEntityFind = ServiceUtils.getCurrentEntityFind(context).orElse(null);

            if(curEntityFind == null) return Collections.EMPTY_LIST;
            XmlAttributeValue attributeValue = curEntityFind.getEntityName().getXmlAttributeValue();
            if(attributeValue == null) return Collections.EMPTY_LIST;
            final String entityName = attributeValue.getValue();

            result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(),entityName));
            return result;

        }


//        if (
//                fifthTagName.equals(ViewEntity.TAG_NAME)
//                &&fourthTagName.equals(MemberEntity.TAG_NAME)
//                        && thirdTagName.equals(EntityCondition.TAG_NAME)
//                        && secondTagName.equals(EConditions.TAG_NAME)
//                        && firstTagName.equals(ECondition.TAG_NAME)
//                        && (curAttributeName.equals(ECondition.ATTR_FIELD_NAME) || curAttributeName.equals(ECondition.ATTR_TO_FIELD_NAME) )
//        )
//        {
//            ECondition curECondition = getCurrentECondition(context).orElse(null);
//            if (curECondition == null) return Collections.EMPTY_LIST;
//            ViewEntity viewEntity = getCurrentViewEntity(context).orElse(null);
//            if(viewEntity == null) return Collections.EMPTY_LIST;
//            MemberEntity memberEntity;
//            if(curAttributeName.equals(ECondition.ATTR_FIELD_NAME)) {
//                 memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity
//                        ,curECondition.getEntityAlias().getXmlAttributeValue().getValue());
//
//            }else {
//                memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity
//                        ,curECondition.getToEntityAlias().getXmlAttributeValue().getValue());
//
//            }
//
//            result.addAll(EntityUtils.getFieldListFromMemberEntity(memberEntity,MemberEntity.ATTR_ENTITY_NAME));
//
//            return result;
//        }


        return Collections.EMPTY_LIST;

    }
}
