package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TextTemplateConverter;

public interface EntityOptions extends DomElement {

    String TAG_NAME = "entity-options";

    @NotNull
    @SubTag(EntityFind.TAG_NAME)
    EntityFind getEntityFind();

    @NotNull GenericAttributeValue<String> getKey();
    @NotNull
    @Convert(TextTemplateConverter.class)
    GenericAttributeValue<String> getText();

}
