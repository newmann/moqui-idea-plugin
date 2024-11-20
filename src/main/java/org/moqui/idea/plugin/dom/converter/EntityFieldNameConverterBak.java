package org.moqui.idea.plugin.dom.converter;

import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.Collection;

import static org.moqui.idea.plugin.util.EntityUtils.*;
import static org.moqui.idea.plugin.util.MyDomUtils.*;
@Deprecated
public class EntityFieldNameConverterBak extends AbstractEntityFieldNameConverter{
    @Override
    AbstractField getField(@Nullable String s, ConvertContext context) {
        if(isNotCheckElement(context)) return null;//如果不是需要检查的内容，则跳过

        Collection<AbstractField> fields = getFields(context);


        return fields.stream().filter(item->{
            final String fieldName = MyDomUtils.getValueOrEmptyString(item.getName());
            return fieldName.equals(s);
        }).findFirst().orElse(null);

    }

    @Override
    @NotNull Collection<AbstractField> getFieldVariants(ConvertContext context) {
        return getFields(context);
    }

    private Collection<AbstractField> getFields(ConvertContext context) {
        Collection<AbstractField> result = new ArrayList<>();


        String curAttributeName = getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);

//        final String firstTagName = getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//        final String secondTagName = getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//        final String thirdTagName = getThirdParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//        final String fourthTagName = getFourthParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//        final String fifthTagName = getFifthParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

//        //Entity
//        Entity curEntity = getCurrentEntity(context).orElse(null);
//        if(curEntity != null) {
//            //Entity-Relationship
//            Relationship curRelationship = getCurrentRelationship(context).orElse(null);
//            if(curRelationship != null) {
//                //Entity-Relationship-keyMap（fieldName）
//                //Entity-Relationship-keyMap（related）
//                KeyMap curKeyMap = getCurrentKeyMap(context).orElse(null);
//                if(curKeyMap != null) {
//                    switch(curAttributeName) {
//                        case KeyMap.ATTR_FIELD_NAME -> {
//                            result.addAll(curEntity.getFieldList());
//                        }
//                        case KeyMap.ATTR_RELATED -> {
//
//                            result.addAll(EntityUtils.getEntityFieldList(context.getProject(),
//                                    MyDomUtils.getValueOrEmptyString(curRelationship.getRelated())));
//                        }
//                    }
//                }
//                //Entity-Relationship-keyValue（related）
//                KeyValue curKeyValue = getCurrentKeyValue(context).orElse(null);
//                if(curKeyValue != null) {
//                    if(curAttributeName.equals(KeyValue.ATTR_RELATED)) {
//                        result.addAll(EntityUtils.getEntityFieldList(context.getProject(),
//                                MyDomUtils.getValueOrEmptyString(curRelationship.getRelated())));
//                    }
//                }
//            }
//            //Entity-Index-IndexField(name)
//            IndexField curIndexField = getCurrentIndexField(context).orElse(null);
//            if(curIndexField != null) {
//                if(curAttributeName.equals(IndexField.ATTR_NAME)) {
//                    result.addAll(curEntity.getFieldList());
//                }
//            }
//        }
//        //ExtendEntity
//        ExtendEntity curExtendEntity = getCurrentExtendEntity(context).orElse(null);
//        if(curExtendEntity != null) {
//            //ExtendEntity-Relationship
//            Relationship curRelationship = getCurrentRelationship(context).orElse(null);
//            if(curRelationship != null) {
//                //ExtendEntity-Relationship-keyMap（related）
//                //ExtendEntity-Relationship-keyValue（related）
//                KeyMap curKeyMap = getCurrentKeyMap(context).orElse(null);
//                if(curKeyMap != null) {
//                    switch(curAttributeName) {
//                        case KeyMap.ATTR_FIELD_NAME -> {
//                            //
//                            result.addAll(EntityUtils.getEntityFieldList(context.getProject(),
//                                    MyDomUtils.getValueOrEmptyString(curExtendEntity.getEntityName())));
//                        }
//                        case KeyMap.ATTR_RELATED -> {
//
//                            result.addAll(EntityUtils.getEntityFieldList(context.getProject(),
//                                    MyDomUtils.getValueOrEmptyString(curRelationship.getRelated())));
//                        }
//                    }
//                }
//            }
//            //ExtendEntity-Index-IndexField(name)
//            IndexField curIndexField = getCurrentIndexField(context).orElse(null);
//            if(curIndexField != null) {
//                if(curAttributeName.equals(IndexField.ATTR_NAME)) {
//                    result.addAll(EntityUtils.getEntityFieldList(context.getProject(),
//                            MyDomUtils.getValueOrEmptyString(curExtendEntity.getEntityName())));
//                }
//            }
//        }
//
//        //ViewEntity
//        ViewEntity curViewEntity = getCurrentViewEntity(context).orElse(null);
//        if(curViewEntity != null) {
//            //ViewEntity-MemberEntity
//            MemberEntity curMemberEntity = getCurrentMemberEntity(context).orElse(null);
//            if (curMemberEntity != null) {
//                //ViewEntity-MemberEntity-KeyMap
//                KeyMap curKeyMap = getCurrentKeyMap(context).orElse(null);
//                if(curKeyMap !=null) {
//                    //ViewEntity-MemberEntity-KeyMap（fieldName）
//                    //ViewEntity-MemberEntity-KeyMap（related）
//                    switch (curAttributeName) {
//                        case (KeyMap.ATTR_FIELD_NAME)->{
//                            //fieldName，取MemberEntity属性joinFromAlias对应表的字段
//                            result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(
//                                    curViewEntity,
//                                    MyDomUtils.getValueOrEmptyString(curMemberEntity.getJoinFromAlias())).orElse(new ArrayList<>()));
//
//                        }
//                        case (KeyMap.ATTR_RELATED)->{
//                            //related，取MemberEntity属性entityName对应表的字段
//                            result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(
//                                    curViewEntity,
//                                    MyDomUtils.getValueOrEmptyString(curMemberEntity.getEntityAlias())).orElse(new ArrayList<>()));
//
//                        }
//                    }
//                }
//            };
//            //ViewEntity-EntityCondition
//            //ViewEntity-MemberEntity 下面的EntityCondition可以复用这段处理
//                    //ViewEntity-MemberEntity-EntityCondition-ECondition（fieldName）
//                    //ViewEntity-MemberEntity-EntityCondition-ECondition（toFieldName）
//                    //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（fieldName）
//                    //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（toFieldName）
//
//            EntityCondition curEntityCondition = getCurrentEntityCondition(context).orElse(null);
//            if (curEntityCondition != null) {
//                //ViewEntity-EntityCondition-ECondition
//                ECondition curECondition = getCurrentECondition(context).orElse(null);
//                if(curECondition != null) {
//
//                    String entityAlias = MyDomUtils.getValueOrEmptyString(curECondition.getEntityAlias());
//                    String toEntityAlias = MyDomUtils.getValueOrEmptyString(curECondition.getToEntityAlias());
//                    //ViewEntity-EntityCondition-ECondition（fieldName）
//                    //ViewEntity-EntityCondition-ECondition（toFieldName）
//                    switch (curAttributeName) {
//                        case ECondition.ATTR_FIELD_NAME -> {
//                            if(entityAlias.isEmpty()) {
//                                //没有alias，就去当前ViewEntity的所有Fields
//                                result.addAll(EntityUtils.getViewEntityIndexAbstractFieldList(curViewEntity)
//                                        .orElse(new ArrayList<>()));
//
//                            }else {
//                                result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(
//                                        curViewEntity,
//                                        entityAlias).orElse(new ArrayList<>()));
//
//                            }
//                        }
//                        case ECondition.ATTR_TO_FIELD_NAME -> {
//                            //如果没有toEntityAlias，则看看是否在MemberEntity下面，如果是，就取MemberEntity的字段
//                            if(toEntityAlias.isEmpty()) {
//                               if(curMemberEntity!=null) {
//                                   result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(
//                                           curViewEntity,
//                                           MyDomUtils.getValueOrEmptyString(curMemberEntity.getEntityAlias())).orElse(new ArrayList<>()));
//
//                               }
//
//                            }else {
//
//                                result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(
//                                        curViewEntity,
//                                        toEntityAlias).orElse(new ArrayList<>()));
//                            }
//                        }
//                    }
//                }
//
//            }
//            //ViewEntity-Alias
//
//            Alias curAlias = getCurrentAlias(context).orElse(null);
//            if(curAlias != null) {
//                String entityAlias = MyDomUtils.getValueOrEmptyString(curAlias.getEntityAlias());
//                if(entityAlias.isEmpty()) {
//                    //ViewEntity-Alias-ComplexAlias-ComplexAliasField(field)
//                    //ComplexAlias可能会有多个嵌套，所以不能根据Alias的位置来判断
//                   ComplexAliasField curComplexAliasField = getCurrentComplexAliasField(context).orElse(null);
//                   if(curComplexAliasField != null) {
//                       result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(curViewEntity,
//                                       MyDomUtils.getValueOrEmptyString(curComplexAliasField.getEntityAlias()))
//                               .orElse(new ArrayList<>()));
//                   }
//                }else {
//                    //ViewEntity-Alias(field)
//                    if (curAttributeName.equals(Alias.ATTR_FIELD)) {
//                        result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(curViewEntity,
//                                        MyDomUtils.getValueOrEmptyString(curAlias.getEntityAlias()))
//                                .orElse(new ArrayList<>()));
//                    }
//                }
//            }
//            //ViewEntity-AliasAll
//            AliasAll curAliasAll = getCurrentAliasAll(context).orElse(null);
//            if(curAliasAll != null) {
//                Exclude curExclude = getCurrentExclude(context).orElse(null);
//                if(curExclude != null) {
//                    //ViewEntity-AliasAll-Exclude（Field）
//                    if (curAttributeName.equals(Exclude.ATTR_FIELD)) {
//                        result.addAll(EntityUtils.getIndexAbstractFieldListFromViewEntityByAlias(curViewEntity,
//                                        MyDomUtils.getValueOrEmptyString(curAliasAll.getEntityAlias()))
//                                .orElse(new ArrayList<>()));
//                    }
//                }
//            }
//
//        }
//        //Service，Seca等下的EntityFindOne
//        EntityFindOne curEntityFindOne = ServiceUtils.getCurrentEntityFindOne(context).orElse(null);
//        if(curEntityFindOne != null) {
//            //EntityFindOne , FieldMap (FieldName)
//            FieldMap curFieldMap = getCurrentFieldMap(context).orElse(null);
//            if(curFieldMap != null) {
//                if(curAttributeName.equals(FieldMap.ATTR_FIELD_NAME)) {
//                    result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(),
//                            MyDomUtils.getValueOrEmptyString(curEntityFindOne.getEntityName())));
//                }
//            }
//
//        }
//        //Service、Seca等下的EntityFind
//        //Service、Seca等下的EntityDeleteByCondition
//        //Service、Seca等下的EntityFindCount
//        //只和EntityFind等有关，就不用判断在哪个Tag下
//        AbstractEntityName nameAbstract = null;
//        nameAbstract = ServiceUtils.getCurrentEntityFind(context).orElse(null);
//        if(nameAbstract == null) {
//            nameAbstract = ServiceUtils.getCurrentEntityDeleteByCondition(context).orElse(null);
//        }
//        if(nameAbstract == null) {
//            nameAbstract = ServiceUtils.getCurrentEntityFindCount(context).orElse(null);
//        }
//
//        if(nameAbstract != null) {
//            //EntityFind , ECondition (FieldName)
//            //EntityFind , EConditions，ECondition (FieldName, ToFieldName)
//            //EntityFind , HavingEConditions，ECondition (FieldName)
//            //EntityDeleteByCondition，ECondition（FieldName）
//            //EntityFindCount - ECondition (FieldName)
//            //EntityFindCount -EConditions - ECondition (FieldName)
//            ECondition curECondition = getCurrentECondition(context).orElse(null);
//            if(curECondition != null) {
//                //不管是FieldName还是ToFieldName都取当前的Entity字段
//                if(curAttributeName.equals(ECondition.ATTR_FIELD_NAME) || curAttributeName.equals(ECondition.ATTR_TO_FIELD_NAME)) {
//                    result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject(),
//                            MyDomUtils.getValueOrEmptyString(nameAbstract.getEntityName())));
//
//                }
//            }
//
//        }
//
//        //Service
//        Service curService = ServiceUtils.getCurrentService(context).orElse(null);
//        if(curService!= null) {
//            //Service下的AutoParameters
//            AutoParameters curAutoParameters = EntityUtils.getCurrentAutoParameters(context).orElse(null);
//            if(curAutoParameters != null) {
//                //Service下的AutoParameters-Exclude（FieldName）
//                Exclude curExclude = getCurrentExclude(context).orElse(null);
//                if(curExclude != null) {
//                    if(curAttributeName.equals(Exclude.ATTR_FIELD_NAME)) {
//                        String curEntityName = MyDomUtils.getValueOrEmptyString(curAutoParameters.getEntityName());
//                        if(MyStringUtils.EMPTY_STRING.equals(curEntityName)) {
//                            //如果当前的AutoParameters中没有定义entityName，则有可能是在Service的inParameters中，这时，service的noun就是EntityName
//                            curEntityName = MyDomUtils.getValueOrEmptyString(curService.getNoun());
//                        }
//
//                        result.addAll(EntityUtils.getEntityOrViewEntityFields(context.getProject()
//                                ,curEntityName));
//                    }
//
//                }
//            }
//
//
//
//        }

        return result;

    }

    @Override
    boolean isNotCheckElement(ConvertContext context) {
        String curAttributeName = getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);

        final String firstTagName = getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String secondTagName = getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String thirdTagName = getThirdParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String fourthTagName = getFourthParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String fifthTagName = getFifthParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

        //filterMapList - fieldMap(fieldName)，todo 现在还不处理，所以不提示

        if(curAttributeName.equals(FieldMap.ATTR_FIELD_NAME) && firstTagName.equals(FieldMap.TAG_NAME)
                && secondTagName.equals(FilterMapList.TAG_NAME)) return true;

        //如果是$｛｝，表示变量，则不需要提示
        String value = getCurrentAttributeStringValue(context).orElse(MyStringUtils.EMPTY_STRING);
        return (EntityUtils.fieldIsGroovyVariable(value));
        

    }
}
