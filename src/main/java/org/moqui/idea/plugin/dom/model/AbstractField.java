package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ParameterTypeConverter;

/**
 * Field，Alias，Parameter的父类，以便进行统一处理
 */
public interface AbstractField extends DomElement {
    String ATTR_NAME = "name";
    String ATTR_TYPE = "type";
    @NameValue
    @Required
    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_TYPE)
    @Convert(ParameterTypeConverter.class)
    GenericAttributeValue<String> getType();
}
