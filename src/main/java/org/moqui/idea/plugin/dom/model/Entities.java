package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//@Stubbed
public interface Entities extends DomElement {
    
    String TAG_NAME = "entities";
    
    String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/entity-definition-3.xsd";

    @NotNull
//    @Stubbed
    @SubTagList(Entity.TAG_NAME)
    List<Entity> getEntities();

    @NotNull
    @SubTagList(ExtendEntity.TAG_NAME)
    List<ExtendEntity> getExtendEntities();

    @NotNull
    @SubTagList(ViewEntity.TAG_NAME)
    List<ViewEntity> getViewEntities();

//    @NotNull
//    @Attribute("xmlns:xsi")
//    GenericAttributeValue<String> getXmlnsXsi();
//
//    @NotNull
//    @Attribute("xsi:noNamespaceSchemaLocation")
//    GenericAttributeValue<String> getXsiNoNamespaceSchemaLocation();

}
