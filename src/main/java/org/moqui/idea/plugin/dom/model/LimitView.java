package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface LimitView extends DomElement {

    String TAG_NAME = "limit-view";

    @NotNull
    GenericAttributeValue<String> getViewIndex();

    @NotNull
    GenericAttributeValue<String> getViewSize();

}
