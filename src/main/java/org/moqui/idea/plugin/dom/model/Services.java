package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.ServicesPresentationProvider;

import java.util.List;
//@Stubbed
@Presentation(provider = ServicesPresentationProvider.class)
public interface Services extends DomElement {

    String TAG_NAME = "services";

    String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/service-definition-3.xsd";
    @NotNull
    @SubTagList(Service.TAG_NAME)
    List<Service> getServiceList();

    @NotNull
    @SubTagList(ServiceInclude.TAG_NAME)
    List<ServiceInclude> getServiceIncludeList();

}
