package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PathParameter extends AttListParameterGeneral {
    public static final String TAG_NAME = "path-parameter";


    public static final String ATTR_NAME = "name";

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();


}
