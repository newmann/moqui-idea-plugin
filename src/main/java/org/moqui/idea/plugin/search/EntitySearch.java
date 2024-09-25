package org.moqui.idea.plugin.search;

import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Optional;

/**
 * 根据Entity Tag，找到所有使用该entity的地方
 */
public class EntitySearch extends QueryExecutorBase<XmlElement, DefinitionsScopedSearch.SearchParameters> {

    public EntitySearch(){
        super(true);
    }

    @Override
    public void processQuery(DefinitionsScopedSearch.SearchParameters queryParameters,
                             @NotNull Processor<? super XmlElement> consumer) {

//        if(!(queryParameters.getElement() instanceof XmlTag)) return;
//
//        final XmlTag xmlTag = (XmlTag)queryParameters.getElement();
//
//        if(!(xmlTag.getName().equals(Entity.TAG_NAME))) return;
//
//        final String entityName = xmlTag.getAttributeValue(Entity.ATTR_NAME_ENTITY_NAME);
//        final String packageName = xmlTag.getAttributeValue(Entity.ATTR_NAME_PACKAGE);
//
//        List<XmlAttributeValue> relatedElements = DomUtils.findEntityRelatedPsiElement(xmlTag.getProject(),
//                EntityUtils.getFullName(entityName,packageName));
//        for(XmlAttributeValue relatedElement : relatedElements) {
//            consumer.process(relatedElement);
//        }

//        //只有entity或view entity定义
        final XmlAttributeValue attributeValue = (XmlAttributeValue) queryParameters.getElement();
        final XmlAttribute attribute = (XmlAttribute) attributeValue.getParent();

        if(!attribute.getName().equals("entity-name")) return;
        if(!ServiceUtils.isServicesFile(attribute.getContainingFile())) return;

        Optional<XmlElement> entityXmlElement =
                EntityUtils.getEntityOrViewEntityXmlElementByName(attribute.getProject(),attribute.getValue());

        if (entityXmlElement.isEmpty()) return;
        System.out.println("process: " + entityXmlElement.get().getText());

        consumer.process(entityXmlElement.get());

//        System.out.println("current PsiElement class is: " + element.getClass().toString() );

    }
}
