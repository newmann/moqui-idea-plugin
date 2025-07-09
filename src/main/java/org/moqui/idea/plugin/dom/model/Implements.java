package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.InterfaceConverter;

public interface Implements extends DomElement {

    String TAG_NAME = "implements";

    String ATTR_SERVICE = "service";

    String ATTR_REQUIRED = "required";

    @NotNull
    @Attribute(ATTR_SERVICE)
    @Convert(InterfaceConverter.class)
    GenericAttributeValue<String> getService();
    @NotNull
    @Attribute(ATTR_REQUIRED)
    GenericAttributeValue<Boolean> getRequired();


}
