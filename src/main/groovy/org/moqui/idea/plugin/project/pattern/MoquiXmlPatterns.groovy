package org.moqui.idea.plugin.project.pattern

import com.intellij.patterns.XmlAttributeValuePattern
import com.intellij.patterns.XmlFilePattern
import com.intellij.patterns.XmlNamedElementPattern
import com.intellij.patterns.XmlTagPattern
import org.moqui.idea.plugin.dom.model.Alias
import org.moqui.idea.plugin.dom.model.AliasAll
import org.moqui.idea.plugin.dom.model.AutoParameters
import org.moqui.idea.plugin.dom.model.ComplexAlias
import org.moqui.idea.plugin.dom.model.ComplexAliasField
import org.moqui.idea.plugin.dom.model.ECondition
import org.moqui.idea.plugin.dom.model.EConditions
import org.moqui.idea.plugin.dom.model.Entities
import org.moqui.idea.plugin.dom.model.Entity
import org.moqui.idea.plugin.dom.model.EntityCondition
import org.moqui.idea.plugin.dom.model.EntityFindOne
import org.moqui.idea.plugin.dom.model.Exclude
import org.moqui.idea.plugin.dom.model.ExtendEntity
import org.moqui.idea.plugin.dom.model.Index
import org.moqui.idea.plugin.dom.model.IndexField
import org.moqui.idea.plugin.dom.model.KeyMap
import org.moqui.idea.plugin.dom.model.KeyValue
import org.moqui.idea.plugin.dom.model.MemberEntity
import org.moqui.idea.plugin.dom.model.Relationship
import org.moqui.idea.plugin.dom.model.Service
import org.moqui.idea.plugin.dom.model.ViewEntity

import static com.intellij.patterns.StandardPatterns.string
import static com.intellij.patterns.XmlPatterns.xmlAttribute
import static com.intellij.patterns.XmlPatterns.xmlAttributeValue
import static com.intellij.patterns.XmlPatterns.xmlAttributeValue
import static com.intellij.patterns.XmlPatterns.xmlAttributeValue
import static com.intellij.patterns.XmlPatterns.xmlAttributeValue
import static com.intellij.patterns.XmlPatterns.xmlAttributeValue
import static com.intellij.patterns.XmlPatterns.xmlAttributeValue
import static com.intellij.patterns.XmlPatterns.xmlFile
import static com.intellij.patterns.XmlPatterns.xmlTag

class MoquiXmlPatterns {

    public static final XmlAttributeValuePattern ENTITY_FIELD_CALL = xmlAttributeValue().andOr(
            xmlAttributeValue().inside(fieldNameAttr().withParent(keyMapTag()).inside(relationshipTag().inside(entityTag()))),
            xmlAttributeValue().inside(nameAttr().withParent(indexFieldTag()).insideinside(keyMapTag().inside(viewLinkTag()))),
            xmlAttributeValue().inside(relFieldNameAttr().inside(keyMapTag()))
    )



    //============================================
    //       UTILITY METHODS
    //============================================
    static XmlNamedElementPattern.XmlAttributePattern makeAttrPattern(String attrName) {
        return xmlAttribute().withName(attrName)
    }
    static XmlNamedElementPattern.XmlAttributePattern makeAttrAndValPattern(String attrName, String attrVal) {
        return xmlAttribute().withName(attrName).withValue(string().contains(attrVal))
    }
    static XmlTagPattern.Capture makeTagPattern(String tagName) {
        return xmlTag().withName(tagName)
    }
    static XmlTagPattern.Capture makeTagPatternWithNotCond(String tagName, XmlTagPattern notPattern) {
        return xmlTag().withName(tagName).andNot(notPattern)
    }

    static XmlFilePattern.Capture entityXmlFile() { xmlFile().withRootTag(entitiesXmlTag()) }

    static XmlNamedElementPattern.XmlAttributePattern nameAttr() { return makeAttrPattern('name') }
    static XmlNamedElementPattern.XmlAttributePattern fieldAttr() { xmlAttribute('field') }
    static XmlNamedElementPattern.XmlAttributePattern fieldNameAttr() { xmlAttribute('field-name') }
    static XmlNamedElementPattern.XmlAttributePattern relatedNameAttr() { xmlAttribute('related') }
    static XmlNamedElementPattern.XmlAttributePattern toFieldNameAttr() { xmlAttribute('to-field-name') }

    static XmlNamedElementPattern.XmlAttributePattern serviceLikeAttr() { return xmlAttribute().withName('service', 'service-name') }

    static XmlNamedElementPattern.XmlAttributePattern javaTypeAttrValue() { return makeAttrAndValPattern('type', 'java') }
    static XmlNamedElementPattern.XmlAttributePattern interfaceTypeAttrValue() { return makeAttrAndValPattern('type', 'interface') }
    static XmlNamedElementPattern.XmlAttributePattern entityAutoTypeAttrValue() { return makeAttrAndValPattern('type', 'entity-auto') }

    static XmlTagPattern.Capture entitiesXmlTag() { xmlTag().withName(Entities.TAG_NAME) }

    static XmlTagPattern.Capture entityTag() { return makeTagPattern(Entity.TAG_NAME) }
    static XmlTagPattern.Capture relationshipTag() { return makeTagPattern(Relationship.TAG_NAME) }
    static XmlTagPattern.Capture indexFieldTag() { return makeTagPattern(IndexField.TAG_NAME) }
    static XmlTagPattern.Capture indexTag() { return makeTagPattern(Index.TAG_NAME) }


    static XmlTagPattern.Capture viewEntityTag() { return makeTagPattern(ViewEntity.TAG_NAME) }
    static XmlTagPattern.Capture memberEntityTag() { return makeTagPattern(MemberEntity.TAG_NAME) }
    static XmlTagPattern.Capture entityConditionTag() { return makeTagPattern(EntityCondition.TAG_NAME) }
    static XmlTagPattern.Capture eConditionTag() { return makeTagPattern(ECondition.TAG_NAME) }
    static XmlTagPattern.Capture eConditionsTag() { return makeTagPattern(EConditions.TAG_NAME) }
    static XmlTagPattern.Capture excludeTag() { return makeTagPattern(Exclude.TAG_NAME) }

    static XmlTagPattern.Capture ExtendEntityTag() { return makeTagPattern(ExtendEntity.TAG_NAME) }

    static XmlTagPattern.Capture keyMapTag() { xmlTag().withName(KeyMap.TAG_NAME) }
    static XmlTagPattern.Capture keyValueTag() { xmlTag().withName(KeyValue.TAG_NAME) }
    static XmlTagPattern.Capture aliasTag() { xmlTag().withName(Alias.TAG_NAME) }
    static XmlTagPattern.Capture aliasAllTag() { xmlTag().withName(AliasAll.TAG_NAME) }
    static XmlTagPattern.Capture complexAliasTag() { xmlTag().withName(ComplexAlias.TAG_NAME) }
    static XmlTagPattern.Capture complexAliasFieldTag() { xmlTag().withName(ComplexAliasField.TAG_NAME) }

    static XmlTagPattern.Capture ServiceEntityTag() { return makeTagPattern(Service.TAG_NAME) }
    static XmlTagPattern.Capture autoParametersTag() { return makeTagPattern(AutoParameters.TAG_NAME) }
    static XmlTagPattern.Capture entityFindTag() { return makeTagPattern(EntityFindOne.TAG_NAME) }

}
