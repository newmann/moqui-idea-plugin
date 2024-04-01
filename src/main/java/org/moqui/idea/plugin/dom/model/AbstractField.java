package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.AllPackageConverter;

/**
 * Field，Alias的父类，以便进行统一处理
 */
public interface AbstractField extends DomElement {
    public static final String ATTR_NAME = "name";

    @NameValue
    @Required
    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

}
