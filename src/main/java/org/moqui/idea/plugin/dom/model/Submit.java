package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface Submit extends DomElement {
    public static final String TAG_NAME = "submit";

    @NotNull
    @SubTag(Image.TAG_NAME)
    Image getImage();

    @NotNull GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getConfirmation();
    @NotNull GenericAttributeValue<String> getType();

    @NotNull GenericAttributeValue<String> getIcon();

}
