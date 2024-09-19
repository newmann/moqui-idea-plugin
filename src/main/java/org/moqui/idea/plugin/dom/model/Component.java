package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.presentation.ComponentPresentationProvider;
import org.moqui.idea.plugin.dom.presentation.EntityPresentationProvider;

import java.util.List;
@Stubbed
@Presentation(icon = "MoquiIcons.ComponentTag",provider = ComponentPresentationProvider.class)
public interface Component extends DomElement {
    public static final String TAG_NAME = "component";
    public static final String ATTR_NAME = "name";

    public static final String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    public static final String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/moqui-conf-3.xsd";
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
