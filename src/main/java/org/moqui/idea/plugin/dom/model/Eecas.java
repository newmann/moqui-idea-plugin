package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//@Stubbed
public interface Eecas extends DomElement {

    String TAG_NAME = "eecas";

    String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/entity-eca-3.xsd";
    @NotNull
    @SubTagList(Eeca.TAG_NAME)
    List<Eeca> getEecaList();
}
