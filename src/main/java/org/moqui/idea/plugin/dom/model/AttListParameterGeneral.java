package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface AttListParameterGeneral extends DomElement {



    
    public static final String ATTR_REQUIRED = "required";
    
    public static final String ATTR_ALLOW_HTML = "allow-html";

    @NotNull
    @Attribute(ATTR_ALLOW_HTML)
    GenericAttributeValue<String> getAllowHtml();

    @NotNull
    @Attribute(ATTR_REQUIRED)
    GenericAttributeValue<Boolean> getRequired();


}
