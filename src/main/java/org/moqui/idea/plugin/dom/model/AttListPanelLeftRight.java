package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface AttListPanelLeftRight extends DomElement {




    @NotNull
    GenericAttributeValue<Boolean> getClosable();

    @NotNull
    GenericAttributeValue<Boolean> getResizable();

    @NotNull
    GenericAttributeValue<String> getSpacing();
    @NotNull
    GenericAttributeValue<String> getSize();
    @NotNull
    GenericAttributeValue<Boolean> getSizeMin();
    @NotNull
    GenericAttributeValue<Boolean> getSizeMax();

}
