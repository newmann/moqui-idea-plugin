package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HideParameters extends DomElement {
    public static final String TAG_NAME = "hide-parameters";

    public static final String ATTR_PARAMETER_MAP = "parameter-map";


    @NotNull
    @Attribute(ATTR_PARAMETER_MAP)
    GenericAttributeValue<String> getParameterMap();



    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();


}
