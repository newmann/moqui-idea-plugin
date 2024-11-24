package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.converter.TransitionConverter;

import java.util.List;


public interface AbstractForm extends DomElement {

    public static final String ATTR_NAME = "name";
    public static final String ATTR_EXTENDS = "extends";

    public static final String ATTR_TRANSITION = "transition";

    @NotNull
    @Attribute(ATTR_NAME)
    @NameValue
    GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_EXTENDS)
    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getExtends();

    @NotNull
    @Attribute(ATTR_TRANSITION)
    @Convert(TransitionConverter.class)
    GenericAttributeValue<String> getTransition();

    @NotNull
    @SubTagList(Field.TAG_NAME)
    List<Field> getFieldList();


}
