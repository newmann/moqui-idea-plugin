package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
//@Stubbed
public interface EntityFacadeXml extends DomElement {

    public static final String TAG_NAME = "entity-facade-xml";

    public static final String ATTR_TYPE = "type";

    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

    //
//    @NotNull
//    @Attribute("xsi:noNamespaceSchemaLocation")
//    GenericAttributeValue<String> getXsiNoNamespaceSchemaLocation();

}
