package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RootScreen extends DomElement {
    public static final String TAG_NAME = "root-screen";

    @NotNull
    GenericAttributeValue<String> getHost();
    @NotNull
    GenericAttributeValue<Boolean> getLocation();


}
