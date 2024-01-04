package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.reflect.DomGenericInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Service extends DomElement {
    public static final String TAG_NAME = "service";
    public static final String ATTR_VERB = "verb";
    public static final String ATTR_NOUN = "noun";
    public static final String ATTR_TYPE = "type";
    @NotNull
    @Attribute(ATTR_VERB)
    GenericAttributeValue<String> getVerb();

    @NotNull
    @Attribute(ATTR_NOUN)
    GenericAttributeValue<String> getNoun();

    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

}
