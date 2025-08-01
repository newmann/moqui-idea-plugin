package org.moqui.idea.plugin.dom.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyBundle;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.*;

import java.util.Collection;
import java.util.List;

public class MoquiDomCheckResoleInspection extends BasicDomElementsInspection<DomElement> {
    public MoquiDomCheckResoleInspection() {
        super(DomElement.class);
    }
    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getGroupDisplayName() {
        return MyBundle.message("inspection.group");
    }

    @Override
    public @NotNull String getShortName() {
        return MyBundle.message("inspection.MoquiDomCheckResole");
    }

    private static boolean isElementInsideManagedFile(GenericDomValue<?> value) {
        XmlFile xmlFile = DomUtil.getFile(value);
//        if (xmlFile == null) {
//            return false;
//        }
        return EntityUtils.isEntitiesFile(xmlFile)
                || ServiceUtils.isServicesFile(xmlFile)
                || ScreenUtils.isScreenFile(xmlFile)
                || SecaUtils.isSecasFile(xmlFile)
                || EecaUtils.isEecasFile(xmlFile)
                || EmecaUtils.isEmecasFile(xmlFile)
                || WidgetTemplateUtils.isWidgetTemplateFile(xmlFile)
                || RestApiUtils.isRestApiFile(xmlFile)
                || MoquiConfUtils.isMoquiConfFile(xmlFile)
                || ComponentUtils.isComponentFile(xmlFile);
    }
    @Override
    protected boolean shouldCheckResolveProblems(GenericDomValue<?> value) {
        return isElementInsideManagedFile(value);
    }

    /**
     * 对Converter中不方便检查的，在这里进行检查
     *
     * @param element element to check
     * @param holder  a place to add problems to
     * @param helper  helper object
     */
    @Override
    protected void checkDomElement(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
//        checkMultipleFields(element, holder, helper);

//        LocationUtils.inspectLocationFromDomElement(element,holder);

//        if (element instanceof ServiceCall serviceCall) {
//            ServiceUtils.inspectServiceCallFromAttribute(serviceCall.getName(), holder);
//            return;
//        }

        if ((element instanceof Relationship relationship)) {

            EntityUtils.inspectEntityFromAttribute(relationship.getRelated(),holder);
            return;
        }

        if ((element instanceof MemberEntity memberEntity)) {
            EntityUtils.inspectEntityFromAttribute(memberEntity.getEntityName(),holder);
            return;
        }

        if(element instanceof ExtendEntity extendEntity) {
            EntityUtils.inspectExtendEntity(extendEntity, holder);
            return;
        }
        if ((element instanceof Eeca eeca)) {
            EntityUtils.inspectEntityFromAttribute(eeca.getEntity(),holder);
            return;
        }

        if ((element instanceof AbstractEntityName abstractEntityName)) {
            EntityUtils.inspectEntityFromAttribute(abstractEntityName.getEntityName(),holder);
            return;
        }
        if(element instanceof TransitionInclude transitionInclude) {
            ScreenUtils.inspectTransitionInclude(transitionInclude, holder);
            return;
        }

        if ((element instanceof Seca seca)) {
            ServiceUtils.inspectServiceCallFromAttribute(seca.getService(), holder);
        }
//        super.checkDomElement(element, holder, helper);
    }

