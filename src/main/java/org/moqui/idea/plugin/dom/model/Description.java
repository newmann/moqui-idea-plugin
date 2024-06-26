package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;

public interface Description extends DomElement {
    public static final String TAG_NAME = "description";

    String getValue();
    void setValue(String value);
}
