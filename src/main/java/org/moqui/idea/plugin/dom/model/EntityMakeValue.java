package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntityMakeValue extends AbstractEntityName {

    public static final String TAG_NAME = "entity-make-value";


//    @NotNull
//    @Convert(EntityFullNameConverter.class)
//    GenericAttributeValue<String> getEntityName();

    @NotNull GenericAttributeValue<String> getMap();
    @NotNull GenericAttributeValue<String> getValueField();



}
