package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//@Stubbed
public interface Emecas extends DomElement {

    String TAG_NAME = "emecas";

    String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/email-eca-3.xsd";
    @NotNull
    @SubTagList(Emeca.TAG_NAME)
    List<Emeca> getEmecaList();
}
