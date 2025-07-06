package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.converter.LocationReferenceConverter;
import org.moqui.idea.plugin.dom.converter.TransitionReferenceConverter;

import java.util.List;


public interface AbstractForm extends DomElement {

    String ATTR_NAME = "name";
    String ATTR_EXTENDS = "extends";
    String ATTR_TRANSITION = "transition";

    @NotNull
    @Attribute(ATTR_NAME)
    @NameValue
    GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_EXTENDS)
//    @Convert(LocationConverter.class)
    @Referencing(LocationReferenceConverter.class)
    GenericAttributeValue<String> getExtends();

    @NotNull
    @Attribute(ATTR_TRANSITION)
    @Referencing(TransitionReferenceConverter.class)
    GenericAttributeValue<String> getTransition();

    @NotNull
    @SubTagList(Field.TAG_NAME)
    List<Field> getFieldList();


}
