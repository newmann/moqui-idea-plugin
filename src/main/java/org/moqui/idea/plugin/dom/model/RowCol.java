package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RowCol extends WidgetElementsList {
    public static final String TAG_NAME = "row-col";


    @NotNull GenericAttributeValue<String> getLg();
    @NotNull GenericAttributeValue<String> getMd();
    @NotNull GenericAttributeValue<String> getSm();
    @NotNull GenericAttributeValue<String> getXs();
    @NotNull GenericAttributeValue<String> getStyle();

}
