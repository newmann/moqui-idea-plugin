package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Eeca extends DomElement {
    public static final String TAG_NAME = "seca";
    public static final String ATTR_ID = "id";
    public static final String ATTR_ENTITY = "entity";
    public static final String ATTR_ON_CREATE = "on_create";
    public static final String ATTR_ON_UPDATE = "on_update";
    public static final String ATTR_ON_DELETE = "on_delete";
    public static final String ATTR_RUN_ON_ERROR = "run_on_error";
    public static final String ATTR_GET_ENTIRE_ENTITY = "get_entire_entity";
    @NotNull
    @Attribute(ATTR_ID)
    GenericAttributeValue<String> getId();

    @NotNull
    @Attribute(ATTR_ENTITY)
    GenericAttributeValue<String> getEntity();

    @NotNull
    @Attribute(ATTR_ON_CREATE)
    GenericAttributeValue<String> getOnCreate();
    @NotNull
    @Attribute(ATTR_ON_UPDATE)
    GenericAttributeValue<String> getOnUpdate();
    @NotNull
    @Attribute(ATTR_ON_DELETE)
    GenericAttributeValue<String> getOnDelete();

    @NotNull
    @Attribute(ATTR_RUN_ON_ERROR)
    GenericAttributeValue<String> getRunOnError();

    @NotNull
    @Attribute(ATTR_RUN_ON_ERROR)
    GenericAttributeValue<String> getGetEntireEntity();
}
