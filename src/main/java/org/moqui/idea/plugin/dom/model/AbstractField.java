package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.AllPackageConverter;

/**
 * Field，Alias，Parameter的父类，以便进行统一处理
 */
public interface AbstractField extends DomElement {
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TYPE = "type";
    @NameValue
    @Required
    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();
}
