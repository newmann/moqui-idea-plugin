package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFullNameConverter;

import java.util.List;

public interface AutoParameters extends AttListParameterGeneral {
    public static final String TAG_NAME = "auto-parameters";

    public static final String ATTR_ENTITY_NAME = "entity-name";
    public static final String ATTR_INCLUDE = "include";

    @NotNull
    @Attribute(ATTR_INCLUDE)
    GenericAttributeValue<String> getInclude();

    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
    @Convert(EntityFullNameConverter.class)
    GenericAttributeValue<String> getEntityName();


    @NotNull
    @SubTagList(Exclude.TAG_NAME)
    List<Exclude> getExcludeList();


}
