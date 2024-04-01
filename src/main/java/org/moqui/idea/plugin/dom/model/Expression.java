package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;

public interface Expression extends DomElement {
    public static final String TAG_NAME = "expression";

    String getValue();
}
