package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface RowCol extends WidgetElementsList {

    String TAG_NAME = "row-col";
    String ATTR_LG = "lg";

    String ATTR_MD = "md";

    String ATTR_SM = "sm";

    String ATTR_XS = "xs";
    String ATTR_STYLE = "style";

    @NotNull
    @Attribute(ATTR_LG)
    GenericAttributeValue<String> getLg();

    @NotNull
    @Attribute(ATTR_MD)
    GenericAttributeValue<String> getMd();
    @NotNull
    @Attribute(ATTR_SM)
    GenericAttributeValue<String> getSm();
    @NotNull
    @Attribute(ATTR_XS)
    GenericAttributeValue<String> getXs();

    @NotNull
    @Attribute(ATTR_STYLE)
    GenericAttributeValue<String> getStyle();

}
