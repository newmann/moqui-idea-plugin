package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TextTemplateConverter;

public interface ListOptions extends DomElement {

    public static final String TAG_NAME = "list-options";


    @NotNull GenericAttributeValue<String> getList();
    @NotNull GenericAttributeValue<String> getKey();
    @NotNull
    @Convert(TextTemplateConverter.class)
    GenericAttributeValue<String> getText();
}
