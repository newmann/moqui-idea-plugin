package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface PathParameter extends AttListParameterGeneral {

    String TAG_NAME = "path-parameter";


    String ATTR_NAME = "name";

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();


}
