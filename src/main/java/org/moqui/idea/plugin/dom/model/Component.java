package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.ComponentPresentationProvider;

import java.util.List;
//@Stubbed
@Presentation(icon = "org.moqui.idea.plugin.MyIcons.ComponentTag",provider = ComponentPresentationProvider.class)
public interface Component extends DomElement {
    
    String TAG_NAME = "component";
    String ATTR_NAME = "name";

    
    String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/moqui-conf-3.xsd";
    @NotNull
    //@Convert(LocationConverter.class)
    GenericAttributeValue<String> getLocation();

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

    @NotNull GenericAttributeValue<String> getVersion();

    @NotNull
    @SubTagList(DependsOn.TAG_NAME)
    List<DependsOn> getDependsOnList();

}
