package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface DateFilter extends DomElement {
    
    String TAG_NAME = "date-filter";
    
    String ATTR_VALID_DATE = "valid-date";
    
    String ATTR_ENTITY_ALIAS = "entity-alias";
    
    String ATTR_FROM_FIELD_NAME = "from-field-name";
    
    String ATTR_THRU_FIELD_NAME = "thru-field-name";
    @NotNull
    @Attribute(ATTR_VALID_DATE)
    GenericAttributeValue<String> getValidDate();
    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    GenericAttributeValue<String> getEntityAlias();
    @NotNull
    @Attribute(ATTR_FROM_FIELD_NAME)
    GenericAttributeValue<String> getFromFieldName();
    @NotNull
    @Attribute(ATTR_THRU_FIELD_NAME)
    GenericAttributeValue<String> getThruFieldName();

    @NotNull
    GenericAttributeValue<Boolean> getIgnoreIfEmpty();

    @NotNull
    GenericAttributeValue<String> getIgnore();

}