    /**
     *     多字段字符串的检查
     *      *         EntityFindOne , SelectField (FieldName)，
     *      *         EntityFind , SelectField (FieldName)，
     *      *         EntityFindCount , SelectField (FieldName)，
     *      *
     *      *         EntityFind , OrderBy (FieldName)，
     *      *         EntityFindCount , OrderBy (FieldName)，
     *      *
     *      *         EntityFind, SearchFormInputs(DefaultOrderBy)
     * @param element
     * @param holder
     * @param helper
     */
    private void checkMultipleFields(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        DomElement parent;
        String entityName = MyStringUtils.EMPTY_STRING;
        String fieldsString;
        GenericAttributeValue attributeValue;
        if((element instanceof SelectField)
                || (element instanceof  OrderBy)
                || (element instanceof  SearchFormInputs)
        ){
            parent = element.getParent();
            if(parent == null) return;
            if(parent instanceof AbstractEntityName entityNameAbstract) {
                entityName = MyDomUtils.getXmlAttributeValueString(entityNameAbstract.getEntityName().getXmlAttributeValue())
                        .orElse(MyStringUtils.EMPTY_STRING);

            }
            if(entityName.equals(MyStringUtils.EMPTY_STRING)) return;

            if(element instanceof SelectField selectField) {
                fieldsString = MyDomUtils.getXmlAttributeValueString(selectField.getFieldName()).orElse(MyStringUtils.EMPTY_STRING);
                attributeValue = selectField.getFieldName();

            }else if(element instanceof OrderBy orderBy) {
                fieldsString = MyDomUtils.getXmlAttributeValueString(orderBy.getFieldName()).orElse(MyStringUtils.EMPTY_STRING);
                attributeValue = orderBy.getFieldName();
            }else{
                SearchFormInputs searchFormInputs = (SearchFormInputs) element;
                fieldsString = MyDomUtils.getXmlAttributeValueString(searchFormInputs.getDefaultOrderBy()).orElse(MyStringUtils.EMPTY_STRING);
                attributeValue = searchFormInputs.getDefaultOrderBy();
            }
//            String[] fieldNameArray = fieldsString.split(",");
//            if (fieldNameArray.length == 0) return;
            List<FieldDescriptor> fieldStringList = EntityUtils.extractFieldDescriptorList(fieldsString,1);

            if(fieldStringList.isEmpty()) return;


            Collection<IndexAbstractField> fieldList = EntityUtils.getEntityOrViewEntityFields(element.getXmlElement().getProject(), entityName);
            for(FieldDescriptor fieldString : fieldStringList) {
                if(fieldString.isContainGroovyVariable() || fieldString.isEmpty()) continue;

                IndexAbstractField field = fieldList.stream().filter(item -> {
                    String itemFieldName = MyDomUtils.getValueOrEmptyString(item.getName());
                    return itemFieldName.equals(fieldString.getFieldName());
                }).findFirst().orElse(null);
                if (field == null) {
                    holder.createProblem(attributeValue, ProblemHighlightType.ERROR, "字段[" + fieldString.getFieldName() + "]没有找到对应的定义",
                            TextRange.from(1 + fieldString.getFieldNameBeginIndex(), 1 + fieldString.getFieldNameEndIndex()));//这里要用checkFieldName
                }
            }
//            int startIndex = 1;
//
//            for (String s : fieldNameArray) {
//                if (MyStringUtils.isEmpty(s)) {
//                    startIndex = startIndex + 1;
//                    continue;
//                }
//
//                String fieldName;
//
//
//                //判断fieldName的第一个字符是否为控制字符，如果是，则跳过第一个字符
//                if (ServiceUtils.ORDER_BY_COMMANDER.contains(s.substring(0, 1))) {
//                    fieldName = s.substring(1);
//                    startIndex = startIndex + 1;
//                } else {
//                    fieldName = s;
//                }
//                //跳过field前面的空字符
//                while (fieldName.charAt(0) == ' ') {
//                    fieldName = fieldName.substring(1);
//                    startIndex = startIndex + 1;
//                }
//
//                //如果字段为变量定义，类似$｛。。。｝，则跳过，不进行处理
//                if (fieldName.contains("${")) continue;
//
//                final String checkFieldName = fieldName.trim();//将field后面的空字符删除
//
//                AbstractField field = fieldList.stream().filter(item -> {
//                    String itemFieldName = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
//                            .orElse(MyStringUtils.EMPTY_STRING);
//                    return itemFieldName.equals(checkFieldName);
//                }).findFirst().orElse(null);
//                if (field == null) {
//                    holder.createProblem(attributeValue, ProblemHighlightType.ERROR, "字段[" + checkFieldName + "]没有找到对应的定义",
//                            TextRange.from(startIndex, checkFieldName.length()));//这里要用checkFieldName
//
////                    result[i] = new PsiRef(element,
////                            new TextRange(startIndex,startIndex+fieldName.length()),
////                            field.getName().getXmlAttributeValue());
//
//                }
//                startIndex = startIndex + fieldName.length() + 1;//这里要用带后空字符的fieldName，以便startIndex位置正确
//
//            }


        }

    }

}

