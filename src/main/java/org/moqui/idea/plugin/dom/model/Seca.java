package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Seca extends DomElement {
    public static final String TAG_NAME = "seca";
    public static final String ATTR_ID = "id";
    public static final String ATTR_SERVICE = "service";
    public static final String ATTR_WHEN = "when";
    public static final String ATTR_RUN_ON_ERROR = "run_on_error";
    @NotNull
    @Attribute(ATTR_ID)
    GenericAttributeValue<String> getId();

    @NotNull
    @Attribute(ATTR_SERVICE)
    GenericAttributeValue<String> getService();

    @NotNull
    @Attribute(ATTR_WHEN)
    GenericAttributeValue<String> getWhen();

    @NotNull
    @Attribute(ATTR_RUN_ON_ERROR)
    GenericAttributeValue<String> getRunOnError();

}
