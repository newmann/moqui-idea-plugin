package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityAndViewNameReferenceConverter;

import java.util.List;

public interface AutoParameters extends AttListParameterGeneral {
    
    String TAG_NAME = "auto-parameters";

    
    String ATTR_ENTITY_NAME = "entity-name";
    String ATTR_INCLUDE = "include";

    @NotNull
    @Attribute(ATTR_INCLUDE)
    GenericAttributeValue<String> getInclude();

    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityAndViewNameReferenceConverter.class)
    GenericAttributeValue<String> getEntityName();


    @NotNull
    @SubTagList(Exclude.TAG_NAME)
    List<Exclude> getExcludeList();


}
