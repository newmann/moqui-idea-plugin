package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//@Stubbed
public interface Secas extends DomElement {
    
    String TAG_NAME = "secas";
    
    String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/service-eca-3.xsd";

    @NotNull
    @SubTagList(Seca.TAG_NAME)
    List<Seca> getSecaList();
}
