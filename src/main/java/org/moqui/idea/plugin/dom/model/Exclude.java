package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface Exclude extends DomElement {

    public static final String TAG_NAME = "exclude";


    public static final String ATTR_FIELD="field";

    public static final String ATTR_FIELD_NAME="field-name";

    @NotNull
    @Attribute(ATTR_FIELD)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getField();

    /**
     * used in auto-fields-service
      */
        @NotNull
    GenericAttributeValue<String> getParameterName();

    /**
     * used in auto-fields-entity
     * @return
     */
    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getFieldName();

}
