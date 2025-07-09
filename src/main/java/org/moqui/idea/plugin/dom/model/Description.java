package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;

public interface Description extends DomElement {
    String TAG_NAME = "description";

    String getValue();
    void setValue(String value);
}
