package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface InitParam extends DomElement {

    public static final String TAG_NAME = "init-param";
    @NotNull
    GenericAttributeValue<String> getName();

    @NotNull
    GenericAttributeValue<String> getValue();

}
