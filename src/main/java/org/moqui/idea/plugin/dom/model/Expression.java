package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;

public interface Expression extends DomElement {
    String TAG_NAME = "expression";

    String getValue();
}
